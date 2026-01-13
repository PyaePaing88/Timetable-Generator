package com.timetablegenerator.repository;

import com.timetablegenerator.model.role;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.util.authSession;
import com.timetablegenerator.util.dbConnection;

import java.sql.*;

public class authRepo {

    private final userModel currentUser = authSession.getUser();

    public userModel findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=? AND is_active=true AND is_delete=false";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userModel u = new userModel();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setRole((role.valueOf(rs.getString("role"))));
                u.setChange_password(rs.getBoolean("change_password"));
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ?, change_password = false, modify_by=?, modify_date=? WHERE id = ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2,currentUser.getId());
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setInt(4, userId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
