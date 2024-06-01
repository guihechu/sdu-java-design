package org.fatmansoft.teach.models;
import javax.persistence.*;
import java.util.List;

/**请假功能
 * leaveId 主键
 * Person person 关联到该用户所用的Person对象，账户所对应的人员信息 person_id 关联 person 表主键 person_id
 * String reason 请假理由
 * LocalDate startDate 请假开始日期
 * LocalDate endDate 请假结束日期
 * String teacherName 审批教师
 * String teacherPhone 审批教师电话
 * Integer status 申请状态
 */

@Entity
@Table(	name = "Leave")
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer leaveId;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "leave_id")
    private List<StudentLeave> studentLeave;
    private String startDate;
    private String endDate;

    private String leaveReason;
    private String examine;
    public Integer getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }




    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
    public List<StudentLeave> getStudentLeave() {
        return studentLeave;
    }

    public void setStudentLeave(List<StudentLeave> studentLeave) {
        this.studentLeave = studentLeave;
    }
    public void setExamine(String examine) {
        this.examine = examine;
    }

    public String getExamine() {
        return examine;
    }

}
