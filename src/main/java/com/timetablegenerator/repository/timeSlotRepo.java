package com.timetablegenerator.repository;

import com.timetablegenerator.model.day;
import com.timetablegenerator.model.timeSlotModel;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.util.authSession;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class timeSlotRepo {

    public timeSlotRepo() {
    }

    private final userModel currentUser = authSession.getUser();

    public void create(timeSlotModel time) throws SQLException {
        String sql = "INSERT INTO time_slots (day_of_week, period, start_time, end_time, created_by, created_date, is_delete, is_morning_shift) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, time.getDay_of_week().name());
            st.setInt(2, time.getPeriod());
            st.setTime(3, time.getStart_time());
            st.setTime(4, time.getEnd_time());
            st.setInt(5, currentUser.getId());
            st.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
            st.setBoolean(7, false); // is_delete
            st.setBoolean(8, time.isIs_morning_shift());

            st.executeUpdate();
        }
    }

    public int getTotalCount(String search) throws Exception {
        String sql = "SELECT COUNT(*) FROM time_slots WHERE (day_of_week LIKE ? OR period LIKE ? OR start_time LIKE ? OR end_time LIKE ?) AND is_delete = 0";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            String searchPattern = "%" + search + "%";
            st.setString(1, searchPattern);
            st.setString(2, searchPattern);
            st.setString(3, searchPattern);
            st.setString(4, searchPattern);

            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<timeSlotModel> findAll(int limit, int offset, String search) throws SQLException {
        List<timeSlotModel> time = new ArrayList<>();
        String sql = "SELECT * FROM time_slots WHERE is_delete = false AND (day_of_week LIKE ? OR period LIKE ? OR start_time LIKE ? OR end_time LIKE ?) ORDER BY id DESC LIMIT ? OFFSET ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            String searchPattern = "%" + search + "%";

            st.setString(1, searchPattern);
            st.setString(2, searchPattern);
            st.setString(3, searchPattern);
            st.setString(4, searchPattern);
            st.setInt(5, limit);
            st.setInt(6, offset);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                time.add(mapResultSetToModel(rs));
            }
        }
        return time;
    }

    public List<timeSlotModel> findAllForTimetable() throws SQLException {
        List<timeSlotModel> timeSlots = new ArrayList<>();
        String sql = "SELECT * FROM time_slots WHERE is_delete = false ORDER BY day_of_week, start_time";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    timeSlots.add(mapResultSetToModel(rs));
                }
            }
        }
        return timeSlots;
    }

    public void update(timeSlotModel time) throws SQLException {
        String sql = "UPDATE time_slots SET day_of_week=?, period=?, start_time=?, end_time=?, modify_by=?, modify_date=?, is_morning_shift=? WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, time.getDay_of_week().name());
            st.setInt(2, time.getPeriod());
            st.setTime(3, time.getStart_time());
            st.setTime(4, time.getEnd_time());
            st.setInt(5, currentUser.getId());
            st.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
            st.setBoolean(7, time.isIs_morning_shift());
            st.setInt(8, time.getId());

            st.executeUpdate();
        }
    }

    public void softDelete(int id) throws SQLException {
        String sql = "UPDATE time_slots SET is_delete = true WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public timeSlotModel findById(int id) throws SQLException {
        String sql = "SELECT * FROM time_slots WHERE id = ? AND is_delete = false";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return mapResultSetToModel(rs);
        }
        return null;
    }

    private timeSlotModel mapResultSetToModel(ResultSet rs) throws SQLException {
        timeSlotModel u = new timeSlotModel();
        u.setId(rs.getInt("id"));
        u.setDay_of_week(day.valueOf(rs.getString("day_of_week")));
        u.setPeriod(rs.getInt("period"));
        u.setStart_time(rs.getTime("start_time"));
        u.setEnd_time(rs.getTime("end_time"));
        u.setCreated_by(rs.getInt("created_by"));
        u.setCreated_date(rs.getTimestamp("created_date"));
        u.setModify_by(rs.getInt("modify_by"));
        u.setModify_date(rs.getTimestamp("modify_date"));
        u.setIs_delete(rs.getBoolean("is_delete"));
        u.setIs_morning_shift(rs.getBoolean("is_morning_shift"));
        return u;
    }
}