package com.timetablegenerator.model;

import java.sql.Time;
import java.sql.Timestamp;

public class timeSlotModel {
    private Integer id;
    private day day_of_week;
    private Integer period;
    private Time start_time;
    private Time end_time;
    private Integer created_by;
    private Timestamp created_date;
    private Integer modify_by;
    private Timestamp modify_date;
    private boolean is_delete;
    private boolean is_morning_shift;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public day getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(day day_of_week) {
        this.day_of_week = day_of_week;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Time getStart_time() {
        return start_time;
    }

    public void setStart_time(Time start_time) {
        this.start_time = start_time;
    }

    public Time getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Time end_time) {
        this.end_time = end_time;
    }

    public Integer getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
    }

    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }

    public Integer getModify_by() {
        return modify_by;
    }

    public void setModify_by(Integer modify_by) {
        this.modify_by = modify_by;
    }

    public Timestamp getModify_date() {
        return modify_date;
    }

    public void setModify_date(Timestamp modify_date) {
        this.modify_date = modify_date;
    }

    public boolean isIs_delete() {
        return is_delete;
    }

    public void setIs_delete(boolean is_delete) {
        this.is_delete = is_delete;
    }

    public boolean isIs_morning_shift() {
        return is_morning_shift;
    }

    public void setIs_morning_shift(boolean is_morning_shift) {
        this.is_morning_shift = is_morning_shift;
    }

}
