package com.timetablegenerator.service;

import com.timetablegenerator.model.*;
import com.timetablegenerator.repository.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.*;

public class timetableService {
    private final timetableRepo repo = new timetableRepo();
    private final classRepo cRepo = new classRepo();
    private final courseRepo coRepo = new courseRepo();
    private final timeSlotRepo tsRepo = new timeSlotRepo();
    private final userRepo uRepo = new userRepo();

    public void generateWeeklyTimetable(Integer departmentId) throws SQLException {
        List<Integer> classIds = cRepo.getClassesIdByDepartment(departmentId);
        List<timeSlotModel> slots = tsRepo.findAllForTimetable();
        Timestamp generationTime = new Timestamp(System.currentTimeMillis());
        Map<Integer, List<Integer>> courseTeachersMap = new HashMap<>();

        List<timetableModel> headers = new ArrayList<>();
        List<timetableAssignmentModel> assignments = new ArrayList<>();

        for (Integer classId : classIds) {
            timetableModel header = new timetableModel();
            header.setDepartment_id(departmentId);
            header.setClass_id(classId);
            header.setSchedule_date(generationTime);
            headers.add(header);

            List<Integer> courseIds = coRepo.getCourseIdByDepartment(classId);
            courseIds.sort(Comparator.comparingInt(cId -> getTeachersForCourse(courseTeachersMap, cId).size()));

            for (Integer courseId : courseIds) {
                int assignedInWeek = 0;
                List<Integer> teachers = getTeachersForCourse(courseTeachersMap, courseId);

                for (timeSlotModel slot : slots) {
                    if (assignedInWeek >= 4) break;

                    for (Integer teacherId : teachers) {
                        if (canAssign(teacherId, classId, slot, generationTime, assignedInWeek)) {
                            timetableAssignmentModel assign = new timetableAssignmentModel();
                            assign.setUser_id(teacherId);
                            assign.setCourse_id(courseId);
                            assign.setTimeSlot_id(slot.getId());
                            assign.setTempClassId(classId); // Link to header

                            assignments.add(assign);
                            assignedInWeek++;
                            break;
                        }
                    }
                }
            }
        }
        // Atomic save: All or nothing
        repo.saveFullSchedule(headers, assignments);
    }

    private boolean canAssign(Integer tId, Integer cId, timeSlotModel slot, Timestamp date, int count) throws SQLException {
        if (repo.hasConflict(tId, cId, slot.getId(), date)) return false;
        boolean isMorning = slot.getStart_time().toLocalTime().isBefore(LocalTime.NOON);
        if (repo.hasShiftConflict(tId, cId, date, !isMorning)) return false;
        if (count == 3) {
            int shiftCount = repo.countDistinctShiftsThisWeek(tId, date);
            if (shiftCount == 1 && repo.isSameShiftAsPrevious(tId, isMorning, date)) return false;
        }
        return repo.isDayContiguous(tId, slot.getDay_of_week().name(), date);
    }

    private List<Integer> getTeachersForCourse(Map<Integer, List<Integer>> map, Integer courseId) {
        return map.computeIfAbsent(courseId, id -> {
            try {
                return uRepo.getTeachersByCourse(id);
            } catch (SQLException e) {
                return new ArrayList<>();
            }
        });
    }
}