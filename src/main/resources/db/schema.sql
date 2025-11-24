CREATE TABLE IF NOT EXISTS Audio (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT,
    format TEXT,
    path TEXT
);

CREATE TABLE IF NOT EXISTS Segment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    audio_id INTEGER,
    beginning_ms INTEGER,
    end_ms INTEGER,
    label TEXT,
    FOREIGN KEY (audio_id) REFERENCES Audio(id)
);

CREATE TABLE IF NOT EXISTS Workspace (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT,
    created_at DATETIME DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS Workspace_Audio (
    workspace_id INTEGER,
    audio_id INTEGER,
    PRIMARY KEY (workspace_id, audio_id),
    FOREIGN KEY (workspace_id) REFERENCES Workspace(id),
    FOREIGN KEY (audio_id) REFERENCES Audio(id)
);

