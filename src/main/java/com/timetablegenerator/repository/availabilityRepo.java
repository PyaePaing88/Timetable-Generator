package com.timetablegenerator.repository;

import com.timetablegenerator.model.availabilityModel;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.util.authSession;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class availabilityRepo {

    public availabilityRepo() {
    }

    private final userModel currentUser = authSession.getUser();

    public void create(availabilityModel availability) throws SQLException {
        String sql = "INSERT INTO availabilities (status, remark, `from`, `to`, is_delete, created_by, created_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, availability.getStatus());
            st.setString(2, availability.getRemark());
            st.setTimestamp(3, availability.getFrom());
            st.setTimestamp(4, availability.getTo());
            st.setBoolean(5, false);
            st.setInt(6, currentUser.getId());
            st.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
        }
    }

    public int getTotalCount(String search) throws Exception {
        String sql = "SELECT COUNT(*) FROM availabilities WHERE (status LIKE ? OR remark LIKE ?) AND is_delete = 0";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, "%" + search + "%");
            st.setString(2, "%" + search + "%");
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<availabilityModel> findAll(int limit, int offset, String search) throws SQLException {
        List<availabilityModel> list = new ArrayList<>();
        String sql = "SELECT a.*, u.name AS teacher_name " +
                "FROM availabilities a " +
                "INNER JOIN users u ON a.created_by = u.id " +
                "WHERE a.is_delete = false " +
                "AND (a.status LIKE ? OR a.Remark LIKE ? OR u.name LIKE ?) " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            String searchPattern = "%" + search + "%";
            st.setString(1, searchPattern);
            st.setString(2, searchPattern);
            st.setString(3, searchPattern);
            st.setInt(4, limit);
            st.setInt(5, offset);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToModel(rs));
            }
        }
        return list;
    }

    public int getTotalCountByTeacher(String search) throws Exception {
        String sql = "SELECT COUNT(*) FROM availabilities a " +
                "INNER JOIN users u ON a.created_by = u.id " +
                "WHERE (a.status LIKE ? OR a.Remark LIKE ? OR u.name LIKE ?) " +
                "AND a.is_delete = 0 AND a.created_by = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            String pattern = "%" + search + "%";
            st.setString(1, pattern);
            st.setString(2, pattern);
            st.setString(3, pattern);
            st.setInt(4, currentUser.getId());
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<availabilityModel> findAllByTeacher(int limit, int offset, String search) throws SQLException {
        List<availabilityModel> list = new ArrayList<>();
        String sql = "SELECT a.*, u.name AS teacher_name " +
                "FROM availabilities a " +
                "INNER JOIN users u ON a.created_by = u.id " +
                "WHERE a.is_delete = false " +
                "AND (a.status LIKE ? OR a.Remark LIKE ? OR u.name LIKE ?) " +
                "AND a.created_by = ? " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            String searchPattern = "%" + search + "%";
            st.setString(1, searchPattern);
            st.setString(2, searchPattern);
            st.setString(3, searchPattern);
            st.setInt(4, currentUser.getId());
            st.setInt(5, limit);
            st.setInt(6, offset);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToModel(rs));
            }
        }
        return list;
    }

    public void update(availabilityModel availability) throws SQLException {
        String sql = "UPDATE availabilities SET status=?, remark=?, `from`=?, `to`=?, modify_by=?, modify_date=? WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, availability.getStatus());
            st.setString(2, availability.getRemark());
            st.setTimestamp(3, availability.getFrom());
            st.setTimestamp(4, availability.getTo());
            st.setInt(5, currentUser.getId());
            st.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            st.setInt(7, availability.getId());
            st.executeUpdate();
        }
    }

    public void softDelete(int id) throws SQLException {
        String sql = "UPDATE availabilities SET is_delete = true WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public availabilityModel findById(int id) throws SQLException {
        String sql = "SELECT a.*, u.name AS teacher_name " +
                "FROM availabilities a " +
                "INNER JOIN users u ON a.created_by = u.id " +
                "WHERE a.id = ? AND a.is_delete = false";

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


    private availabilityModel mapResultSetToModel(ResultSet rs) throws SQLException {
        availabilityModel model = new availabilityModel();
        model.setId(rs.getInt("id"));
        model.setStatus(rs.getString("status"));
        model.setRemark(rs.getString("Remark"));
        model.setFrom(rs.getTimestamp("from"));
        model.setTo(rs.getTimestamp("to"));
        model.setCreated_by(rs.getInt("created_by"));
        model.setCreated_date(rs.getTimestamp("created_date"));
        model.setModify_by(rs.getInt("modify_by"));
        model.setModify_date(rs.getTimestamp("modify_date"));
        model.setIs_delete(rs.getBoolean("is_delete"));

        model.setTeacher_name(rs.getString("teacher_name"));

        return model;
    }
}