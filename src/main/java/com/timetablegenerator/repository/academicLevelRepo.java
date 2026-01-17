package com.timetablegenerator.repository;

import com.timetablegenerator.model.academicLevelModel;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.util.authSession;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class academicLevelRepo {

    public academicLevelRepo() {
    }

    private final userModel currentUser = authSession.getUser();

    public void create(academicLevelModel al) throws SQLException {
        String sql = "INSERT INTO academic_levels (year, is_delete, created_by, created_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, al.getYear());
            st.setBoolean(2, false);
            st.setInt(3, currentUser.getId());
            st.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
        }
    }

    public int getTotalCount(String search) throws Exception {
        String sql = "SELECT COUNT(*) FROM academic_levels WHERE (year LIKE ?) AND is_delete = 0";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<academicLevelModel> findAll(int limit, int offset, String search) throws SQLException {
        List<academicLevelModel> al = new ArrayList<>();
        String sql = "SELECT * FROM academic_levels WHERE is_delete = false AND (year LIKE ?) LIMIT ? OFFSET ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            st.setInt(2, limit);
            st.setInt(3, offset);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                al.add(mapResultSetToModel(rs));
            }
        }
        return al;
    }

    public List<academicLevelModel> findAllForCombo() throws SQLException {
        List<academicLevelModel> al = new ArrayList<>();
        String sql = "SELECT * FROM academic_levels WHERE is_delete = false";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                al.add(mapResultSetToModel(rs));
            }
        }
        return al;
    }

    public void update(academicLevelModel al) throws SQLException {
        String sql = "UPDATE academic_levels SET year=?, modify_by=?, modify_date=? WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, al.getYear());
            st.setInt(2, currentUser.getId());
            st.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            st.setInt(4, al.getId());
            st.executeUpdate();
        }
    }

    public void softDelete(int id) throws SQLException {
        String sql = "UPDATE academic_levels SET is_delete = true WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public academicLevelModel findById(int id) throws SQLException {
        String sql = "SELECT * FROM academic_levels WHERE id = ? AND is_delete = false";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapResultSetToModel(rs);
        }
        return null;
    }

    private academicLevelModel mapResultSetToModel(ResultSet rs) throws SQLException {
        academicLevelModel u = new academicLevelModel();
        u.setId(rs.getInt("id"));
        u.setYear(rs.getString("year"));
        u.setIs_delete(rs.getBoolean("is_delete"));
        u.setCreated_by(rs.getInt("created_by"));
        u.setCreated_date(rs.getTimestamp("created_date"));
        u.setModify_by(rs.getInt("modify_by"));
        u.setModify_date(rs.getTimestamp("modify_date"));
        return u;
    }
}
