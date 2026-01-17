package com.timetablegenerator.repository;

import com.timetablegenerator.model.courseModel;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.util.authSession;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class courseRepo {

    public courseRepo() {
    }

    private final userModel currentUser = authSession.getUser();

    public void create(courseModel course) throws SQLException {
        String sql = "INSERT INTO courses (course_name,department_id, is_delete, created_by, created_date) VALUES (?,?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, course.getCourse_name());
            st.setInt(2, course.getDepartment_id());
            st.setBoolean(3, false);
            st.setInt(4, currentUser.getId());
            st.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
        }
    }

    public int getTotalCount(String search) throws Exception {
        String sql = "SELECT COUNT(*) FROM courses WHERE (course_name LIKE ?) AND is_delete = 0";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<courseModel> findAll(int limit, int offset, String search) throws SQLException {
        List<courseModel> course = new ArrayList<>();
        String sql = "SELECT c.*, d.department_name " +
                "FROM courses c " +
                "INNER JOIN departments d ON c.department_id = d.id " +
                "WHERE c.is_delete = false AND (c.course_name LIKE ?) " +
                "LIMIT ? OFFSET ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            st.setInt(2, limit);
            st.setInt(3, offset);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                course.add(mapResultSetToModel(rs));
            }
        }
        return course;
    }

    public List<courseModel> findAllForCombo() throws SQLException {
        List<courseModel> course = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE is_delete = false";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                course.add(mapResultSetToModel(rs));
            }
        }
        return course;
    }

    public void update(courseModel course) throws SQLException {
        String sql = "UPDATE courses SET course_name=?, department_id=?, modify_by=?, modify_date=? WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, course.getCourse_name());
            st.setInt(2, course.getDepartment_id());
            st.setInt(3, currentUser.getId());
            st.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            st.setInt(5, course.getId());
            st.executeUpdate();
        }
    }

    public void softDelete(int id) throws SQLException {
        String sql = "UPDATE courses SET is_delete = true WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public courseModel findById(int id) throws SQLException {
        String sql = "SELECT c.*, d.department_name FROM courses c " +
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

    private courseModel mapResultSetToModel(ResultSet rs) throws SQLException {
        courseModel c = new courseModel();
        c.setId(rs.getInt("id"));
        c.setCourse_name(rs.getString("course_name"));
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
