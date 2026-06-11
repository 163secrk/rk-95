package com.community.elderlycare.repository;

import com.community.elderlycare.model.Volunteer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class VolunteerRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Volunteer> mapper = (rs, i) -> {
        Volunteer v = new Volunteer();
        v.setId(rs.getLong("id"));
        v.setName(rs.getString("name"));
        v.setPhone(rs.getString("phone"));
        v.setSkill(rs.getString("skill"));
        v.setStatus(rs.getString("status"));
        String ca = rs.getString("created_at");
        if (ca != null) v.setCreatedAt(java.time.LocalDateTime.parse(ca.replace(" ", "T")));
        return v;
    };

    public VolunteerRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Volunteer> findAll() {
        return jdbc.query("SELECT * FROM volunteer ORDER BY id DESC", mapper);
    }

    public List<Volunteer> findByStatus(String status) {
        return jdbc.query("SELECT * FROM volunteer WHERE status=?", mapper, status);
    }

    public Volunteer findById(Long id) {
        return jdbc.queryForObject("SELECT * FROM volunteer WHERE id=?", mapper, id);
    }

    public Volunteer save(Volunteer v) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO volunteer(name,phone,skill,status) VALUES(?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, v.getName());
            ps.setString(2, v.getPhone());
            ps.setString(3, v.getSkill());
            ps.setString(4, v.getStatus() != null ? v.getStatus() : "空闲");
            return ps;
        }, kh);
        v.setId(kh.getKey().longValue());
        return v;
    }

    public void update(Volunteer v) {
        jdbc.update("UPDATE volunteer SET name=?,phone=?,skill=?,status=? WHERE id=?",
            v.getName(), v.getPhone(), v.getSkill(), v.getStatus(), v.getId());
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM volunteer WHERE id=?", id);
    }

    public long count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM volunteer", Long.class);
    }
}
