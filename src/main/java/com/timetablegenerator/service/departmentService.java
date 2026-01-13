package com.timetablegenerator.service;

import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.departmentRepo;
import java.util.List;

public class departmentService {
    private final departmentRepo repo;

    public departmentService(departmentRepo deptRepository) {
        this.repo = deptRepository;
    }

    public int getTotalDepartmentCount(String search) throws Exception {
        return repo.getTotalCount(search);
    }

    public List<departmentModel> getDepartments(int page, int pageSize, String search) throws Exception {
        int offset = (page - 1) * pageSize;
        return repo.findAll(pageSize, offset, search);
    }


    public List<departmentModel> getMajorDepartments() throws Exception {
        return repo.findAllMajor();
    }

    public List<departmentModel> getMinorDepartments() throws Exception {
        return repo.findAllMinor();
    }

    public departmentModel getDepartmentById(int id) throws Exception {
        return repo.findById(id);
    }

    public void saveDepartment(departmentModel dept) throws Exception {
        if (dept.getId() > 0) {
            repo.update(dept);
        } else {
            repo.create(dept);
        }
    }

    public void deleteDepartment(int id) throws Exception {
        repo.softDelete(id);
    }
}
