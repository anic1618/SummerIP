package com.example.iiitd.ip1.model;

import java.io.Serializable;

/**
 * Created by iiitd on 22/7/17.
 */

public class User implements Serializable {
    private int id;
    private String email;
    private String username;
    private String first_name;
    private String last_name;
    boolean is_superuser;
    private String url;
    private String data;
    private String meter_id;

    public void setMeter_id(String meter_id) {
        this.meter_id = meter_id;
    }

    public String getMeter_id() {

        return meter_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }




    public User() {

    }

    public User(int id, String username, String email, String first_name, String last_name, boolean is_superuser) {
        this.id = id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.is_superuser = is_superuser;

        this.username = username;
    }

    public User(int id, String username, String email, String first_name, String last_name) {
        this.id = id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.is_superuser = false;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public boolean is_superuser() {
        return is_superuser;
    }

    public void setIs_superuser(boolean is_superuser) {
        this.is_superuser = is_superuser;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUrl() {

        return url;
    }

    public String getData() {
        return data;
    }
}
