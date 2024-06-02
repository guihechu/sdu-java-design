package org.fatmansoft.teach.models;

import javax.persistence.*;

/**
 * Honor 荣誉信息实体类  保存学生所获荣誉的基本信息信息，
 * Integer honorId 人员表 honor 主键 honor_id
 * Integer studentId  student_id 对应student 表里面的 student_id
 * String honorNum 荣誉编号
 * String honorDay 荣誉获得时间
 */
@Entity
@Table(    name = "honor" ,
        uniqueConstraints = {
        })

public class Honor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer honorId;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    private String honorNum;
    private String honorType;
    private String honorGrade;
    private String honorName;
    private String honorDay;




    public Integer getHonorId() { return honorId; }
    public void setHonorId(Integer honorId) {
        this.honorId = honorId;
    }


    public String getHonorNum() {
        return honorNum;
    }
    public void setHonorNum(String honorNum) { this.honorNum = honorNum; }

    public String getHonorName() { return honorName; }
    public void setHonorName(String honorName) {
        this.honorName = honorName;
    }

    public String getHonorDay() { return honorDay; }
    public void setHonorDay(String honorDay) { this.honorDay = honorDay; }

    public String getHonorGrade() {
        return honorGrade;
    }
    public void setHonorGrade(String honorGrade) {
        this.honorGrade = honorGrade;
    }

    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }

    public String getHonorType() {
        return honorType;
    }

    public void setHonorType(String honorType) {
        this.honorType = honorType;
    }
}

