package com.community.elderlycare.repository;

import com.community.elderlycare.model.Elder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ElderRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Elder> mapper = (rs, i) -> {
        Elder e = new Elder();
        e.setId(rs.getLong("id"));
        e.setName(rs.getString("name"));
        e.setGender(rs.getString("gender"));
        e.setAge(rs.getInt("age"));
        e.setPhone(rs.getString("phone"));
        e.setAddress(rs.getString("address"));
        e.setEmergencyContact(rs.getString("emergency_contact"));
        e.setEmergencyPhone(rs.getString("emergency_phone"));
        String ca = rs.getString("created_at");
        if (ca != null) e.setCreatedAt(java.time.LocalDateTime.parse(ca.replace(" ", "T")));
        return e;
    };

    public ElderRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Elder> findAll() {
        return jdbc.query("SELECT * FROM elder ORDER BY id DESC", mapper);
    }

    public Elder findById(Long id) {
        return jdbc.queryForObject("SELECT * FROM elder WHERE id=?", mapper, id);
    }

    public Elder save(Elder e) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO elder(name,gender,age,phone,address,emergency_contact,emergency_phone) VALUES(?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, e.getName());
            ps.setString(2, e.getGender());
            ps.setInt(3, e.getAge());
            ps.setString(4, e.getPhone());
            ps.setString(5, e.getAddress());
            ps.setString(6, e.getEmergencyContact());
            ps.setString(7, e.getEmergencyPhone());
            return ps;
        }, kh);
        e.setId(kh.getKey().longValue());
        return e;
    }

    public void update(Elder e) {
        jdbc.update("UPDATE elder SET name=?,gender=?,age=?,phone=?,address=?,emergency_contact=?,emergency_phone=? WHERE id=?",
            e.getName(), e.getGender(), e.getAge(), e.getPhone(), e.getAddress(),
            e.getEmergencyContact(), e.getEmergencyPhone(), e.getId());
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM elder WHERE id=?", id);
    }

    public long count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM elder", Long.class);
    }
}
