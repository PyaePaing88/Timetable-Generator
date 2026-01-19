package com.timetablegenerator.repository;

import com.timetablegenerator.model.*;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.*;

public class timetableRepo {

    public void saveFullSchedule(List<timetableModel> headers, List<timetableAssignmentModel> assignments) throws SQLException {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String headerSql = "INSERT INTO timetable (department_id, class_id, schedule_date) VALUES (?, ?, ?)";
                String assignSql = "INSERT INTO timetable_assignments (timetable_id, user_id, course_id, timeSlot_id) VALUES (?, ?, ?, ?)";

                for (timetableModel header : headers) {
                    try (PreparedStatement hSt = conn.prepareStatement(headerSql, Statement.RETURN_GENERATED_KEYS)) {
                        hSt.setInt(1, header.getDepartment_id());
                        hSt.setInt(2, header.getClass_id());
                        hSt.setInt(3, header.getSchedule_date());
                        hSt.executeUpdate();

                        try (ResultSet rs = hSt.getGeneratedKeys()) {
                            if (rs.next()) {
                                int hId = rs.getInt(1);
                                try (PreparedStatement aSt = conn.prepareStatement(assignSql)) {
                                    for (timetableAssignmentModel a : assignments) {
                                        if (a.getTempClassId().equals(header.getClass_id())) {
                                            aSt.setInt(1, hId);
                                            aSt.setInt(2, a.getUser_id());
                                            aSt.setInt(3, a.getCourse_id());
                                            aSt.setInt(4, a.getTimeSlot_id());
                                            aSt.addBatch();
                                        }
                                    }
                                    aSt.executeBatch();
                                }
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public int getTotalTimetableCount(String search) throws SQLException {
        String sql = "SELECT COUNT(*) FROM timetable t " +
                "JOIN departments d ON t.department_id = d.id " +
                "JOIN classes c ON t.class_id = c.id " +
                "WHERE d.department_name LIKE ? OR c.class_name LIKE ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            String query = "%" + search + "%";
            st.setString(1, query);
            st.setString(2, query);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<TimetableCardDTO> getTimetablesPaginated(int page, int size, String search) throws SQLException {
        List<TimetableCardDTO> list = new ArrayList<>();
        int offset = (page - 1) * size;

        String sql = "SELECT t.id, d.department_name, c.class_name, t.schedule_date " +
                "FROM timetable t " +
                "JOIN departments d ON t.department_id = d.id " +
                "JOIN classes c ON t.class_id = c.id " +
                "WHERE d.department_name LIKE ? OR c.class_name LIKE ? " +
                "ORDER BY t.id DESC LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            String query = "%" + search + "%";
            st.setString(1, query);
            st.setString(2, query);
            st.setInt(3, size);
            st.setInt(4, offset);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    TimetableCardDTO dto = new TimetableCardDTO();
                    dto.setTimetableId(rs.getInt("id"));
                    dto.setDepartmentName(rs.getString("department_name"));
                    dto.setClassName(rs.getString("class_name"));
                    dto.setScheduleDate(rs.getInt("schedule_date"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public List<TimetableDetailDTO> findAssignmentsByTimetableId(Integer timetableId) throws SQLException {
        List<TimetableDetailDTO> list = new ArrayList<>();
        String sql = "SELECT ts.day_of_week, ts.period, ts.start_time, ts.end_time, " +
                "u.name AS teacher_name, co.course_name, co.subject_code, " +
                "ta.id, ta.user_id, ta.is_leave " +
                "FROM timetable_assignments ta " +
                "JOIN time_slots ts ON ta.timeSlot_id = ts.id " +
                "JOIN users u ON ta.user_id = u.id " +
                "JOIN courses co ON ta.course_id = co.id " +
                "WHERE ta.timetable_id = ? " +
                "ORDER BY ts.day_of_week, ts.period";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, timetableId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    TimetableDetailDTO dto = new TimetableDetailDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setTeacher_id(rs.getInt("user_id"));
                    dto.setDay(rs.getString("day_of_week"));
                    dto.setPeriod(rs.getInt("period"));
                    dto.setTime(rs.getString("start_time") + " - " + rs.getString("end_time"));
                    dto.setTeacherName(rs.getString("teacher_name"));
                    dto.setCourseName(rs.getString("course_name"));
                    dto.setSubjectCode(rs.getString("subject_code"));
                    dto.setIs_leave(rs.getBoolean("is_leave"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
}