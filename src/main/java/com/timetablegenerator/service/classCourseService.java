package com.timetablegenerator.service;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.repository.classCourseRepo;
import com.timetablegenerator.util.authSession;

import java.util.List;
import java.util.stream.Collectors;

public class classCourseService {
    private final classCourseRepo repo = new classCourseRepo();

    public List<classModel> getUnlinkedClasses(int courseId) throws Exception {
        return repo.getUnlinkedClasses(courseId);
    }

    public List<classModel> getLinkedClasses(int courseId) throws Exception {
        return repo.getLinkedClasses(courseId);
    }

    public void updateClassCourseLinks(int courseId, List<classModel> assignedClasses) throws Exception {
        List<Integer> ids = assignedClasses.stream().map(classModel::getId).collect(Collectors.toList());
        int currentUserId = authSession.getUser().getId();
        repo.saveLinks(courseId, ids, currentUserId);
    }
}