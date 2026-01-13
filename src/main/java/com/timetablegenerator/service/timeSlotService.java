package com.timetablegenerator.service;

import com.timetablegenerator.model.timeSlotModel;
import com.timetablegenerator.repository.timeSlotRepo;

import java.util.List;

public class timeSlotService {

    private final timeSlotRepo repo;

    public timeSlotService(timeSlotRepo timeRepository) {
        this.repo = timeRepository;
    }

    public int getTotalTimeSlotCount(String search) throws Exception {
        return repo.getTotalCount(search);
    }

    public List<timeSlotModel> getTimeSlot(int page, int pageSize, String search) throws Exception {
        int offset = (page - 1) * pageSize;
        return repo.findAll(pageSize, offset, search);
    }

    public timeSlotModel getTimeSlotById(int id) throws Exception {
        return repo.findById(id);
    }

    public void saveTimeSlot(timeSlotModel time) throws Exception {
        if (time.getId() != null && time.getId() > 0) {
            repo.update(time);
        } else {
            repo.create(time);
        }
    }

    public void deleteTimeSlot(int id) throws Exception {
        repo.softDelete(id);
    }
}
