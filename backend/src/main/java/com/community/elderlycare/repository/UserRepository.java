package com.community.elderlycare.repository;

import com.community.elderlycare.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> mapper = (rs, i) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        long vid = rs.getLong("volunteer_id");
        u.setVolunteerId(rs.wasNull() ? null : vid);
        u.setName(rs.getString("name"));
        String ca = rs.getString("created_at");
        if (ca != null) u.setCreatedAt(java.time.LocalDateTime.parse(ca.replace(" ", "T")));
        return u;
    };

    public UserRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public User findByUsername(String username) {
        List<User> list = jdbc.query("SELECT * FROM user WHERE username=?", mapper, username);
        return list.isEmpty() ? null : list.get(0);
    }

    public User findById(Long id) {
        return jdbc.queryForObject("SELECT * FROM user WHERE id=?", mapper, id);
    }

    public List<User> findAll() {
        return jdbc.query("SELECT * FROM user ORDER BY id DESC", mapper);
    }

    public User save(User u) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO user(username,password,role,volunteer_id,name) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRole());
            ps.setObject(4, u.getVolunteerId());
            ps.setString(5, u.getName());
            return ps;
        }, kh);
        u.setId(kh.getKey().longValue());
        return u;
    }

    public void update(User u) {
        jdbc.update("UPDATE user SET username=?,password=?,role=?,volunteer_id=?,name=? WHERE id=?",
            u.getUsername(), u.getPassword(), u.getRole(), u.getVolunteerId(), u.getName(), u.getId());
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM user WHERE id=?", id);
    }

    public boolean existsByUsername(String username) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM user WHERE username=?", Integer.class, username) > 0;
    }
}
