package com.timetablegenerator.repository;

import com.timetablegenerator.model.role;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.util.dbConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class userRepo {

    private final Connection connection;

    public userRepo() {
        this.connection = dbConnection.getConnection();
    }

    public void create(userModel user) throws SQLException {
        String sql = "INSERT INTO users (name, phone, email, password, department_id, role, is_active, is_delete, created_by, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement st = this.connection.prepareStatement(sql)) {
            st.setString(1, user.getName());
            st.setString(2, user.getPhone());
            st.setString(3, user.getEmail());
            st.setString(4, user.getPassword());
            st.setInt(5, user.getDepartment_id());
            st.setString(6, user.getRole().name());
            st.setBoolean(7, true);
            st.setBoolean(8, false);
            st.setInt(9, user.getCreated_by());
            st.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
        }
    }

    public int getTotalCount(String search) throws Exception {
        String sql = "SELECT COUNT(*) FROM users WHERE (name LIKE ? OR email LIKE ?) AND is_delete = 0";
        try (PreparedStatement st = this.connection.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            st.setString(2, "%" + search + "%");
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<userModel> findAll(int limit, int offset, String search) throws SQLException {
        List<userModel> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_delete = false AND (name LIKE ? OR email LIKE ?) LIMIT ? OFFSET ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            st.setString(2, "%" + search + "%");
            st.setInt(3, limit);
            st.setInt(4, offset);
            ResultSet rs = st.executeQuery();
            while (rs.next()) { users.add(mapResultSetToModel(rs)); }
        }
        return users;
    }

    public List<userModel> findAllForDept() throws SQLException {
        List<userModel> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_delete = false AND is_active = true AND role='professor' AND department_id=0";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) { users.add(mapResultSetToModel(rs)); }
        }
        return users;
    }

    public List<userModel> findAllByDeptId(int id) throws SQLException {
        List<userModel> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE department_id = ? AND is_delete = false AND is_active=true AND role='professor'";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) { users.add(mapResultSetToModel(rs)); }
        }
        return users;
    }

    public void update(userModel user) throws SQLException {
        String sql = "UPDATE users SET name=?, phone=?, email=?, department_id=?, role=?, is_active=?, modify_by=?, modify_date=? WHERE id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, user.getName());
            st.setString(2, user.getPhone());
            st.setString(3, user.getEmail());
            st.setInt(4, user.getDepartment_id());
            st.setString(5, user.getRole().name());
            st.setBoolean(6, user.isIs_active());
            st.setInt(7, user.getModify_by());
            st.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            st.setInt(9, user.getId());
            st.executeUpdate();
        }
    }

    public void softDelete(int id) throws SQLException {
        String sql = "UPDATE users SET is_delete = true WHERE id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public userModel findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? AND is_delete = false";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapResultSetToModel(rs);
        }
        return null;
    }

    private userModel mapResultSetToModel(ResultSet rs) throws SQLException {
        userModel u = new userModel();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setDepartment_id(rs.getInt("department_id"));
        u.setRole(role.valueOf(rs.getString("role"))); // assuming stored as string
        u.setIs_active(rs.getBoolean("is_active"));
        u.setIs_delete(rs.getBoolean("is_delete"));
        u.setCreated_by(rs.getInt("created_by"));
        u.setCreated_date(rs.getTimestamp("created_date"));
        u.setModify_by(rs.getInt("modify_by"));
        u.setModify_date(rs.getTimestamp("modify_date"));
        return u;
    }
}
