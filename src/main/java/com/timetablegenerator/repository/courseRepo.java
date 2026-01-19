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
        String sql = "INSERT INTO courses (course_name, subject_code, department_id, academicLevel_id, is_delete, created_by, created_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, course.getCourse_name());
            st.setString(2, course.getSubject_code());
            st.setInt(3, course.getDepartment_id());
            st.setInt(4, course.getAcademicLevel_id()); // Added
            st.setBoolean(5, false);
            st.setInt(6, currentUser.getId());
            st.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
        }
    }

    public int getTotalCount(String search) throws Exception {
        String sql = "SELECT COUNT(*) FROM courses WHERE is_delete = false AND (course_name LIKE ? OR subject_code LIKE ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            String pattern = "%" + search + "%";
            st.setString(1, pattern);
            st.setString(2, pattern);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<courseModel> findAll(int limit, int offset, String search) throws SQLException {
        List<courseModel> courses = new ArrayList<>();
        String sql = "SELECT c.*, d.department_name, al.year AS academic_level_name " +
                "FROM courses c " +
                "INNER JOIN departments d ON c.department_id = d.id " +
                "INNER JOIN academic_levels al ON c.academicLevel_id = al.id " +
                "WHERE c.is_delete = false AND (c.course_name LIKE ? OR c.subject_code LIKE ?) " +
                "ORDER BY c.id DESC " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            String searchPattern = "%" + search + "%";
            st.setString(1, searchPattern);
            st.setString(2, searchPattern);
            st.setInt(3, limit);
            st.setInt(4, offset);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToModel(rs));
                }
            }
        }
        return courses;
    }

    public List<courseModel> findAllForCombo() throws SQLException {
        List<courseModel> courses = new ArrayList<>();
        String sql = "SELECT c.*, d.department_name, al.name AS academic_level_name " +
                "FROM courses c " +
                "INNER JOIN departments d ON c.department_id = d.id " +
                "INNER JOIN academic_levels al ON c.academicLevel_id = al.id " +
                "WHERE c.is_delete = false";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToModel(rs));
            }
        }
        return courses;
    }

    public List<Integer> getCourseIdByClass(Integer classId) throws SQLException {
        List<Integer> courseIds = new ArrayList<>();
        String sql = "SELECT c.id " +
                "FROM courses c " +
                "JOIN class_course cc ON c.id = cc.course_id " +
                "WHERE cc.class_id = ? " +
                "AND c.is_delete = 0 " +
                "AND cc.is_delete = 0";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, classId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    courseIds.add(rs.getInt("id"));
                }
            }
        }
        return courseIds;
    }

    public void update(courseModel course) throws SQLException {
        String sql = "UPDATE courses SET course_name=?, subject_code=?, department_id=?, academicLevel_id=?, modify_by=?, modify_date=? WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, course.getCourse_name());
            st.setString(2, course.getSubject_code());
            st.setInt(3, course.getDepartment_id());
            st.setInt(4, course.getAcademicLevel_id()); // Added
            st.setInt(5, currentUser.getId());
            st.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            st.setInt(7, course.getId());
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
        String sql = "SELECT c.*, d.department_name, al.year AS academic_level_name " +
                "FROM courses c " +
                "INNER JOIN departments d ON c.department_id = d.id " +
                "INNER JOIN academic_levels al ON c.academicLevel_id = al.id " +
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
        c.setSubject_code(rs.getString("subject_code"));
        c.setDepartment_id(rs.getInt("department_id"));

        try {
            c.setDepartment_name(rs.getString("department_name"));
        } catch (SQLException e) {
            c.setDepartment_name("N/A");
        }

        c.setAcademicLevel_id(rs.getInt("academicLevel_id"));
        try {
            c.setAcademicLevel(rs.getString("academic_level_name"));
        } catch (SQLException e) {
            c.setAcademicLevel("N/A");
        }

        c.setIs_delete(rs.getBoolean("is_delete"));
        c.setCreated_by(rs.getInt("created_by"));
        c.setCreated_date(rs.getTimestamp("created_date"));
        c.setModify_by(rs.getInt("modify_by"));
        c.setModify_date(rs.getTimestamp("modify_date"));
        return c;
    }
}