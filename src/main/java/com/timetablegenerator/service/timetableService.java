package com.timetablegenerator.service;

import com.timetablegenerator.model.*;
import com.timetablegenerator.repository.*;

import java.sql.SQLException;
import java.time.Year;
import java.util.*;

public class timetableService {
    private final timetableRepo repo = new timetableRepo();
    private final classRepo cRepo = new classRepo();
    private final courseRepo coRepo = new courseRepo();
    private final timeSlotRepo tsRepo = new timeSlotRepo();
    private final userRepo uRepo = new userRepo();

    public void generateWeeklyTimetable(Integer departmentId) throws SQLException {
        List<Integer> classIds = cRepo.getClassesIdByDepartment(departmentId);
        List<timeSlotModel> allSlots = tsRepo.findAllForTimetable();
        int generationYear = Year.now().getValue();

        System.out.println("\n--- STARTING TIMETABLE GENERATION ---");
        System.out.println("Target: " + (classIds.size() * 30) + " total assignments.");

        List<timetableModel> headers = new ArrayList<>();
        List<timetableAssignmentModel> assignments = new ArrayList<>();

        Map<Integer, Set<Integer>> teacherBusySlots = new HashMap<>();
        Map<Integer, Set<Integer>> classBusySlots = new HashMap<>();
        Map<Integer, List<timeSlotModel>> teacherSchedule = new HashMap<>();
        Map<Integer, Integer> workloadCache = new HashMap<>();

        // Create a copy for shuffling
        List<timeSlotModel> shuffledSlots = new ArrayList<>(allSlots);

        for (Integer classId : classIds) {
            System.out.println("\n>> PROCESSING CLASS: " + classId);

            timetableModel header = new timetableModel();
            header.setDepartment_id(departmentId);
            header.setClass_id(classId);
            header.setSchedule_date(generationYear);
            headers.add(header);

            List<Integer> courseIds = coRepo.getCourseIdByClass(classId);

            for (Integer courseId : courseIds) {
                int assignedForThisCourse = 0;
                List<Integer> teachers = uRepo.getTeachersByCourse(courseId);

                // KEY: Shuffle for every course to spread teachers across the week
                Collections.shuffle(shuffledSlots);

                for (timeSlotModel slot : shuffledSlots) {
                    if (assignedForThisCourse >= 5) break;

                    for (Integer tId : teachers) {
                        if (isSafeToAssign(tId, classId, slot, teacherSchedule, teacherBusySlots, classBusySlots)) {

                            timetableAssignmentModel assign = new timetableAssignmentModel();
                            assign.setUser_id(tId);
                            assign.setCourse_id(courseId);
                            assign.setTimeSlot_id(slot.getId());
                            assign.setTempClassId(classId);

                            assignments.add(assign);

                            // Mark as busy
                            classBusySlots.computeIfAbsent(classId, k -> new HashSet<>()).add(slot.getId());
                            teacherBusySlots.computeIfAbsent(tId, k -> new HashSet<>()).add(slot.getId());
                            teacherSchedule.computeIfAbsent(tId, k -> new ArrayList<>()).add(slot);

                            assignedForThisCourse++;
                            break;
                        } else {
                            // Debugging: Why did it fail?
                            if (teacherBusySlots.getOrDefault(tId, Collections.emptySet()).contains(slot.getId())) {
                                // Teacher is teaching another class at this time
                            }
                        }
                    }
                }

                if (assignedForThisCourse < 5) {
                    System.err.println("   [ALERT] Course " + courseId + " in Class " + classId +
                            " only got " + assignedForThisCourse + "/5 slots! (Check Teacher Overlap)");
                } else {
                    System.out.println("   [OK] Course " + courseId + " assigned 5 slots.");
                }
            }
            System.out.println(">> FINISHED CLASS " + classId + ". Current Total: " + assignments.size());
        }

        System.out.println("\n--- GENERATION SUMMARY ---");
        System.out.println("Total Classes: " + classIds.size());
        System.out.println("Total Assignments: " + assignments.size());

        if (assignments.size() < (classIds.size() * 30)) {
            System.err.println("WARNING: Target not met. Some teachers are likely double-booked in your database.");
        } else {
            System.out.println("SUCCESS: 180/180 records generated!");
        }

        repo.saveFullSchedule(headers, assignments);
    }

    private boolean isSafeToAssign(Integer tId, Integer cId, timeSlotModel slot,
                                   Map<Integer, List<timeSlotModel>> tSched,
                                   Map<Integer, Set<Integer>> tBusy,
                                   Map<Integer, Set<Integer>> cBusy) {

        int sId = slot.getId();
        List<timeSlotModel> currentTeacherSlots = tSched.getOrDefault(tId, new ArrayList<>());

        if (tBusy.getOrDefault(tId, Collections.emptySet()).contains(sId)) return false;
        if (cBusy.getOrDefault(cId, Collections.emptySet()).contains(sId)) return false;

        boolean hasSameDayDifferentShift = currentTeacherSlots.stream()
                .anyMatch(s -> s.getDay_of_week().equals(slot.getDay_of_week()) &&
                        s.isIs_morning_shift() != slot.isIs_morning_shift());
        if (hasSameDayDifferentShift) return false;

        if (!currentTeacherSlots.isEmpty()) {
            boolean isContiguous = false;
            List<String> week = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
            int newDayIdx = week.indexOf(slot.getDay_of_week());
            for (timeSlotModel s : currentTeacherSlots) {
                if (Math.abs(newDayIdx - week.indexOf(s.getDay_of_week())) <= 1) {
                    isContiguous = true;
                    break;
                }
            }
            if (!isContiguous) return false;
        }

        if (currentTeacherSlots.size() > 3) {
            long morningCount = currentTeacherSlots.stream().filter(timeSlotModel::isIs_morning_shift).count();
            if (morningCount == currentTeacherSlots.size() && slot.isIs_morning_shift()) return false;
        }

        return true;
    }

    public Map<String, List<TimetableDetailDTO>> getTimetableGrid(Integer timetableId) throws SQLException {
        List<TimetableDetailDTO> assignments = repo.findAssignmentsByTimetableId(timetableId);

        Map<String, List<TimetableDetailDTO>> weeklyGrid = new LinkedHashMap<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        for (String day : days) {
            weeklyGrid.put(day, new ArrayList<>());
        }

        for (TimetableDetailDTO dto : assignments) {
            if (weeklyGrid.containsKey(dto.getDay())) {
                weeklyGrid.get(dto.getDay()).add(dto);
            }
        }
        return weeklyGrid;
    }

    public List<TimetableDetailDTO> getTimetableListByHeader(Integer timetableId) throws SQLException {
        return repo.findAssignmentsByTimetableId(timetableId);
    }

    // Add these to timetableService.java
    public int getTotalTimetableCount(String search) throws SQLException {
        return repo.getTotalTimetableCount(search);
    }

    public List<TimetableCardDTO> getTimetablesPaginated(int page, int size, String search) throws SQLException {
        return repo.getTimetablesPaginated(page, size, search);
    }
}