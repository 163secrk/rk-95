package com.community.elderlycare.repository;

import com.community.elderlycare.model.MealReservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class MealReservationRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<MealReservation> mapper = (rs, i) -> {
        MealReservation m = new MealReservation();
        m.setId(rs.getLong("id"));
        m.setElderId(rs.getLong("elder_id"));
        m.setMealDate(rs.getString("meal_date"));
        m.setMealType(rs.getString("meal_type"));
        m.setMenuItem(rs.getString("menu_item"));
        m.setStatus(rs.getString("status"));
        m.setRemark(rs.getString("remark"));
        String ca = rs.getString("created_at");
        if (ca != null) m.setCreatedAt(java.time.LocalDateTime.parse(ca.replace(" ", "T")));
        return m;
    };

    public MealReservationRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<MealReservation> findAll() {
        return jdbc.query(
            "SELECT m.*, e.name as elder_name FROM meal_reservation m LEFT JOIN elder e ON m.elder_id=e.id ORDER BY m.id DESC",
            (rs, i) -> { MealReservation m = mapper.mapRow(rs, i); m.setElderName(rs.getString("elder_name")); return m; });
    }

    public MealReservation findById(Long id) {
        return jdbc.queryForObject(
            "SELECT m.*, e.name as elder_name FROM meal_reservation m LEFT JOIN elder e ON m.elder_id=e.id WHERE m.id=?",
            (rs, i) -> { MealReservation m = mapper.mapRow(rs, i); m.setElderName(rs.getString("elder_name")); return m; }, id);
    }

    public MealReservation save(MealReservation m) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO meal_reservation(elder_id,meal_date,meal_type,menu_item,status,remark) VALUES(?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, m.getElderId());
            ps.setString(2, m.getMealDate());
            ps.setString(3, m.getMealType());
            ps.setString(4, m.getMenuItem());
            ps.setString(5, m.getStatus() != null ? m.getStatus() : "已预约");
            ps.setString(6, m.getRemark());
            return ps;
        }, kh);
        m.setId(kh.getKey().longValue());
        return m;
    }

    public void update(MealReservation m) {
        jdbc.update("UPDATE meal_reservation SET elder_id=?,meal_date=?,meal_type=?,menu_item=?,status=?,remark=? WHERE id=?",
            m.getElderId(), m.getMealDate(), m.getMealType(), m.getMenuItem(), m.getStatus(), m.getRemark(), m.getId());
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM meal_reservation WHERE id=?", id);
    }

    public long countToday() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM meal_reservation WHERE meal_date=date('now','localtime')", Long.class);
    }
}
