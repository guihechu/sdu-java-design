package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "absent",
        uniqueConstraints = {
        })
public class Absent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer absentId;
    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;
    private Integer time;

    public Integer getAbsentId() {
        return absentId;
    }

    public void setAbsentId(Integer checkId) {
        this.absentId = absentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
