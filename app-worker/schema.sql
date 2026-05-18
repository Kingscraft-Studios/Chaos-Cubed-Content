-- =========================================================
-- PLAYERS
-- =========================================================

CREATE TABLE IF NOT EXISTS players (
	uuid TEXT PRIMARY KEY,

	name TEXT NOT NULL UNIQUE,

	presence TEXT NOT NULL DEFAULT 'OFFLINE',

	in_world INTEGER NOT NULL DEFAULT 0,
	joinable_world INTEGER NOT NULL DEFAULT 0,

	allow_requests INTEGER NOT NULL DEFAULT 1,

	current_world TEXT,
	current_server TEXT,

	created_at INTEGER NOT NULL,
	last_seen INTEGER NOT NULL
);

-- =========================================================
-- FRIEND RELATIONSHIPS
-- =========================================================

CREATE TABLE IF NOT EXISTS friend_relations (
	id INTEGER PRIMARY KEY AUTOINCREMENT,

	sender_uuid TEXT NOT NULL,
	receiver_uuid TEXT NOT NULL,

	status TEXT NOT NULL,

	created_at INTEGER NOT NULL,

	UNIQUE(sender_uuid, receiver_uuid)
);

-- =========================================================
-- INDEXES
-- =========================================================

CREATE INDEX IF NOT EXISTS idx_players_name
	ON players(name);

CREATE INDEX IF NOT EXISTS idx_relation_sender
	ON friend_relations(sender_uuid);

CREATE INDEX IF NOT EXISTS idx_relation_receiver
	ON friend_relations(receiver_uuid);

CREATE INDEX IF NOT EXISTS idx_relation_status
	ON friend_relations(status);
