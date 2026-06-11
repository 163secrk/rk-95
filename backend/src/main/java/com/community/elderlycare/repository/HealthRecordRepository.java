package com.community.elderlycare.repository;

import com.community.elderlycare.model.HealthRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class HealthRecordRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<HealthRecord> mapper = (rs, i) -> {
        HealthRecord h = new HealthRecord();
        h.setId(rs.getLong("id"));
        h.setElderId(rs.getLong("elder_id"));
        h.setRecordDate(rs.getString("record_date"));
        h.setBloodPressure(rs.getString("blood_pressure"));
        h.setHeartRate(rs.getInt("heart_rate") == 0 ? null : rs.getInt("heart_rate"));
        h.setBloodSugar(rs.getString("blood_sugar"));
        h.setTemperature(rs.getString("temperature"));
        h.setMedication(rs.getString("medication"));
        h.setNotes(rs.getString("notes"));
        String ca = rs.getString("created_at");
        if (ca != null) h.setCreatedAt(java.time.LocalDateTime.parse(ca.replace(" ", "T")));
        return h;
    };

    public HealthRecordRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<HealthRecord> findAll() {
        return jdbc.query(
            "SELECT h.*, e.name as elder_name FROM health_record h LEFT JOIN elder e ON h.elder_id=e.id ORDER BY h.id DESC",
            (rs, i) -> { HealthRecord h = mapper.mapRow(rs, i); h.setElderName(rs.getString("elder_name")); return h; });
    }

    public HealthRecord findById(Long id) {
        return jdbc.queryForObject(
            "SELECT h.*, e.name as elder_name FROM health_record h LEFT JOIN elder e ON h.elder_id=e.id WHERE h.id=?",
            (rs, i) -> { HealthRecord h = mapper.mapRow(rs, i); h.setElderName(rs.getString("elder_name")); return h; }, id);
    }

    public HealthRecord save(HealthRecord h) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO health_record(elder_id,record_date,blood_pressure,heart_rate,blood_sugar,temperature,medication,notes) VALUES(?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, h.getElderId());
            ps.setString(2, h.getRecordDate());
            ps.setString(3, h.getBloodPressure());
            ps.setObject(4, h.getHeartRate());
            ps.setString(5, h.getBloodSugar());
            ps.setString(6, h.getTemperature());
            ps.setString(7, h.getMedication());
            ps.setString(8, h.getNotes());
            return ps;
        }, kh);
        h.setId(kh.getKey().longValue());
        return h;
    }

    public void update(HealthRecord h) {
        jdbc.update("UPDATE health_record SET elder_id=?,record_date=?,blood_pressure=?,heart_rate=?,blood_sugar=?,temperature=?,medication=?,notes=? WHERE id=?",
            h.getElderId(), h.getRecordDate(), h.getBloodPressure(), h.getHeartRate(), h.getBloodSugar(),
            h.getTemperature(), h.getMedication(), h.getNotes(), h.getId());
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM health_record WHERE id=?", id);
    }

    public long countToday() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM health_record WHERE record_date=date('now','localtime')", Long.class);
    }
}
