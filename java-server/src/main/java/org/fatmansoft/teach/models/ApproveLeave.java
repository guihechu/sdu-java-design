package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(    name = "approveLeave",
        uniqueConstraints = {
        })
public class ApproveLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer approveLeaveId;
    @ManyToOne
    @JoinColumn(name = "studentLeave_id")
    private StudentLeave studentLeave;

    public Integer getApproveLeaveId() {
        return approveLeaveId;
    }

    public void setApproveLeaveId(Integer approveLeaveId) {
        this.approveLeaveId = approveLeaveId;
    }

    public StudentLeave getStudentLeave() {
        return studentLeave;
    }

    public void setStudentLeave(StudentLeave studentLeave) {
        this.studentLeave = studentLeave;
    }
}