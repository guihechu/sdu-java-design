package com.teach.javafxclient.model;

public class Course {
    private Integer courseId;
    private String num;
    private String name;
    private String preCourse;
    public Course(){

    }
    public Course(Integer courseId,String num,String name,String preCourse){
        this.courseId = courseId;
        this.num = num;
        this.name = name;
        this.preCourse = preCourse;
    }
    public Course(Integer courseId,String num,String name){
        this.courseId = courseId;
        this.num = num;
        this.name = name;
        preCourse = null;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreCourse() {
        return preCourse;
    }

    public void setPreCourseId(String preCourse) {
        this.preCourse = preCourse;
    }
    public String toString(){return num+"-"+name;}
}
