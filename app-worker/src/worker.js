const corsHeaders = {
	"Access-Control-Allow-Origin": "*",
	"Access-Control-Allow-Headers": "Content-Type",
	"Access-Control-Allow-Methods": "GET,POST,OPTIONS",
	"Content-Type": "application/json"
};

const json = (data, status = 200) =>
	new Response(JSON.stringify(data), { status, headers: corsHeaders });

/* =====================================================
   D U R A B L E   O B J E C T
===================================================== */
export class FriendsHub {
	constructor(state, env) {
		this.state = state;
		this.env = env;

		// uuid -> websocket
		this.sessions = new Map();

		// uuid -> last activity timestamp
		this.lastSeen = new Map();

		// HEARTBEAT CLEANUP LOOP
		this.state.blockConcurrencyWhile(async () => {
			this.startHeartbeat();
		});
	}

	/* =====================================================
	   HEARTBEAT WATCHDOG
	===================================================== */
	startHeartbeat() {
		setInterval(async () => {
			const now = Date.now();

			for (const [uuid, last] of this.lastSeen.entries()) {
				if (now - last > 20000) { // 20s timeout

					const socket = this.sessions.get(uuid);

					if (socket) {
						try {
							socket.close();
						} catch {}
					}

					this.sessions.delete(uuid);
					this.lastSeen.delete(uuid);

					// mark OFFLINE in DB
					await this.env.DB.prepare(`
						UPDATE players
						SET presence = 'OFFLINE',
						    last_seen = ?
						WHERE uuid = ?
					`).bind(now, uuid).run();
				}
			}
		}, 30000);
	}

	/* =====================================================
	   MAIN FETCH (WS UPGRADE)
	===================================================== */
	async fetch(request) {
		const url = new URL(request.url);

		if (url.pathname !== "/ws") {
			return new Response("Not WebSocket route", { status: 404 });
		}

		if (request.headers.get("Upgrade") !== "websocket") {
			return new Response("Expected websocket", { status: 426 });
		}

		const pair = new WebSocketPair();
		const client = pair[0];
		const server = pair[1];

		server.accept();

		const connId = crypto.randomUUID();

		server.send(JSON.stringify({
			type: "connected",
			connId
		}));

		/* =====================================================
		   MESSAGE HANDLER
		===================================================== */
		server.addEventListener("message", async (msg) => {
			try {
				const data = JSON.parse(msg.data);

				// update activity if uuid exists
				if (data.uuid) {
					this.lastSeen.set(data.uuid, Date.now());
				}

				/* ---------------- LOGIN ---------------- */
				if (data.type === "login") {
					const uuid = data.uuid;
					if (!uuid) return;

					this.sessions.set(uuid, server);
					this.lastSeen.set(uuid, Date.now());

					await this.env.DB.prepare(`
						UPDATE players
						SET presence = 'ONLINE',
						    last_seen = ?
						WHERE uuid = ?
					`).bind(Date.now(), uuid).run();

					server.send(JSON.stringify({
						type: "login_ok",
						uuid
					}));

					return;
				}

				/* ---------------- PING ---------------- */
				if (data.type === "ping") {
					server.send(JSON.stringify({
						type: "pong",
						time: Date.now()
					}));
					return;
				}

				/* ---------------- FRIEND REQUEST ---------------- */
				if (data.type === "friend_request") {
					const target = this.sessions.get(data.to);

					if (target) {
						target.send(JSON.stringify({
							type: "friend_request",
							from: data.from
						}));
					}
					return;
				}

				/* ---------------- FRIEND ACCEPT ---------------- */
				if (data.type === "friend_accept") {
					const a = this.sessions.get(data.from);
					const b = this.sessions.get(data.to);

					if (a) {
						a.send(JSON.stringify({
							type: "friend_accepted",
							from: data.to
						}));
					}

					if (b) {
						b.send(JSON.stringify({
							type: "friend_accepted",
							from: data.from
						}));
					}

					return;
				}

				/* ---------------- FRIEND REMOVE ---------------- */
				if (data.type === "friend_remove") {
					const a = this.sessions.get(data.from);
					const b = this.sessions.get(data.to);

					if (a) {
						a.send(JSON.stringify({
							type: "friend_removed",
							from: data.to
						}));
					}

					if (b) {
						b.send(JSON.stringify({
							type: "friend_removed",
							from: data.from
						}));
					}

					return;
				}

			} catch (err) {
				server.send(JSON.stringify({
					type: "error",
					message: "invalid json"
				}));
			}
		});

		/* =====================================================
		   CLEAN DISCONNECT
		===================================================== */
		server.addEventListener("close", () => {
			this.handleDisconnect(server);
		});

		return new Response(null, {
			status: 101,
			webSocket: client
		});
	}

	/* =====================================================
	   DISCONNECT HANDLER
	===================================================== */
	async handleDisconnect(server) {
		for (const [uuid, socket] of this.sessions.entries()) {
			if (socket === server) {
				this.sessions.delete(uuid);
				this.lastSeen.delete(uuid);

				const now = Date.now();

				await this.env.DB.prepare(`
					UPDATE players
					SET presence = 'OFFLINE',
					    last_seen = ?
					WHERE uuid = ?
				`).bind(now, uuid).run();

				break;
			}
		}
	}
}

/* =====================================================
   M A I N   W O R K E R
===================================================== */
export default {

	async fetch(request, env) {

		const url = new URL(request.url);

		if (request.method === "OPTIONS") {
			return new Response(null, { headers: corsHeaders });
		}

		/* =====================================================
		   WS ROUTE
		===================================================== */
		if (url.pathname === "/ws") {
			const id = env.FRIENDS_HUB.idFromName("global");
			const stub = env.FRIENDS_HUB.get(id);
			return stub.fetch(request);
		}

		/* =====================================================
		   REGISTER
		===================================================== */
		if (url.pathname === "/register" && request.method === "POST") {

			const body = await request.json().catch(() => ({}));

			if (!body.uuid || !body.name) {
				return json({ error: "missing uuid or name" }, 400);
			}

			await env.DB
				.prepare(`
					INSERT INTO players (uuid, name, created_at, last_seen)
					VALUES (?, ?, ?, ?)
					ON CONFLICT(uuid) DO UPDATE SET
						name = excluded.name,
						last_seen = excluded.last_seen
				`)
				.bind(body.uuid, body.name.trim(), Date.now(), Date.now())
				.run();

			return json({ ok: true });
		}

		/* =====================================================
		   FRIEND REQUEST (HTTP + WS TRIGGER)
		===================================================== */
		if (url.pathname === "/friend/request" && request.method === "POST") {

			const body = await request.json().catch(() => ({}));

			await env.DB
				.prepare(`
					INSERT INTO friend_relations
						(sender_uuid, receiver_uuid, status, created_at)
					VALUES (?, ?, 'PENDING', ?)
				`)
				.bind(body.from, body.to, Date.now())
				.run();

			// WS notify
			const hub = env.FRIENDS_HUB.get(env.FRIENDS_HUB.idFromName("global"));

			const socket = hub.sessions?.get(body.to);
			if (socket) {
				socket.send(JSON.stringify({
					type: "friend_request",
					from: body.from
				}));
			}

			return json({ ok: true });
		}

		/* =====================================================
		   ACCEPT FRIEND REQUEST
		===================================================== */
		if (url.pathname === "/friend/accept" && request.method === "POST") {

			const body = await request.json().catch(() => ({}));

			await env.DB
				.prepare(`
					UPDATE friend_relations
					SET status = 'ACCEPTED'
					WHERE sender_uuid = ?
					  AND receiver_uuid = ?
					  AND status = 'PENDING'
				`)
				.bind(body.friend, body.uuid)
				.run();

			const hub = env.FRIENDS_HUB.get(env.FRIENDS_HUB.idFromName("global"));

			const a = hub.sessions?.get(body.friend);
			const b = hub.sessions?.get(body.uuid);

			if (a) a.send(JSON.stringify({
				type: "friend_accepted",
				from: body.uuid
			}));

			if (b) b.send(JSON.stringify({
				type: "friend_accepted",
				from: body.friend
			}));

			return json({ ok: true });
		}

		/* =====================================================
		   REMOVE FRIEND
		===================================================== */
		if (url.pathname === "/friend/remove" && request.method === "POST") {

			const body = await request.json().catch(() => ({}));

			await env.DB
				.prepare(`
					DELETE FROM friend_relations
					WHERE (
						sender_uuid = ? AND receiver_uuid = ?
					) OR (
						sender_uuid = ? AND receiver_uuid = ?
					)
				`)
				.bind(body.uuid, body.friend, body.friend, body.uuid)
				.run();

			const hub = env.FRIENDS_HUB.get(env.FRIENDS_HUB.idFromName("global"));

			const a = hub.sessions?.get(body.uuid);
			const b = hub.sessions?.get(body.friend);

			if (a) a.send(JSON.stringify({
				type: "friend_removed",
				from: body.friend
			}));

			if (b) b.send(JSON.stringify({
				type: "friend_removed",
				from: body.uuid
			}));

			return json({ ok: true });
		}

		/* =====================================================
		   FRIEND LIST
		===================================================== */
		if (url.pathname === "/friends" && request.method === "GET") {

			const uuid = url.searchParams.get("uuid");

			const relations = await env.DB
				.prepare(`
					SELECT sender_uuid, receiver_uuid, status
					FROM friend_relations
					WHERE sender_uuid = ? OR receiver_uuid = ?
				`)
				.bind(uuid, uuid)
				.all();

			const friendList = [];

			for (const r of relations.results || []) {

				if (r.status !== "ACCEPTED") continue;

				const friendId =
					r.sender_uuid === uuid ? r.receiver_uuid : r.sender_uuid;

				const p = await env.DB
					.prepare(`
						SELECT uuid, name, presence
						FROM players
						WHERE uuid = ?
					`)
					.bind(friendId)
					.first();

				friendList.push({
					uuid: friendId,
					name: p?.name || "Unknown",
					presence: p?.presence || "OFFLINE"
				});
			}

			return json({
				uuid,
				friends: friendList
			});
		}

		return new Response("Not found", { status: 404, headers: corsHeaders });
	}
};
