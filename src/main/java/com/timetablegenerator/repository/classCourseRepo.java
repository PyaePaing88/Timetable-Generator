package com.timetablegenerator.repository;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class classCourseRepo {

    public List<classModel> getUnlinkedClasses(int courseId) throws Exception {
        List<classModel> classes = new ArrayList<>();
        String query = "SELECT * FROM classes WHERE id NOT IN " +
                "(SELECT class_id FROM class_course WHERE course_id = ? AND is_delete = false)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            st.setInt(1, courseId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    classModel cm = new classModel();
                    cm.setId(rs.getInt("id"));
                    cm.setClass_name(rs.getString("class_name"));
                    classes.add(cm);
                }
            }
        }
        return classes;
    }

    public List<classModel> getLinkedClasses(int courseId) throws Exception {
        List<classModel> classes = new ArrayList<>();
        String query = "SELECT c.* FROM classes c " +
                "JOIN class_course cc ON c.id = cc.class_id " +
                "WHERE cc.course_id = ? AND cc.is_delete = false";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            st.setInt(1, courseId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    classModel cm = new classModel();
                    cm.setId(rs.getInt("id"));
                    cm.setClass_name(rs.getString("class_name"));
                    classes.add(cm);
                }
            }
        }
        return classes;
    }

    public void saveLinks(int courseId, List<Integer> classIds, int userId) throws Exception {
        try (Connection conn = dbConnection.getConnection()) {
            try {
                conn.setAutoCommit(false);

                String deleteQuery = "UPDATE class_course SET is_delete = true, modify_by = ?, modify_date = CURRENT_TIMESTAMP WHERE course_id = ?";
                try (PreparedStatement st = conn.prepareStatement(deleteQuery)) {
                    st.setInt(1, userId);
                    st.setInt(2, courseId);
                    st.executeUpdate();
                }

                String insertQuery =
                        "INSERT INTO class_course (class_id, course_id, is_major, created_by, created_date, is_delete) " +
                                "SELECT ?, ?, " +
                                "(SELECT CASE WHEN c.department_id = co.department_id THEN true ELSE false END " +
                                " FROM classes c, courses co WHERE c.id = ? AND co.id = ?), " +
                                "?, CURRENT_TIMESTAMP, false";

                try (PreparedStatement st = conn.prepareStatement(insertQuery)) {
                    for (Integer classId : classIds) {
                        st.setInt(1, classId);
                        st.setInt(2, courseId);
                        st.setInt(3, classId);
                        st.setInt(4, courseId);
                        st.setInt(5, userId);
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