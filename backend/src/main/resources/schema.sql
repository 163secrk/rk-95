CREATE TABLE IF NOT EXISTS elder (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    gender TEXT NOT NULL,
    age INTEGER NOT NULL,
    phone TEXT,
    address TEXT,
    emergency_contact TEXT,
    emergency_phone TEXT,
    created_at TEXT DEFAULT (datetime('now','localtime'))
);

CREATE TABLE IF NOT EXISTS meal_reservation (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    elder_id INTEGER NOT NULL,
    meal_date TEXT NOT NULL,
    meal_type TEXT NOT NULL,
    menu_item TEXT,
    status TEXT DEFAULT '已预约',
    remark TEXT,
    created_at TEXT DEFAULT (datetime('now','localtime')),
    FOREIGN KEY (elder_id) REFERENCES elder(id)
);

CREATE TABLE IF NOT EXISTS health_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    elder_id INTEGER NOT NULL,
    record_date TEXT NOT NULL,
    blood_pressure TEXT,
    heart_rate INTEGER,
    blood_sugar TEXT,
    temperature TEXT,
    medication TEXT,
    notes TEXT,
    created_at TEXT DEFAULT (datetime('now','localtime')),
    FOREIGN KEY (elder_id) REFERENCES elder(id)
);

CREATE TABLE IF NOT EXISTS volunteer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    skill TEXT,
    status TEXT DEFAULT '空闲',
    created_at TEXT DEFAULT (datetime('now','localtime'))
);

CREATE TABLE IF NOT EXISTS dispatch_order (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    elder_id INTEGER NOT NULL,
    volunteer_id INTEGER,
    service_type TEXT NOT NULL,
    service_date TEXT NOT NULL,
    description TEXT,
    status TEXT DEFAULT '待派单',
    service_start_time TEXT,
    service_end_time TEXT,
    actual_duration INTEGER DEFAULT 0,
    rating INTEGER,
    review_comment TEXT,
    created_at TEXT DEFAULT (datetime('now','localtime')),
    FOREIGN KEY (elder_id) REFERENCES elder(id),
    FOREIGN KEY (volunteer_id) REFERENCES volunteer(id)
);

CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL,
    volunteer_id INTEGER,
    name TEXT,
    created_at TEXT DEFAULT (datetime('now','localtime')),
    FOREIGN KEY (volunteer_id) REFERENCES volunteer(id)
);
