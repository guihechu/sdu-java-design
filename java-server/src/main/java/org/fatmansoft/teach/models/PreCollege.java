package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "preCollege",
        uniqueConstraints = {
        })
public class PreCollege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer infoId;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Size(max = 7)
    private String political;

    @Size(max = 15)
    private String highSchool;

    private String graduation;

    @Size(max =30)
    private String health;

    @Size(max = 30)
    private String hometown;

    @Size(max = 7)
    private String nation;

    @Size(max = 40)
    private String change;

    public Integer getInfoId() {
        return infoId;
    }

    public void setInfoId(Integer infoId) {
        this.infoId = infoId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getPolitical() {
        return political;
    }

    public void setPolitical(String political) {
        this.political = political;
    }

    public String getHighSchool() {
        return highSchool;
    }

    public void setHighSchool(String highSchool) {
        this.highSchool = highSchool;
    }

    public String getGraduation() {
        return graduation;
    }

    public void setGraduation(String graduation) {
        this.graduation = graduation;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String homeTown) {
        this.hometown = homeTown;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
