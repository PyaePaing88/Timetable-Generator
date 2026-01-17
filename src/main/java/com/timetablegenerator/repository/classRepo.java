package com.timetablegenerator.repository;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.service.departmentService;
import com.timetablegenerator.util.authSession;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class classRepo {
    private departmentService deptService;

    public classRepo() {
        this.deptService = new departmentService(new departmentRepo());
    }

    private final userModel currentUser = authSession.getUser();

    public void create(classModel clas) throws SQLException {
        String sql = "INSERT INTO classes (class_name,department_id, is_delete, created_by, created_date) VALUES (?,?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, clas.getClass_name());
            st.setInt(2, clas.getDepartment_id());
            st.setBoolean(3, false);
            st.setInt(4, currentUser.getId());
            st.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
        }
    }

    public int getTotalCount(String search) throws Exception {
        String sql = "SELECT COUNT(*) FROM classes WHERE (class_name LIKE ?) AND is_delete = 0";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<classModel> findAll(int limit, int offset, String search) throws SQLException {
        List<classModel> clas = new ArrayList<>();
        String sql = "SELECT c.*, d.department_name " +
                "FROM classes c " +
                "INNER JOIN departments d ON c.department_id = d.id " +
                "WHERE c.is_delete = false AND (c.class_name LIKE ?) " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            st.setInt(2, limit);
            st.setInt(3, offset);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                clas.add(mapResultSetToModel(rs));
            }
        }
        return clas;
    }

    public List<classModel> findAllForCombo() throws SQLException {
        List<classModel> clas = new ArrayList<>();
        String sql = "SELECT * FROM classes WHERE is_delete = false";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                clas.add(mapResultSetToModel(rs));
            }
        }
        return clas;
    }

    public void update(classModel clas) throws SQLException {
        String sql = "UPDATE classes SET class_name=?, department_id=?, modify_by=?, modify_date=? WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, clas.getClass_name());
            st.setInt(2, clas.getDepartment_id());
            st.setInt(3, currentUser.getId());
            st.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            st.setInt(5, clas.getId());
            st.executeUpdate();
        }
    }

    public void softDelete(int id) throws SQLException {
        String sql = "UPDATE classes SET is_delete = true WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public classModel findById(int id) throws SQLException {
        String sql = "SELECT c.*, d.department_name FROM classes c " +
                "INNER JOIN departments d ON c.department_id = d.id " +
                "WHERE c.id = ? AND c.is_delete = false";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapResultSetToModel(rs);
        }
        return null;
    }

    public boolean existsByNameAndDept(String name, int deptId) {
        String sql = "SELECT 1 FROM classes WHERE class_name = ? AND department_id = ? AND is_delete = 0 LIMIT 1";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, name);
            st.setInt(2, deptId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private classModel mapResultSetToModel(ResultSet rs) throws SQLException {
        classModel c = new classModel();
        c.setId(rs.getInt("id"));
        c.setClass_name(rs.getString("class_name"));
        c.setDepartment_id(rs.getInt("department_id"));
        try {
            c.setDepartment_name(rs.getString("department_name"));
        } catch (SQLException e) {
            c.setDepartment_name("N/A");
        }
        c.setIs_delete(rs.getBoolean("is_delete"));
        c.setCreated_by(rs.getInt("created_by"));
        c.setCreated_date(rs.getTimestamp("created_date"));
        c.setModify_by(rs.getInt("modify_by"));
        c.setModify_date(rs.getTimestamp("modify_date"));
        return c;
    }
}
