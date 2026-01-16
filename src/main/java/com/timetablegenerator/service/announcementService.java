package com.timetablegenerator.service;

import com.timetablegenerator.model.announcementModel;
import com.timetablegenerator.repository.announcementRepo;

import java.util.List;

public class announcementService {
    private final announcementRepo repo;

    public announcementService(announcementRepo repo) {
        this.repo = repo;
    }

    public int getTotalAnnouncementCount(String search) throws Exception {
        return repo.getTotalCount(search);
    }

    public List<announcementModel> getAnnouncements(int page, int pageSize, String search) throws Exception {
        int offset = (page - 1) * pageSize;
        return repo.findAll(pageSize, offset, search);
    }

    public announcementModel getAnnouncementById(int id) throws Exception {
        return repo.findById(id);
    }

    public void saveAnnouncement(announcementModel announcement) throws Exception {
        if (announcement.getId() != null && announcement.getId() > 0) {
            repo.update(announcement);
        } else {
            repo.create(announcement);
        }
    }

    public void deleteAnnouncement(int id) throws Exception {
        repo.softDelete(id);
    }
}