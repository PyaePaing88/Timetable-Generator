package com.timetablegenerator.repository;

import com.timetablegenerator.model.*;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.*;

public class timetableRepo {


    public List<TimetableCardDTO> findAllWithNames() throws SQLException {
        List<TimetableCardDTO> list = new ArrayList<>();
        String sql = "SELECT t.id, d.department_name, c.class_name, t.schedule_date " +
                "FROM timetable t " +
                "JOIN departments d ON t.department_id = d.id " +
                "JOIN classes c ON t.class_id = c.id " +
                "ORDER BY t.schedule_date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                TimetableCardDTO dto = new TimetableCardDTO();
                dto.setTimetableId(rs.getInt("id"));
                dto.setDepartmentName(rs.getString("department_name"));
                dto.setClassName(rs.getString("class_name"));
                dto.setScheduleDate(rs.getTimestamp("schedule_date").toString());
                list.add(dto);
            }
        }
        return list;
    }

    public void saveFullSchedule(List<timetableModel> headers, List<timetableAssignmentModel> assignments) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            for (timetableModel header : headers) {
                int headerId = saveHeaderInternal(conn, header);

                for (timetableAssignmentModel assign : assignments) {
                    if (assign.getTempClassId().equals(header.getClass_id())) {
                        assign.setTimetable_id(headerId);
                        saveAssignmentInternal(conn, assign);
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private int saveHeaderInternal(Connection conn, timetableModel model) throws SQLException {
        String sql = "INSERT INTO timetable (department_id, class_id, schedule_date) VALUES (?, ?, ?)";
        try (PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, model.getDepartment_id());
            st.setInt(2, model.getClass_id());
            st.setTimestamp(3, model.getSchedule_date());
            st.executeUpdate();
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Failed to retrieve generated ID for timetable.");
            }
        }
    }

    private void saveAssignmentInternal(Connection conn, timetableAssignmentModel model) throws SQLException {
        String sql = "INSERT INTO timetable_assignments (timetable_id, user_id, course_id, timeSlot_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, model.getTimetable_id());
            st.setInt(2, model.getUser_id());
            st.setInt(3, model.getCourse_id());
            st.setInt(4, model.getTimeSlot_id());
            st.executeUpdate();
        }
    }

    public boolean hasConflict(Integer userId, Integer classId, Integer slotId, Timestamp date) throws SQLException {
        String sql = "SELECT 1 FROM timetable_assignments ta JOIN timetable t ON ta.timetable_id = t.id " +
                "WHERE (ta.user_id = ? OR t.class_id = ?) AND ta.timeSlot_id = ? AND t.schedule_date = ? LIMIT 1";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, userId);
            st.setInt(2, classId);
            st.setInt(3, slotId);
            st.setTimestamp(4, date);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean hasShiftConflict(Integer tId, Integer cId, Timestamp date, boolean morning) throws SQLException {
        String shiftCondition = morning ? "ts.start_time < '12:00:00'" : "ts.start_time >= '12:00:00'";
        String sql = "SELECT EXISTS(SELECT 1 FROM timetable_assignments ta JOIN timetable t ON ta.timetable_id = t.id " +
                "JOIN time_slots ts ON ta.timeSlot_id = ts.id " +
                "WHERE ta.user_id = ? AND t.class_id = ? AND t.schedule_date = ? AND " + shiftCondition + ")";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, tId);
            st.setInt(2, cId);
            st.setTimestamp(3, date);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    public int countDistinctShiftsThisWeek(Integer tId, Timestamp start) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT CASE WHEN ts.start_time < '12:00:00' THEN 1 ELSE 2 END) " +
                "FROM timetable_assignments ta JOIN timetable t ON ta.timetable_id = t.id " +
                "JOIN time_slots ts ON ta.timeSlot_id = ts.id " +
                "WHERE ta.user_id = ? AND t.schedule_date BETWEEN ? AND DATE_ADD(?, INTERVAL 6 DAY)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, tId);
            st.setTimestamp(2, start);
            st.setTimestamp(3, start);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public boolean isDayContiguous(Integer tId, String currentDay, Timestamp start) throws SQLException {
        String sql = "SELECT DISTINCT ts.day_of_week FROM timetable_assignments ta " +
                "JOIN timetable t ON ta.timetable_id = t.id JOIN time_slots ts ON ta.timeSlot_id = ts.id " +
                "WHERE ta.user_id = ? AND t.schedule_date BETWEEN ? AND DATE_ADD(?, INTERVAL 6 DAY)";
        List<String> days = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, tId);
            st.setTimestamp(2, start);
            st.setTimestamp(3, start);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) days.add(rs.getString("day_of_week").toLowerCase());
            }
        }
        if (days.isEmpty()) return true;
        List<String> week = Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");
        int currIdx = week.indexOf(currentDay.toLowerCase());
        return days.stream().anyMatch(d -> Math.abs(currIdx - week.indexOf(d.toLowerCase())) <= 1);
    }

    public boolean isSameShiftAsPrevious(Integer tId, boolean currentIsMorning, Timestamp startDate) throws SQLException {
        String timeCondition = currentIsMorning ? "ts.start_time < '12:00:00'" : "ts.start_time >= '12:00:00'";
        String sql = "SELECT EXISTS(SELECT 1 FROM timetable_assignments ta JOIN timetable t ON ta.timetable_id = t.id " +
                "JOIN time_slots ts ON ta.timeSlot_id = ts.id " +
                "WHERE ta.user_id = ? AND t.schedule_date BETWEEN ? AND DATE_ADD(?, INTERVAL 6 DAY) AND " + timeCondition + ")";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, tId);
            st.setTimestamp(2, startDate);
            st.setTimestamp(3, startDate);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }
}