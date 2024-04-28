package com.teach.javafxclient.model;

/**
 * Student学生表实体类 保存每个学生的西悉尼 继承Person类，
 * Integer studentId 用户表 student 主键 student_id
 * String major 专业
 * String className 班级
 *
 */

public class Teacher extends Person{
    private Integer teacherId;
    private String title;
    private String degree;
    public Teacher(){
        super();
    }
    public Teacher(Integer teacherId,Integer personId,String num, String name){
        super(personId,num,name);
        this.teacherId = teacherId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String Degree) {
        this.degree = degree;
    }
}
