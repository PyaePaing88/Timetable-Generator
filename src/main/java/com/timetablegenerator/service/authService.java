package com.timetablegenerator.service;

import com.timetablegenerator.model.userModel;
import com.timetablegenerator.repository.userRepo;
import com.timetablegenerator.util.passwordUtil;

public class authService {

    private final userRepo repo = new userRepo();

//    public userModel login(String email, String password) {
//        userModel user = repo.findByEmail(email);
//
//        if (user == null)
//            return null;
//
//        String hashed = passwordUtil.hashPassword(password);
//        return hashed.equals(user.getPassword()) ? user : null;
//    }
}
