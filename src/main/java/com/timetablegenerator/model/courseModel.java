package com.timetablegenerator.model;

import java.sql.Timestamp;

public class courseModel {
    private Integer id;
    private String course_name;
    private String subject_code;
    private Integer department_id;
    private String department_name;
    private Integer academicLevel_id;
    private String academicLevel;
    private boolean is_delete;
    private Integer created_by;
    private Timestamp created_date;
    private int modify_by;
    private Timestamp modify_date;
    private Integer period_per_week;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getSubject_code() {
        return subject_code;
    }

    public void setSubject_code(String subject_code) {
        this.subject_code = subject_code;
    }

    public Integer getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Integer department_id) {
        this.department_id = department_id;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public Integer getAcademicLevel_id() {
        return academicLevel_id;
    }

    public void setAcademicLevel_id(Integer academicLevel_id) {
        this.academicLevel_id = academicLevel_id;
    }

    public String getAcademicLevel() {
        return academicLevel;
    }

    public void setAcademicLevel(String academicLevel) {
        this.academicLevel = academicLevel;
    }

    public boolean isIs_delete() {
        return is_delete;
    }

    public void setIs_delete(boolean is_delete) {
        this.is_delete = is_delete;
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

    public int getModify_by() {
        return modify_by;
    }

    public void setModify_by(int modify_by) {
        this.modify_by = modify_by;
    }

    public Timestamp getModify_date() {
        return modify_date;
    }

    public void setModify_date(Timestamp modify_date) {
        this.modify_date = modify_date;
    }

    public Integer getPeriod_per_week() {
        return period_per_week;
    }

    public void setPeriod_per_week(Integer period_per_week) {
        this.period_per_week = period_per_week;
    }
}
