package com.timetablegenerator.repository;

import com.timetablegenerator.model.userModel;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class userCourseRepo {

    public List<userModel> getUnlinkedUsers(int courseId) throws Exception {
        List<userModel> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE department_id = (SELECT department_id FROM courses WHERE id = ?) " +
                "AND is_delete = false " +
                "AND id NOT IN (SELECT user_id FROM user_course WHERE course_id = ? AND is_delete = false)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {
            st.setInt(1, courseId);
            st.setInt(2, courseId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    userModel um = new userModel();
                    um.setId(rs.getInt("id"));
                    um.setName(rs.getString("name"));
                    users.add(um);
                }
            }
        }
        return users;
    }

    public List<userModel> getLinkedUsers(int courseId) throws Exception {
        List<userModel> users = new ArrayList<>();
        String query = "SELECT u.* FROM users u " +
                "JOIN user_course uc ON u.id = uc.user_id " +
                "WHERE uc.course_id = ? AND uc.is_delete = false";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {
            st.setInt(1, courseId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    userModel um = new userModel();
                    um.setId(rs.getInt("id"));
                    um.setName(rs.getString("name"));
                    users.add(um);
                }
            }
        }
        return users;
    }

    public void saveLinks(int courseId, List<Integer> userIds, int currentAdminId) throws Exception {
        try (Connection conn = dbConnection.getConnection()) {
            try {
                conn.setAutoCommit(false);

                // 1. Soft delete existing links for this course
                String deleteQuery = "UPDATE user_course SET is_delete = true, modify_by = ?, modify_date = CURRENT_TIMESTAMP WHERE course_id = ?";
                try (PreparedStatement st = conn.prepareStatement(deleteQuery)) {
                    st.setInt(1, currentAdminId);
                    st.setInt(2, courseId);
                    st.executeUpdate();
                }

                // 2. Insert new links
                String insertQuery = "INSERT INTO user_course (user_id, course_id, created_by, created_date, is_delete) VALUES (?, ?, ?, CURRENT_TIMESTAMP, false)";
                try (PreparedStatement st = conn.prepareStatement(insertQuery)) {
                    for (Integer userId : userIds) {
                        st.setInt(1, userId);
                        st.setInt(2, courseId);
                        st.setInt(3, currentAdminId);
                        st.addBatch();
                    }
                    st.executeBatch();
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
}