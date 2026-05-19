import { FriendsHub } from "./friends-hub.js";
import { corsHeaders, json, error } from "./utils.js";
import { validateUUID, validateName, parseBody } from "./validators.js";
import * as db from "./db.js";

export { FriendsHub };

function getHubStub(env) {
	const id = env.FRIENDS_HUB.idFromName("global");
	return env.FRIENDS_HUB.get(id);
}

async function rpc(hub, path, body) {
	return hub.fetch(
		new Request(`http://do${path}`, {
			method: "POST",
			body: JSON.stringify(body)
		})
	);
}

export default {
	async fetch(request, env) {
		const url = new URL(request.url);

		/* ---- CORS preflight ---- */

		if (request.method === "OPTIONS") {
			return new Response(null, { headers: corsHeaders });
		}

		/* ---- WebSocket ---- */

		if (url.pathname === "/ws") {
			const stub = getHubStub(env);
			return stub.fetch(request);
		}

		/* ---- POST /register ---- */

		if (url.pathname === "/register" && request.method === "POST") {
			const body = await parseBody(request);
			if (!body) return error("invalid json", 400);

			if (!validateUUID(body.uuid)) {
				return error("invalid uuid", 400);
			}
			if (!validateName(body.name)) {
				return error("invalid name", 400);
			}

			try {
				const result = await db.registerPlayer(env.DB, body.uuid, body.name);
				return json(result);
			} catch (e) {
				return error("registration failed", 500);
			}
		}

		/* ---- POST /friend/request/name (by player name) ---- */

		if (url.pathname === "/friend/request/name" && request.method === "POST") {
			const body = await parseBody(request);
			if (!body) return error("invalid json", 400);

			if (!validateUUID(body.from)) {
				return error("invalid from uuid", 400);
			}
			if (!validateName(body.toName)) {
				return error("invalid target name", 400);
			}

			try {
				const target = await db.findPlayerByName(env.DB, body.toName);
				if (!target) {
					return error("player not found", 404);
				}

				const result = await db.createFriendRequest(
					env.DB,
					body.from,
					target.uuid
				);
				if (result.error) return error(result.error, 409);

				const hub = getHubStub(env);
				await rpc(hub, "/rpc/notify-request", {
					from: body.from,
					to: target.uuid
				});

				return json({ ok: true });
			} catch (e) {
				return error("friend request failed", 500);
			}
		}

		/* ---- POST /friend/request (by UUID) ---- */

		if (url.pathname === "/friend/request" && request.method === "POST") {
			const body = await parseBody(request);
			if (!body) return error("invalid json", 400);

			if (!validateUUID(body.from) || !validateUUID(body.to)) {
				return error("invalid uuid(s)", 400);
			}

			try {
				const result = await db.createFriendRequest(
					env.DB,
					body.from,
					body.to
				);
				if (result.error) return error(result.error, 409);

				const hub = getHubStub(env);
				await rpc(hub, "/rpc/notify-request", {
					from: body.from,
					to: body.to
				});

				return json({ ok: true });
			} catch (e) {
				return error("friend request failed", 500);
			}
		}

		/* ---- POST /friend/accept ---- */

		if (url.pathname === "/friend/accept" && request.method === "POST") {
			const body = await parseBody(request);
			if (!body) return error("invalid json", 400);

			if (!validateUUID(body.uuid) || !validateUUID(body.friend)) {
				return error("invalid uuid(s)", 400);
			}

			try {
				const result = await db.acceptFriendRequest(
					env.DB,
					body.friend,
					body.uuid
				);
				if (result.error) return error(result.error, 404);

				const hub = getHubStub(env);
				await rpc(hub, "/rpc/notify-accept", {
					a: body.friend,
					b: body.uuid
				});

				return json({ ok: true });
			} catch (e) {
				return error("accept failed", 500);
			}
		}

		/* ---- POST /friend/remove ---- */

		if (url.pathname === "/friend/remove" && request.method === "POST") {
			const body = await parseBody(request);
			if (!body) return error("invalid json", 400);

			if (!validateUUID(body.uuid) || !validateUUID(body.friend)) {
				return error("invalid uuid(s)", 400);
			}

			try {
				const result = await db.removeFriend(
					env.DB,
					body.uuid,
					body.friend
				);
				if (result.error) return error(result.error, 404);

				const hub = getHubStub(env);
				await rpc(hub, "/rpc/notify-remove", {
					a: body.uuid,
					b: body.friend
				});

				return json({ ok: true });
			} catch (e) {
				return error("remove failed", 500);
			}
		}

		/* ---- GET /friends ---- */

		if (url.pathname === "/friends" && request.method === "GET") {
			const uuid = url.searchParams.get("uuid");
			if (!validateUUID(uuid)) return error("invalid uuid", 400);

			try {
				const friends = await db.getFriends(env.DB, uuid);
				return json({ uuid, friends });
			} catch (e) {
				return error("failed to fetch friends", 500);
			}
		}

		/* ---- GET /friend/requests ---- */

		if (url.pathname === "/friend/requests" && request.method === "GET") {
			const uuid = url.searchParams.get("uuid");
			if (!validateUUID(uuid)) return error("invalid uuid", 400);

			try {
				const requests = await db.getPendingRequests(env.DB, uuid);
				return json({ uuid, ...requests });
			} catch (e) {
				return error("failed to fetch requests", 500);
			}
		}

		/* ---- GET /allow-requests ---- */

		if (url.pathname === "/allow-requests" && request.method === "GET") {
			const uuid = url.searchParams.get("uuid");
			if (!validateUUID(uuid)) return error("invalid uuid", 400);

			try {
				const allow = await db.getAllowRequests(env.DB, uuid);
				return json({ uuid, allow: allow === 1 || allow === true });
			} catch (e) {
				return error("failed to fetch allow-requests", 500);
			}
		}

		/* ---- POST /allow-requests ---- */

		if (url.pathname === "/allow-requests" && request.method === "POST") {
			const body = await parseBody(request);
			if (!body) return error("invalid json", 400);
			if (!validateUUID(body.uuid)) return error("invalid uuid", 400);
			if (typeof body.allow !== "boolean") return error("allow must be boolean", 400);

			try {
				await db.setAllowRequests(env.DB, body.uuid, body.allow);

				const hub = getHubStub(env);
				await rpc(hub, "/rpc/notify-allow-requests", {
					uuid: body.uuid,
					allow: body.allow
				});

				return json({ ok: true });
			} catch (e) {
				return error("failed to set allow-requests", 500);
			}
		}

		return error("Not found", 404);
	}
};
