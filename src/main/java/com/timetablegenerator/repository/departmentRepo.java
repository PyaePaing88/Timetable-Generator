package com.timetablegenerator.repository;

import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class departmentRepo {
    private final Connection connection;

    public departmentRepo() {
        this.connection = dbConnection.getConnection();
    }

    public void create(departmentModel dept) throws SQLException {
        String sql = "INSERT INTO departments (department_name, is_delete, created_by, created_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement st = this.connection.prepareStatement(sql)) {
            st.setString(1, dept.getDepartment_name());
            st.setBoolean(2, false);
            st.setInt(3, dept.getCreated_by());
            st.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
        }
    }

    public int getTotalCount(String search) throws Exception {
        String sql = "SELECT COUNT(*) FROM departments WHERE (department_name LIKE ?) AND is_delete = 0";
        try (PreparedStatement st = this.connection.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<departmentModel> findAll(int limit, int offset, String search) throws SQLException {
        List<departmentModel> dept = new ArrayList<>();
        String sql = "SELECT * FROM departments WHERE is_delete = false AND (department_name LIKE ?) LIMIT ? OFFSET ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            st.setInt(2, limit);
            st.setInt(3, offset);
            ResultSet rs = st.executeQuery();
            while (rs.next()) { dept.add(mapResultSetToModel(rs)); }
        }
        return dept;
    }

    public List<departmentModel> findAllForCombo() throws SQLException {
        List<departmentModel> dept = new ArrayList<>();
        String sql = "SELECT * FROM departments WHERE is_delete = false";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) { dept.add(mapResultSetToModel(rs)); }
        }
        return dept;
    }

    public void update(departmentModel dept) throws SQLException {
        String sql = "UPDATE departments SET department_name=?, modify_by=?, modify_date=? WHERE id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, dept.getDepartment_name());
            st.setInt(2, dept.getModify_by());
            st.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            st.setInt(4, dept.getId());
            st.executeUpdate();
        }
    }

    public void softDelete(int id) throws SQLException {
        String sql = "UPDATE departments SET is_delete = true WHERE id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public departmentModel findById(int id) throws SQLException {
        String sql = "SELECT * FROM departments WHERE id = ? AND is_delete = false";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapResultSetToModel(rs);
        }
        return null;
    }

    private departmentModel mapResultSetToModel(ResultSet rs) throws SQLException {
        departmentModel u = new departmentModel();
        u.setId(rs.getInt("id"));
        u.setDepartment_name(rs.getString("department_name"));
        u.setIs_delete(rs.getBoolean("is_delete"));
        u.setCreated_by(rs.getInt("created_by"));
        u.setCreated_date(rs.getTimestamp("created_date"));
        u.setModify_by(rs.getInt("modify_by"));
        u.setModify_date(rs.getTimestamp("modify_date"));
        return u;
    }
}
