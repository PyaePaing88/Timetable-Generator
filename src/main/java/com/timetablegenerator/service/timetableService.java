package com.timetablegenerator.service;

import com.timetablegenerator.model.*;
import com.timetablegenerator.repository.*;
import com.timetablegenerator.util.authSession;

import java.sql.SQLException;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

public class timetableService {
    private final timetableRepo repo = new timetableRepo();
    private final classRepo cRepo = new classRepo();
    private final courseRepo coRepo = new courseRepo();
    private final timeSlotRepo tsRepo = new timeSlotRepo();
    private final userRepo uRepo = new userRepo();
    private final availabilityRepo aRepo = new availabilityRepo();

    // Constant for Library/Gap filler
    private static final int LIBRARY_COURSE_ID = 0;

    public void generateWeeklyTimetable(Integer departmentId) throws SQLException {
        // 1. Data Fetching
        List<classModel> classes = cRepo.getClassesByDepartment(departmentId);
        List<timeSlotModel> allSlots = tsRepo.findAllForTimetable();
        Map<Integer, Set<Integer>> teacherBlacklist = aRepo.getUnavailableMap();
        List<Integer> sortedTeacherIds = uRepo.getTeachersByWorkload(departmentId);
        int generationYear = Year.now().getValue();

        // Map slots by Day -> Period -> Model
        Map<String, Map<Integer, timeSlotModel>> slotMap = allSlots.stream()
                .collect(Collectors.groupingBy(s -> s.getDay_of_week().toString(),
                        Collectors.toMap(timeSlotModel::getPeriod, s -> s)));

        // 2. Tracking structures for the Generation Session
        Map<Integer, Set<Integer>> teacherBusySlots = new HashMap<>();
        Map<Integer, Set<Integer>> classBusySlots = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> teacherShiftStats = new HashMap<>();

        List<timetableModel> headers = new ArrayList<>();
        List<timetableAssignmentModel> assignments = new ArrayList<>();

        // 3. Main Generation Loop
        for (classModel clazz : classes) {
            // Header preparation
            timetableModel header = new timetableModel();
            header.setDepartment_id(departmentId);
            header.setClass_id(clazz.getId());
            header.setSchedule_date(generationYear);
            headers.add(header);

            List<courseModel> courses = coRepo.getCoursesByClass(clazz.getId());

            // Daily Tracker: Ensures a course is only assigned ONCE per day (Constraint 1 & 2)
            Map<String, Set<Integer>> classDailyCourseTracker = new HashMap<>();

            for (courseModel course : courses) {
                int periodsNeeded = course.getPeriod_per_week();
                List<Integer> qualifiedTeachers = uRepo.getTeachersByCourse(course.getId());

                // Sort by workload priority (Constraint 5)
                qualifiedTeachers.sort(Comparator.comparingInt(id ->
                        sortedTeacherIds.contains(id) ? sortedTeacherIds.indexOf(id) : Integer.MAX_VALUE));

                int assignedCount = 0;

                // Step A: Assign Paired Slots (1-2 or 5-6) (Constraint 8)
                assignedCount += assignPairedSlots(clazz, course, qualifiedTeachers, slotMap,
                        assignments, teacherBusySlots, classBusySlots,
                        teacherBlacklist, teacherShiftStats, periodsNeeded, classDailyCourseTracker);

                // Step B: Assign Single Slots (3 or 4) if needed (Constraint 4)
                if (assignedCount < periodsNeeded) {
                    assignedCount += assignSingleSlots(clazz, course, qualifiedTeachers, slotMap,
                            assignments, teacherBusySlots, classBusySlots,
                            teacherBlacklist, teacherShiftStats, (periodsNeeded - assignedCount), classDailyCourseTracker);
                }
            }

            // Step C: Fill remaining gaps with Library periods (Constraint 9)
            fillLibrarySlots(clazz, slotMap, assignments, classBusySlots);
        }

        // 4. Persistence with Duplicate Validation
        repo.saveFullSchedule(headers, assignments);
    }

    private int assignPairedSlots(classModel clazz, courseModel course, List<Integer> teachers,
                                  Map<String, Map<Integer, timeSlotModel>> slotMap,
                                  List<timetableAssignmentModel> assignments,
                                  Map<Integer, Set<Integer>> tBusy, Map<Integer, Set<Integer>> cBusy,
                                  Map<Integer, Set<Integer>> blacklist,
                                  Map<Integer, Map<Integer, Integer>> stats, int limit,
                                  Map<String, Set<Integer>> dailyTracker) {
        int count = 0;
        List<String> days = new ArrayList<>(slotMap.keySet());
        Collections.shuffle(days);

        for (String day : days) {
            // Stop if we reached the limit or only have 1 period left to fill
            // (Pairs need exactly 2)
            if (count + 2 > limit) break;

            // Check if course already assigned today (Null-safe)
            if (dailyTracker.getOrDefault(day, Collections.emptySet()).contains(course.getId())) continue;

            int[][] pairs = {{1, 2}, {5, 6}};
            for (int[] pair : pairs) {
                Map<Integer, timeSlotModel> daySlots = slotMap.get(day);
                if (daySlots == null) continue;

                timeSlotModel s1 = daySlots.get(pair[0]);
                timeSlotModel s2 = daySlots.get(pair[1]);

                if (s1 == null || s2 == null) continue;

                for (Integer tId : teachers) {
                    if (canAssign(tId, clazz.getId(), s1, tBusy, cBusy, blacklist, stats) &&
                            canAssign(tId, clazz.getId(), s2, tBusy, cBusy, blacklist, stats)) {

                        recordAssignment(assignments, tId, course.getId(), s1, clazz.getId(), tBusy, cBusy, stats);
                        recordAssignment(assignments, tId, course.getId(), s2, clazz.getId(), tBusy, cBusy, stats);

                        dailyTracker.computeIfAbsent(day, k -> new HashSet<>()).add(course.getId());
                        count += 2;
                        break; // Move to next day (prevents multiple pairs of same subject same day)
                    }
                }
                // If we assigned a pair for this day, break the pairs loop to move to the next day
                if (dailyTracker.getOrDefault(day, Collections.emptySet()).contains(course.getId())) break;
            }
        }
        return count;
    }

    private int assignSingleSlots(classModel clazz, courseModel course, List<Integer> teachers,
                                  Map<String, Map<Integer, timeSlotModel>> slotMap,
                                  List<timetableAssignmentModel> assignments,
                                  Map<Integer, Set<Integer>> tBusy, Map<Integer, Set<Integer>> cBusy,
                                  Map<Integer, Set<Integer>> blacklist,
                                  Map<Integer, Map<Integer, Integer>> stats, int limit,
                                  Map<String, Set<Integer>> dailyTracker) {
        int count = 0;
        List<String> days = new ArrayList<>(slotMap.keySet());
        Collections.shuffle(days);

        for (String day : days) {
            if (count >= limit) break;

            // Null-safe check: If already taught today via pair or other single, skip
            if (dailyTracker.getOrDefault(day, Collections.emptySet()).contains(course.getId())) continue;

            int[] periods = {3, 4};
            for (int p : periods) {
                Map<Integer, timeSlotModel> daySlots = slotMap.get(day);
                if (daySlots == null) continue;

                timeSlotModel slot = daySlots.get(p);
                if (slot == null) continue;

                for (Integer tId : teachers) {
                    if (canAssign(tId, clazz.getId(), slot, tBusy, cBusy, blacklist, stats)) {
                        recordAssignment(assignments, tId, course.getId(), slot, clazz.getId(), tBusy, cBusy, stats);
                        dailyTracker.computeIfAbsent(day, k -> new HashSet<>()).add(course.getId());
                        count++;
                        break;
                    }
                }
                // Once assigned to period 3 or 4, move to the next day
                if (dailyTracker.getOrDefault(day, Collections.emptySet()).contains(course.getId())) break;
            }
        }
        return count;
    }

    private boolean canAssign(Integer tId, Integer cId, timeSlotModel s,
                              Map<Integer, Set<Integer>> tBusy, Map<Integer, Set<Integer>> cBusy,
                              Map<Integer, Set<Integer>> blacklist, Map<Integer, Map<Integer, Integer>> stats) {

        if (tBusy.getOrDefault(tId, Collections.emptySet()).contains(s.getId())) return false;
        if (cBusy.getOrDefault(cId, Collections.emptySet()).contains(s.getId())) return false;
        if (blacklist.getOrDefault(tId, Collections.emptySet()).contains(s.getId())) return false;

        // Shift Balancing (Constraint 7)
        Map<Integer, Integer> teacherStats = stats.getOrDefault(tId, new HashMap<>());
        int morning = teacherStats.getOrDefault(1, 0);
        int evening = teacherStats.getOrDefault(0, 0);
        if (s.isIs_morning_shift() && morning > (evening + 4)) return false;

        return true;
    }

    private void recordAssignment(List<timetableAssignmentModel> list, Integer tId, Integer coId,
                                  timeSlotModel slot, Integer clId, Map<Integer, Set<Integer>> tBusy,
                                  Map<Integer, Set<Integer>> cBusy, Map<Integer, Map<Integer, Integer>> stats) {
        timetableAssignmentModel am = new timetableAssignmentModel();
        am.setUser_id(tId);
        am.setCourse_id(coId);
        am.setTimeSlot_id(slot.getId());
        am.setTempClassId(clId);
        list.add(am);

        tBusy.computeIfAbsent(tId, k -> new HashSet<>()).add(slot.getId());
        cBusy.computeIfAbsent(clId, k -> new HashSet<>()).add(slot.getId());
        int shift = slot.isIs_morning_shift() ? 1 : 0;
        stats.computeIfAbsent(tId, k -> new HashMap<>()).merge(shift, 1, Integer::sum);
    }

    private void fillLibrarySlots(classModel clazz, Map<String, Map<Integer, timeSlotModel>> slotMap,
                                  List<timetableAssignmentModel> assignments, Map<Integer, Set<Integer>> cBusy) {
        for (String day : slotMap.keySet()) {
            for (int p = 1; p <= 6; p++) {
                timeSlotModel slot = slotMap.get(day).get(p);
                if (slot != null && !cBusy.getOrDefault(clazz.getId(), Collections.emptySet()).contains(slot.getId())) {
                    timetableAssignmentModel lib = new timetableAssignmentModel();
                    lib.setUser_id(null);
                    lib.setCourse_id(LIBRARY_COURSE_ID);
                    lib.setTimeSlot_id(slot.getId());
                    lib.setTempClassId(clazz.getId());
                    assignments.add(lib);
                    cBusy.get(clazz.getId()).add(slot.getId());
                }
            }
        }
    }

    /// For listing
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

    public TimetableDetailDTO getTimetableAssignmentById(Integer id) throws SQLException {
        return repo.findAssignmentsById(id);
    }

    public boolean updateTimetableAssignment(TimetableDetailDTO data) throws SQLException {
        return repo.updateTimetableAssignment(data);
    }

    public List<TimetableDetailDTO> getTimetableListTeacher() throws SQLException {
        return repo.findAssignmentsByTeacherId(authSession.getUser().getId());
    }
}