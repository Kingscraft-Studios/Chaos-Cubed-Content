import { json, error } from "./utils.js";

const HEARTBEAT_INTERVAL = 30000;
const STALE_TIMEOUT = 20000;

export class FriendsHub {
	constructor(state, env) {
		this.state = state;
		this.env = env;

		this.sessions = new Map();
		this.lastSeen = new Map();

		this.state.blockConcurrencyWhile(() => {
			this.startHeartbeat();
		});
	}

	/* ---------- heartbeat watchdog ---------- */

	startHeartbeat() {
		setInterval(() => {
			const now = Date.now();

			for (const [uuid, last] of this.lastSeen) {
				if (now - last > STALE_TIMEOUT) {
					this.cleanupSession(uuid, now);
				}
			}
		}, HEARTBEAT_INTERVAL);
	}

	async cleanupSession(uuid, now) {
		const socket = this.sessions.get(uuid);
		if (socket) {
			try {
				socket.close();
			} catch {}
		}

		this.sessions.delete(uuid);
		this.lastSeen.delete(uuid);

		await this.env.DB.prepare(`
			UPDATE players
			SET presence = 'OFFLINE', last_seen = ?
			WHERE uuid = ?
		`).bind(now, uuid).run();
	}

	async handleDisconnect(server) {
		for (const [uuid, socket] of this.sessions) {
			if (socket === server) {
				await this.cleanupSession(uuid, Date.now());
				break;
			}
		}
	}

	/* ---------- WebSocket message handling ---------- */

	async handleWsMessage(server, data) {
		if (data.uuid) {
			this.lastSeen.set(data.uuid, Date.now());
		}

		switch (data.type) {
			case "login":
				return this.handleLogin(server, data);
			case "ping":
				return server.send(JSON.stringify({ type: "pong", time: Date.now() }));
			default:
				server.send(JSON.stringify({ type: "error", message: "unknown type" }));
		}
	}

	async handleLogin(server, data) {
		if (!data.uuid) return;

		this.sessions.set(data.uuid, server);
		this.lastSeen.set(data.uuid, Date.now());

		await this.env.DB.prepare(`
			UPDATE players
			SET presence = 'ONLINE', last_seen = ?
			WHERE uuid = ?
		`).bind(Date.now(), data.uuid).run();

		server.send(JSON.stringify({ type: "login_ok", uuid: data.uuid }));
	}

	/* ---------- WebSocket upgrade handler ---------- */

	handleWebSocket(request) {
		if (request.headers.get("Upgrade") !== "websocket") {
			return error("Expected websocket", 426);
		}

		const pair = new WebSocketPair();
		const client = pair[0];
		const server = pair[1];

		server.accept();

		const connId = crypto.randomUUID();
		server.send(JSON.stringify({ type: "connected", connId }));

		server.addEventListener("message", (msg) => {
			try {
				const data = JSON.parse(msg.data);
				this.handleWsMessage(server, data).catch(err =>
					server.send(JSON.stringify({ type: "error", message: "internal error" }))
				);
			} catch {
				server.send(JSON.stringify({ type: "error", message: "invalid json" }));
			}
		});

		server.addEventListener("close", () => {
			this.handleDisconnect(server);
		});

		return new Response(null, { status: 101, webSocket: client });
	}

	/* ---------- RPC methods (called via DO fetch sub-requests) ---------- */

	async notifyFriendRequest(from, to) {
		const target = this.sessions.get(to);
		if (target) {
			const fromPlayer = await this.env.DB
				.prepare("SELECT name FROM players WHERE uuid = ?")
				.bind(from).first();
			target.send(JSON.stringify({
				type: "friend_request",
				from,
				name: fromPlayer?.name || from
			}));
			return { notified: true };
		}
		return { notified: false };
	}

	async notifyFriendAccept(a, b) {
		const [playerA, playerB] = await Promise.all([
			this.env.DB.prepare("SELECT name FROM players WHERE uuid = ?").bind(a).first(),
			this.env.DB.prepare("SELECT name FROM players WHERE uuid = ?").bind(b).first()
		]);

		const sessionA = this.sessions.get(a);
		const sessionB = this.sessions.get(b);

		if (sessionA) {
			sessionA.send(JSON.stringify({
				type: "friend_accepted",
				from: b,
				name: playerB?.name || b
			}));
		}
		if (sessionB) {
			sessionB.send(JSON.stringify({
				type: "friend_accepted",
				from: a,
				name: playerA?.name || a
			}));
		}
	}

	async notifyFriendRemove(a, b) {
		const [playerA, playerB] = await Promise.all([
			this.env.DB.prepare("SELECT name FROM players WHERE uuid = ?").bind(a).first(),
			this.env.DB.prepare("SELECT name FROM players WHERE uuid = ?").bind(b).first()
		]);

		const sessionA = this.sessions.get(a);
		const sessionB = this.sessions.get(b);

		if (sessionA) {
			sessionA.send(JSON.stringify({
				type: "friend_removed",
				from: b,
				name: playerB?.name || b
			}));
		}
		if (sessionB) {
			sessionB.send(JSON.stringify({
				type: "friend_removed",
				from: a,
				name: playerA?.name || a
			}));
		}
	}

	async notifyAllowRequests(uuid, allow) {
		const target = this.sessions.get(uuid);
		if (target) {
			target.send(JSON.stringify({
				type: "allow_requests_updated",
				uuid,
				allow
			}));
			return { notified: true };
		}
		return { notified: false };
	}

	getOnlineStatus(uuid) {
		return { online: this.sessions.has(uuid) };
	}

	/* ---------- main DO fetch router ---------- */

	async fetch(request) {
		const url = new URL(request.url);

		if (url.pathname === "/ws") {
			return this.handleWebSocket(request);
		}

		switch (url.pathname) {
			case "/rpc/notify-request": {
				if (request.method !== "POST") break;
				const body = await request.json();
				return json(await this.notifyFriendRequest(body.from, body.to));
			}
			case "/rpc/notify-accept": {
				if (request.method !== "POST") break;
				const body = await request.json();
				await this.notifyFriendAccept(body.a, body.b);
				return json({ ok: true });
			}
			case "/rpc/notify-remove": {
				if (request.method !== "POST") break;
				const body = await request.json();
				await this.notifyFriendRemove(body.a, body.b);
				return json({ ok: true });
			}
			case "/rpc/online-status": {
				if (request.method !== "GET") break;
				const uuid = url.searchParams.get("uuid");
				if (!uuid) return error("missing uuid");
				return json(this.getOnlineStatus(uuid));
			}
			case "/rpc/notify-allow-requests": {
				if (request.method !== "POST") break;
				const body = await request.json();
				return json(await this.notifyAllowRequests(body.uuid, body.allow));
			}
		}

		return error("Not found", 404);
	}
}
