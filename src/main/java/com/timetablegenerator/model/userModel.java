package com.timetablegenerator.model;

import java.sql.Timestamp;

public class userModel {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String password;
    private int department_id;
    private String department_name;
    private role role;
    private boolean is_active;
    private boolean is_delete;
    private int created_by;
    private Timestamp created_date;
    private int modify_by;
    private Timestamp modify_date;
    private boolean change_password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public com.timetablegenerator.model.role getRole() {
        return role;
    }

    public void setRole(com.timetablegenerator.model.role role) {
        this.role = role;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public boolean isIs_delete() {
        return is_delete;
    }

    public void setIs_delete(boolean is_delete) {
        this.is_delete = is_delete;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_ate) {
        this.created_date = created_ate;
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

    public boolean isChange_password() {
        return change_password;
    }

    public void setChange_password(boolean change_password) {
        this.change_password = change_password;
    }
}
