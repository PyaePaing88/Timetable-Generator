package com.timetablegenerator.service;

import com.timetablegenerator.model.userModel;
import com.timetablegenerator.repository.userRepo;
import java.util.List;


public class userService {

    private final userRepo repo;

    public userService(userRepo userRepository) {
        this.repo = userRepository;
    }

    public int getTotalUserCount(String search) throws Exception {
        return repo.getTotalCount(search);
    }

    public List<userModel> getUsers(int page, int pageSize, String search) throws Exception {
        int offset = (page - 1) * pageSize;
        return repo.findAll(pageSize, offset, search);
    }

    public userModel getUserById(int id) throws Exception {
        return repo.findById(id);
    }

    public void saveUser(userModel user) throws Exception {
        if (user.getId() > 0) {
            repo.update(user);
        } else {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                String hashedPassword = com.timetablegenerator.util.passwordUtil.hashPassword(user.getPassword());
                user.setPassword(hashedPassword);
            }
            repo.create(user);
        }
    }

    public void deleteUser(int id) throws Exception {
        repo.softDelete(id);
    }
}
