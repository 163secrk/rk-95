package com.community.elderlycare.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DatabaseInitializer {
    private final JdbcTemplate jdbc;

    public DatabaseInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() throws IOException {
        try {
            Path dataDir = Path.of(System.getProperty("user.dir"), "data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create data directory", e);
        }

        jdbc.execute("CREATE TABLE IF NOT EXISTS elder (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "gender TEXT NOT NULL," +
            "age INTEGER NOT NULL," +
            "phone TEXT," +
            "address TEXT," +
            "emergency_contact TEXT," +
            "emergency_phone TEXT," +
            "created_at TEXT DEFAULT (datetime('now','localtime'))" +
            ")");

        jdbc.execute("CREATE TABLE IF NOT EXISTS meal_reservation (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "elder_id INTEGER NOT NULL," +
            "meal_date TEXT NOT NULL," +
            "meal_type TEXT NOT NULL," +
            "menu_item TEXT," +
            "status TEXT DEFAULT '已预约'," +
            "remark TEXT," +
            "created_at TEXT DEFAULT (datetime('now','localtime'))," +
            "FOREIGN KEY (elder_id) REFERENCES elder(id)" +
            ")");

        jdbc.execute("CREATE TABLE IF NOT EXISTS health_record (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "elder_id INTEGER NOT NULL," +
            "record_date TEXT NOT NULL," +
            "blood_pressure TEXT," +
            "heart_rate INTEGER," +
            "blood_sugar TEXT," +
            "temperature TEXT," +
            "medication TEXT," +
            "notes TEXT," +
            "created_at TEXT DEFAULT (datetime('now','localtime'))," +
            "FOREIGN KEY (elder_id) REFERENCES elder(id)" +
            ")");

        jdbc.execute("CREATE TABLE IF NOT EXISTS volunteer (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "phone TEXT NOT NULL," +
            "skill TEXT," +
            "status TEXT DEFAULT '空闲'," +
            "created_at TEXT DEFAULT (datetime('now','localtime'))" +
            ")");

        jdbc.execute("CREATE TABLE IF NOT EXISTS dispatch_order (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "elder_id INTEGER NOT NULL," +
            "volunteer_id INTEGER," +
            "service_type TEXT NOT NULL," +
            "service_date TEXT NOT NULL," +
            "description TEXT," +
            "status TEXT DEFAULT '待派单'," +
            "service_start_time TEXT," +
            "service_end_time TEXT," +
            "actual_duration INTEGER DEFAULT 0," +
            "rating INTEGER," +
            "review_comment TEXT," +
            "created_at TEXT DEFAULT (datetime('now','localtime'))," +
            "FOREIGN KEY (elder_id) REFERENCES elder(id)," +
            "FOREIGN KEY (volunteer_id) REFERENCES volunteer(id)" +
            ")");

        try {
            jdbc.execute("ALTER TABLE dispatch_order ADD COLUMN service_start_time TEXT");
        } catch (Exception e) {}
        try {
            jdbc.execute("ALTER TABLE dispatch_order ADD COLUMN service_end_time TEXT");
        } catch (Exception e) {}
        try {
            jdbc.execute("ALTER TABLE dispatch_order ADD COLUMN actual_duration INTEGER DEFAULT 0");
        } catch (Exception e) {}
        try {
            jdbc.execute("ALTER TABLE dispatch_order ADD COLUMN rating INTEGER");
        } catch (Exception e) {}
        try {
            jdbc.execute("ALTER TABLE dispatch_order ADD COLUMN review_comment TEXT");
        } catch (Exception e) {}

        jdbc.execute("CREATE TABLE IF NOT EXISTS user (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username TEXT NOT NULL UNIQUE," +
            "password TEXT NOT NULL," +
            "role TEXT NOT NULL," +
            "volunteer_id INTEGER," +
            "name TEXT," +
            "created_at TEXT DEFAULT (datetime('now','localtime'))," +
            "FOREIGN KEY (volunteer_id) REFERENCES volunteer(id)" +
            ")");

        Integer adminCount = jdbc.queryForObject("SELECT COUNT(*) FROM user WHERE username='admin'", Integer.class);
        if (adminCount == 0) {
            jdbc.update("INSERT INTO user(username, password, role, name) VALUES(?, ?, ?, ?)",
                "admin", "admin123", "admin", "系统管理员");
        }
    }
}
