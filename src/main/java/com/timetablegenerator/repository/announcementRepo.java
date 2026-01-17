package com.timetablegenerator.repository;

import com.timetablegenerator.model.announcementModel;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.util.authSession;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class announcementRepo {
    private final userModel currentUser = authSession.getUser();

    public announcementRepo() {
    }

    public void create(announcementModel announcement) throws SQLException {
        String sql = "INSERT INTO announcements (title, message, is_delete, created_by, created_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, announcement.getTitle());
            st.setString(2, announcement.getMessage());
            st.setBoolean(3, false);
            st.setInt(4, currentUser.getId());
            st.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
        }
    }

    public int getTotalCount(String search) throws Exception {
        String sql = "SELECT COUNT(*) FROM announcements WHERE (title LIKE ? OR message LIKE ?) AND is_delete = 0";
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

    public List<announcementModel> findAll(int limit, int offset, String search) throws SQLException {
        List<announcementModel> list = new ArrayList<>();
        String sql = "SELECT a.* FROM announcements a " +
                "WHERE a.is_delete = false " +
                "AND (a.title LIKE ? OR a.message LIKE ?) " +
                "ORDER BY a.created_date DESC LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            String searchPattern = "%" + search + "%";
            st.setString(1, searchPattern);
            st.setString(2, searchPattern);
            st.setInt(3, limit);
            st.setInt(4, offset);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToModel(rs));
            }
        }
        return list;
    }

    public void update(announcementModel announcement) throws SQLException {
        String sql = "UPDATE announcements SET title=?, message=?, modify_by=?, modify_date=? WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, announcement.getTitle());
            st.setString(2, announcement.getMessage());
            st.setInt(3, currentUser.getId());
            st.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            st.setInt(5, announcement.getId());
            st.executeUpdate();
        }
    }

    public void softDelete(int id) throws SQLException {
        String sql = "UPDATE announcements SET is_delete = true WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public announcementModel findById(int id) throws SQLException {
        String sql = "SELECT * FROM announcements WHERE id = ? AND is_delete = false";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return mapResultSetToModel(rs);
            }
        }
        return null;
    }

    private announcementModel mapResultSetToModel(ResultSet rs) throws SQLException {
        announcementModel model = new announcementModel();
        model.setId(rs.getInt("id"));
        model.setTitle(rs.getString("title"));
        model.setMessage(rs.getString("message"));
        model.setCreated_by(rs.getInt("created_by"));
        model.setCreated_date(rs.getTimestamp("created_date"));
        model.setModify_by(rs.getInt("modify_by"));
        model.setModify_date(rs.getTimestamp("modify_date"));
        model.setIs_delete(rs.getBoolean("is_delete"));
        return model;
    }
}