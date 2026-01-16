package com.timetablegenerator.service;

import com.timetablegenerator.model.availabilityModel;
import com.timetablegenerator.repository.availabilityRepo;

import java.util.List;

public class availabilityService {
    private final availabilityRepo repo;

    public availabilityService(availabilityRepo repo) {
        this.repo = repo;
    }

    public int getTotalAvailabilityCount(String search) throws Exception {
        return repo.getTotalCount(search);
    }

    public List<availabilityModel> getAvailabilities(int page, int pageSize, String search) throws Exception {
        int offset = (page - 1) * pageSize;
        return repo.findAll(pageSize, offset, search);
    }

    public int getTotalAvailabilityCountByTeacher(String search) throws Exception {
        return repo.getTotalCountByTeacher(search);
    }

    public List<availabilityModel> getAvailabilityByTeacherId(int page, int pageSize, String search) throws Exception {
        int offset = (page - 1) * pageSize;
        return repo.findAllByTeacher(pageSize, offset, search);
    }

    public availabilityModel getAvailabilityById(int id) throws Exception {
        return repo.findById(id);
    }

    public void saveAvailability(availabilityModel availability) throws Exception {
        if (availability.getId() != null && availability.getId() > 0) {
            repo.update(availability);
        } else {
            repo.create(availability);
        }
    }

    public void deleteAvailability(int id) throws Exception {
        repo.softDelete(id);
    }
}