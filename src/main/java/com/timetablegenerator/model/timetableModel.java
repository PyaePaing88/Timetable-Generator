package com.timetablegenerator.model;

import java.sql.Timestamp;

public class timetableModel {
    private Integer id;
    private Integer department_id;
    private Timestamp schedule_date;
    private Integer class_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Integer department_id) {
        this.department_id = department_id;
    }

    public Timestamp getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(Timestamp schedule_date) {
        this.schedule_date = schedule_date;
    }

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }
}
