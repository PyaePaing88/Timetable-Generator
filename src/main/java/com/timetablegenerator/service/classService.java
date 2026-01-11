package com.timetablegenerator.service;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.repository.classRepo;

import java.util.List;

public class classService {
    private final classRepo repo;

    public classService(classRepo classRepository) {
        this.repo = classRepository;
    }

    public int getTotalClassCount(String search) throws Exception {
        return repo.getTotalCount(search);
    }

    public List<classModel> getClasses(int page, int pageSize, String search) throws Exception {
        int offset = (page - 1) * pageSize;
        return repo.findAll(pageSize, offset, search);
    }

    public List<classModel> getClassesForCombo() throws Exception {
        return repo.findAllForCombo();
    }

    public classModel getClassById(int id) throws Exception {
        return repo.findById(id);
    }

    public void saveClass(classModel classes) throws Exception {
        if (classes.getId() == 0) {
            boolean exists = repo.existsByNameAndDept(classes.getClass_name(), classes.getDepartment_id());
            if (exists) {
                throw new Exception("The class '" + classes.getClass_name() + "' already exists for this department.");
            }
            repo.create(classes);
        } else {
            repo.update(classes);
        }
    }

    public void deleteClass(int id) throws Exception {
        repo.softDelete(id);
    }
}
