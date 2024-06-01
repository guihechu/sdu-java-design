package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(   name = "rejectLeave",
        uniqueConstraints = {
        })
public class RejectLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rejectLeaveId;
    @ManyToOne
    @JoinColumn(name = "studentLeave_id")
    private StudentLeave studentLeave;

    public Integer getRejectLeaveId() {
        return rejectLeaveId;
    }

    public void setRejectLeaveId(Integer rejectLeaveId) {
        this.rejectLeaveId = rejectLeaveId;
    }

    public StudentLeave getStudentLeave() {
        return studentLeave;
    }

    public void setStudentLeave(StudentLeave studentLeave) {
        this.studentLeave = studentLeave;
    }
}