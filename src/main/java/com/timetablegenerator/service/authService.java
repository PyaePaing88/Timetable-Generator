package com.timetablegenerator.service;

import com.timetablegenerator.model.userModel;
import com.timetablegenerator.repository.authRepo;
import com.timetablegenerator.util.passwordUtil;

public class authService {

    private final authRepo repo = new authRepo();

    public userModel login(String email, String password) {
        userModel user = repo.findByEmail(email);

        if (user == null)
            return null;

        String hashed = passwordUtil.hashPassword(password);
        return hashed.equals(user.getPassword()) ? user : null;
    }

    public boolean changePassword(int userId, String newPassword) {
        String hashedPassword = passwordUtil.hashPassword(newPassword);

        return repo.updateUserPassword(userId, hashedPassword);
    }
}
