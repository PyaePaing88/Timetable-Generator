package com.timetablegenerator.model;

public class timetableAssignmentModel {
    private Integer id;
    private Integer timetable_id;
    private Integer user_id;
    private Integer course_id;
    private Integer timeSlot_id;

    private Integer tempClassId;

    public Integer getTempClassId() {
        return tempClassId;
    }

    public void setTempClassId(Integer tempClassId) {
        this.tempClassId = tempClassId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTimetable_id() {
        return timetable_id;
    }

    public void setTimetable_id(Integer timetable_id) {
        this.timetable_id = timetable_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getCourse_id() {
        return course_id;
    }

    public void setCourse_id(Integer course_id) {
        this.course_id = course_id;
    }

    public Integer getTimeSlot_id() {
        return timeSlot_id;
    }

    public void setTimeSlot_id(Integer timeSlot_id) {
        this.timeSlot_id = timeSlot_id;
    }
}
