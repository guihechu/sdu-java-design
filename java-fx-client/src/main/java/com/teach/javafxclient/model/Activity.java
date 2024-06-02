package com.teach.javafxclient.model;
import javax.swing.*;
import javax.xml.namespace.QName;
import java.time.LocalDateTime;

public class Activity {
    private Integer activityId;
    private String num;
    private String name;
    private String description;
    private String date;
    private String location;
    private String signUp;
    private String signUpName;


    public Activity(Integer activityId, String name, String date, String location, String num) {
        this.activityId = activityId;
        this.name = name;
        this.date = date;
        this.location = location;
        this.num = num;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        activityId = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNum() { return num; }

    public void setNum(String num) { this.num = num; }

    public String getSignUpName() {
        return signUpName;
    }

    public void setSignUpName(String signUpName) {
        this.signUpName = signUpName;
    }

    public String getSignUp() {
        return signUp;
    }

    public void setSignUp(String signUp) {
        this.signUp = signUp;
    }
}
