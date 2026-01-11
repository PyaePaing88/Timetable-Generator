package com.timetablegenerator.service;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.model.courseModel;
import com.timetablegenerator.repository.classRepo;
import com.timetablegenerator.repository.courseRepo;

import java.util.List;

public class courseService {
    private final courseRepo repo;

    public courseService(courseRepo courseRepository) {
        this.repo = courseRepository;
    }

    public int getTotalCourseCount(String search) throws Exception {
        return repo.getTotalCount(search);
    }

    public List<courseModel> getCourses(int page, int pageSize, String search) throws Exception {
        int offset = (page - 1) * pageSize;
        return repo.findAll(pageSize, offset, search);
    }

    public List<courseModel> getCoursesForCombo() throws Exception {
        return repo.findAllForCombo();
    }

    public courseModel getCourseById(int id) throws Exception {
        return repo.findById(id);
    }

    public void saveCourses(courseModel course) throws Exception {
        if (course.getId() > 0) {
            repo.update(course);
        } else {
            repo.create(course);
        }
    }

    public void deleteCourse(int id) throws Exception {
        repo.softDelete(id);
    }
}
