export async function registerPlayer(db, uuid, name) {
	const trimmed = name.trim();
	await db
		.prepare(`
			INSERT INTO players (uuid, name, created_at, last_seen)
			VALUES (?, ?, ?, ?)
			ON CONFLICT(uuid) DO UPDATE SET
				name = excluded.name,
				last_seen = excluded.last_seen
		`)
		.bind(uuid, trimmed, Date.now(), Date.now())
		.run();

	return { ok: true, created: true };
}

export async function findPlayerByName(db, name) {
	return db
		.prepare(`SELECT uuid, name, presence FROM players WHERE LOWER(name) = LOWER(?)`)
		.bind(name.trim())
		.first();
}

export async function createFriendRequest(db, from, to) {
	const existing = await db
		.prepare(`
			SELECT status FROM friend_relations
			WHERE (sender_uuid = ? AND receiver_uuid = ?)
			   OR (sender_uuid = ? AND receiver_uuid = ?)
		`)
		.bind(from, to, to, from)
		.first();

	if (existing) {
		if (existing.status === "ACCEPTED") {
			return { error: "already friends" };
		}
		if (existing.status === "PENDING") {
			return { error: "request already exists" };
		}
	}

	await db
		.prepare(`
			INSERT INTO friend_relations (sender_uuid, receiver_uuid, status, created_at)
			VALUES (?, ?, 'PENDING', ?)
		`)
		.bind(from, to, Date.now())
		.run();

	return { ok: true };
}

export async function acceptFriendRequest(db, friend, uuid) {
	const result = await db
		.prepare(`
			UPDATE friend_relations
			SET status = 'ACCEPTED'
			WHERE sender_uuid = ? AND receiver_uuid = ? AND status = 'PENDING'
		`)
		.bind(friend, uuid)
		.run();

	if (result.changes === 0) {
		return { error: "no pending request found" };
	}

	return { ok: true };
}

export async function removeFriend(db, uuid, friend) {
	const result = await db
		.prepare(`
			DELETE FROM friend_relations
			WHERE (sender_uuid = ? AND receiver_uuid = ?)
			   OR (sender_uuid = ? AND receiver_uuid = ?)
		`)
		.bind(uuid, friend, friend, uuid)
		.run();

	if (result.changes === 0) {
		return { error: "not found" };
	}

	return { ok: true };
}

export async function getFriends(db, uuid) {
	const relations = await db
		.prepare(`
			SELECT sender_uuid, receiver_uuid
			FROM friend_relations
			WHERE (sender_uuid = ? OR receiver_uuid = ?) AND status = 'ACCEPTED'
		`)
		.bind(uuid, uuid)
		.all();

	const friends = [];
	for (const r of relations.results || []) {
		const friendId = r.sender_uuid === uuid ? r.receiver_uuid : r.sender_uuid;

		const player = await db
			.prepare(`SELECT uuid, name, presence FROM players WHERE uuid = ?`)
			.bind(friendId)
			.first();

		friends.push({
			uuid: friendId,
			name: player?.name || "Unknown",
			presence: player?.presence || "OFFLINE"
		});
	}

	return friends;
}

export async function getPendingRequests(db, uuid) {
	const [incoming, outgoing] = await Promise.all([
		db
			.prepare(`
				SELECT fr.sender_uuid AS uuid, fr.created_at AS createdAt, p.name
				FROM friend_relations fr
				JOIN players p ON p.uuid = fr.sender_uuid
				WHERE fr.receiver_uuid = ? AND fr.status = 'PENDING'
				ORDER BY fr.created_at DESC
			`)
			.bind(uuid)
			.all(),

		db
			.prepare(`
				SELECT fr.receiver_uuid AS uuid, fr.created_at AS createdAt, p.name
				FROM friend_relations fr
				JOIN players p ON p.uuid = fr.receiver_uuid
				WHERE fr.sender_uuid = ? AND fr.status = 'PENDING'
				ORDER BY fr.created_at DESC
			`)
			.bind(uuid)
			.all()
	]);

	return {
		incoming: incoming.results || [],
		outgoing: outgoing.results || []
	};
}
