package com.community.elderlycare.repository;

import com.community.elderlycare.model.DispatchOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class DispatchOrderRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<DispatchOrder> mapper = (rs, i) -> {
        DispatchOrder d = new DispatchOrder();
        d.setId(rs.getLong("id"));
        d.setElderId(rs.getLong("elder_id"));
        long vid = rs.getLong("volunteer_id");
        d.setVolunteerId(rs.wasNull() ? null : vid);
        d.setServiceType(rs.getString("service_type"));
        d.setServiceDate(rs.getString("service_date"));
        d.setDescription(rs.getString("description"));
        d.setStatus(rs.getString("status"));
        String ca = rs.getString("created_at");
        if (ca != null) d.setCreatedAt(java.time.LocalDateTime.parse(ca.replace(" ", "T")));
        return d;
    };

    public DispatchOrderRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<DispatchOrder> findAll() {
        return jdbc.query(
            "SELECT d.*, e.name as elder_name, v.name as volunteer_name " +
            "FROM dispatch_order d LEFT JOIN elder e ON d.elder_id=e.id LEFT JOIN volunteer v ON d.volunteer_id=v.id ORDER BY d.id DESC",
            (rs, i) -> { DispatchOrder d = mapper.mapRow(rs, i); d.setElderName(rs.getString("elder_name")); d.setVolunteerName(rs.getString("volunteer_name")); return d; });
    }

    public DispatchOrder findById(Long id) {
        return jdbc.queryForObject(
            "SELECT d.*, e.name as elder_name, v.name as volunteer_name " +
            "FROM dispatch_order d LEFT JOIN elder e ON d.elder_id=e.id LEFT JOIN volunteer v ON d.volunteer_id=v.id WHERE d.id=?",
            (rs, i) -> { DispatchOrder d = mapper.mapRow(rs, i); d.setElderName(rs.getString("elder_name")); d.setVolunteerName(rs.getString("volunteer_name")); return d; }, id);
    }

    public DispatchOrder save(DispatchOrder d) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO dispatch_order(elder_id,volunteer_id,service_type,service_date,description,status) VALUES(?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, d.getElderId());
            ps.setObject(2, d.getVolunteerId());
            ps.setString(3, d.getServiceType());
            ps.setString(4, d.getServiceDate());
            ps.setString(5, d.getDescription());
            ps.setString(6, d.getStatus() != null ? d.getStatus() : "待派单");
            return ps;
        }, kh);
        d.setId(kh.getKey().longValue());
        return d;
    }

    public void update(DispatchOrder d) {
        jdbc.update("UPDATE dispatch_order SET elder_id=?,volunteer_id=?,service_type=?,service_date=?,description=?,status=? WHERE id=?",
            d.getElderId(), d.getVolunteerId(), d.getServiceType(), d.getServiceDate(), d.getDescription(), d.getStatus(), d.getId());
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM dispatch_order WHERE id=?", id);
    }

    public long countPending() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM dispatch_order WHERE status='待派单'", Long.class);
    }

    public List<DispatchOrder> findByVolunteerId(Long volunteerId) {
        return jdbc.query(
            "SELECT d.*, e.name as elder_name, v.name as volunteer_name " +
            "FROM dispatch_order d LEFT JOIN elder e ON d.elder_id=e.id LEFT JOIN volunteer v ON d.volunteer_id=v.id " +
            "WHERE d.volunteer_id=? ORDER BY d.id DESC",
            (rs, i) -> { DispatchOrder d = mapper.mapRow(rs, i); d.setElderName(rs.getString("elder_name")); d.setVolunteerName(rs.getString("volunteer_name")); return d; }, volunteerId);
    }

    public List<DispatchOrder> findByVolunteerIdAndStatus(Long volunteerId, String status) {
        return jdbc.query(
            "SELECT d.*, e.name as elder_name, v.name as volunteer_name " +
            "FROM dispatch_order d LEFT JOIN elder e ON d.elder_id=e.id LEFT JOIN volunteer v ON d.volunteer_id=v.id " +
            "WHERE d.volunteer_id=? AND d.status=? ORDER BY d.id DESC",
            (rs, i) -> { DispatchOrder d = mapper.mapRow(rs, i); d.setElderName(rs.getString("elder_name")); d.setVolunteerName(rs.getString("volunteer_name")); return d; }, volunteerId, status);
    }
}
