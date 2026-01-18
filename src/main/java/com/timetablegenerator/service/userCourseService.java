package com.timetablegenerator.service;

import com.timetablegenerator.model.userModel;
import com.timetablegenerator.repository.userCourseRepo;
import com.timetablegenerator.util.authSession;

import java.util.List;
import java.util.stream.Collectors;

public class userCourseService {
    private final userCourseRepo repo = new userCourseRepo();

    public List<userModel> getUnlinkedUsers(int courseId) throws Exception {
        return repo.getUnlinkedUsers(courseId);
    }

    public List<userModel> getLinkedUsers(int courseId) throws Exception {
        return repo.getLinkedUsers(courseId);
    }

    public void updateUserCourseLinks(int courseId, List<userModel> assignedUsers) throws Exception {
        List<Integer> userIds = assignedUsers.stream().map(userModel::getId).collect(Collectors.toList());
        int currentUserId = authSession.getUser().getId();
        repo.saveLinks(courseId, userIds, currentUserId);
    }
}