export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    const headers = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "Content-Type",
      "Access-Control-Allow-Methods": "GET,POST,OPTIONS",
      "Content-Type": "application/json"
    };

    if (request.method === "OPTIONS") {
      return new Response(null, { headers });
    }

    const json = (body, status = 200) =>
      new Response(JSON.stringify(body), { status, headers });

    const now = () => Date.now();

    // ---------- NAME NORMALIZATION ----------
    const normalizeName = (name) => name.trim().toLowerCase();

    const nameKey = (name) => `name:${normalizeName(name)}`;
    const playerKey = (uuid) => `player:${uuid}`;

    // ---------- HELPERS ----------
    const getPlayer = async (uuid) => {
      if (!uuid) return null;
      const raw = await env.KV.get(playerKey(uuid));
      return raw ? JSON.parse(raw) : null;
    };

    const savePlayer = async (p) => {
      await env.KV.put(playerKey(p.uuid), JSON.stringify(p));
    };

    const normalize = (arr) => (Array.isArray(arr) ? arr : []);
    const dedupe = (arr) => [...new Set(arr)];
    const capBadge = (n) => Math.min(n, 5);

    const isOnline = (p) =>
      p?.lastSeen && now() - p.lastSeen < 5 * 60 * 1000;

    const presence = (p) => {
      if (!p) return "OFFLINE";
      if (!isOnline(p)) return "OFFLINE";
      if (p.joinableWorld) return "JOINABLE_WORLD";
      if (p.inWorld) return "IN_WORLD";
      return "ONLINE";
    };

    const updateNameIndex = async (oldName, newName, uuid) => {
      if (oldName) {
        await env.KV.delete(nameKey(oldName));
      }
      await env.KV.put(nameKey(newName), uuid);
    };

    // ---------- ROOT ----------
    if (url.pathname === "/") {
      return json({ status: "ONLINE" });
    }

    // ---------- REGISTER ----------
    if (url.pathname === "/register" && request.method === "POST") {
      const { uuid, name } = await request.json().catch(() => ({}));

      if (!uuid || !name) {
        return json({ error: "missing uuid or name" }, 400);
      }

      let player = await getPlayer(uuid);
      const existed = !!player;

      const cleanName = name.trim();

      if (!player) {
        player = {
          uuid,
          name: cleanName,
          friends: [],
          requests: [],
          outgoingRequests: [],
          blocked: [],
          allowRequests: true,
          inWorld: false,
          joinableWorld: false,
          lastSeen: now(),
          createdAt: now()
        };
      } else {
        const oldName = player.name;
        player.name = cleanName;
        player.lastSeen = now();

        await updateNameIndex(oldName, cleanName, uuid);
      }

      await env.KV.put(nameKey(cleanName), uuid);
      await savePlayer(player);

      return json({ ok: true, created: !existed });
    }

    // ---------- RESOLVE NAME ----------
    const resolveName = async (name) => {
      if (!name) return null;

      const uuid = await env.KV.get(nameKey(name));
      if (!uuid) return null;

      return getPlayer(uuid);
    };

    // ---------- FRIEND REQUEST ----------
    if (url.pathname === "/friend/request" && request.method === "POST") {
      const { from, to } = await request.json().catch(() => ({}));

      const a = await getPlayer(from);
      const b = await getPlayer(to);

      if (!a || !b) return json({ error: "not found" }, 404);

      if (normalize(a.friends).includes(to)) {
        return json({ error: "already friends" }, 409);
      }

      b.requests = dedupe([...normalize(b.requests), from]);
      a.outgoingRequests = dedupe([...normalize(a.outgoingRequests), to]);

      await savePlayer(a);
      await savePlayer(b);

      return json({ ok: true });
    }

    // ---------- FRIEND REQUEST BY NAME ----------
    if (url.pathname === "/friend/request/name" && request.method === "POST") {
      const { from, toName } = await request.json().catch(() => ({}));

      const a = await getPlayer(from);
      const b = await resolveName(toName);

      if (!a || !b) return json({ error: "not found" }, 404);

      if (a.friends.includes(b.uuid)) {
        return json({ error: "already friends" }, 409);
      }

      b.requests = dedupe([...normalize(b.requests), a.uuid]);
      a.outgoingRequests = dedupe([...normalize(a.outgoingRequests), b.uuid]);

      await savePlayer(a);
      await savePlayer(b);

      return json({ ok: true });
    }

    // ---------- FRIENDS ----------
    if (url.pathname === "/friends" && request.method === "GET") {
      const uuid = url.searchParams.get("uuid");
      const player = await getPlayer(uuid);

      if (!player) return json({});

      const friends = await Promise.all(
        normalize(player.friends).map(async (id) => {
          const f = await getPlayer(id);
          return {
            uuid: id,
            name: f?.name || id,
            presence: presence(f)
          };
        })
      );

      const incoming = await Promise.all(
        normalize(player.requests).map(async (id) => {
          const p = await getPlayer(id);
          return {
            uuid: id,
            name: p?.name || id,
            direction: "incoming",
            presence: presence(p)
          };
        })
      );

      const outgoing = await Promise.all(
        normalize(player.outgoingRequests).map(async (id) => {
          const p = await getPlayer(id);
          return {
            uuid: id,
            name: p?.name || id,
            direction: "outgoing",
            presence: presence(p)
          };
        })
      );

      return json({
        uuid: player.uuid,
        name: player.name,
        presence: presence(player),
        friends,
        requests: incoming,
        outgoingRequests: outgoing,
        incomingBadge: capBadge(normalize(player.requests).length)
      });
    }

    // ---------- ACCEPT ----------
    if (url.pathname === "/friend/accept" && request.method === "POST") {
      const { uuid, friend } = await request.json().catch(() => ({}));

      const a = await getPlayer(uuid);
      const b = await getPlayer(friend);

      if (!a || !b) return json({ error: "not found" }, 404);

      a.requests = normalize(a.requests).filter(x => x !== friend);
      a.outgoingRequests = normalize(a.outgoingRequests).filter(x => x !== friend);

      b.requests = normalize(b.requests).filter(x => x !== uuid);
      b.outgoingRequests = normalize(b.outgoingRequests).filter(x => x !== uuid);

      a.friends = dedupe([...normalize(a.friends), friend]);
      b.friends = dedupe([...normalize(b.friends), uuid]);

      await savePlayer(a);
      await savePlayer(b);

      return json({ ok: true });
    }

    // ---------- DECLINE ----------
    if (url.pathname === "/friend/decline" && request.method === "POST") {
      const { uuid, friend } = await request.json().catch(() => ({}));

      const a = await getPlayer(uuid);
      const b = await getPlayer(friend);

      if (!a || !b) return json({ error: "not found" }, 404);

      a.requests = normalize(a.requests).filter(x => x !== friend);
      b.outgoingRequests = normalize(b.outgoingRequests).filter(x => x !== uuid);

      await savePlayer(a);
      await savePlayer(b);

      return json({ ok: true });
    }

    // ---------- CANCEL ----------
    if (url.pathname === "/friend/cancel" && request.method === "POST") {
      const { uuid, friend } = await request.json().catch(() => ({}));

      const a = await getPlayer(uuid);
      const b = await getPlayer(friend);

      if (!a || !b) return json({ error: "not found" }, 404);

      a.outgoingRequests = normalize(a.outgoingRequests).filter(x => x !== friend);
      b.requests = normalize(b.requests).filter(x => x !== uuid);

      await savePlayer(a);
      await savePlayer(b);

      return json({ ok: true });
    }

    // ---------- REMOVE FRIEND ----------
    if (url.pathname === "/friend/remove" && request.method === "POST") {
      const { uuid, friend } = await request.json().catch(() => ({}));

      const a = await getPlayer(uuid);
      const b = await getPlayer(friend);

      if (!a || !b) return json({ error: "not found" }, 404);

      a.friends = normalize(a.friends).filter(x => x !== friend);
      b.friends = normalize(b.friends).filter(x => x !== uuid);

      await savePlayer(a);
      await savePlayer(b);

      return json({ ok: true });
    }

    // ---------- STATUS ----------
    if (url.pathname === "/status" && request.method === "POST") {
      const body = await request.json().catch(() => ({}));

      const p = await getPlayer(body.uuid);
      if (!p) return json({ error: "not found" }, 404);

      p.lastSeen = now();

      if (typeof body.inWorld === "boolean") p.inWorld = body.inWorld;
      if (typeof body.joinableWorld === "boolean") p.joinableWorld = body.joinableWorld;

      await savePlayer(p);

      return json({ ok: true });
    }

    return new Response("Not found", { status: 404, headers });
  }
};