package com.timetablegenerator.service;

import com.timetablegenerator.model.academicLevelModel;
import com.timetablegenerator.repository.academicLevelRepo;
import java.util.List;

public class academicLevelService {
    private final academicLevelRepo repo;

    public academicLevelService(academicLevelRepo alRepository) {
        this.repo = alRepository;
    }

    public int getTotalAcademicLevelCount(String search) throws Exception {
        return repo.getTotalCount(search);
    }

    public List<academicLevelModel> getAcademicLevel(int page, int pageSize, String search) throws Exception {
        int offset = (page - 1) * pageSize;
        return repo.findAll(pageSize, offset, search);
    }

    public List<academicLevelModel> getAcademicLevelForCombo() throws Exception {
        return repo.findAllForCombo();
    }

    public academicLevelModel getAcademicLevelById(int id) throws Exception {
        return repo.findById(id);
    }

    public void saveAcademicLevel(academicLevelModel al) throws Exception {
        if (al.getId() != null && al.getId() > 0) {
            repo.update(al);
        } else {
            repo.create(al);
        }
    }

    public void deleteAcademicLevel(int id) throws Exception {
        repo.softDelete(id);
    }
}
