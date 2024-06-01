package org.fatmansoft.teach.models;
import javax.persistence.*;

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
@Table(	name = "studentLeave")
public class StudentLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentLeaveId;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "leave_id")
    private Leave leave;

    public Integer getStudentLeaveId() {
        return studentLeaveId;
    }

    public void setStudentLeaveId(Integer studentLeaveId) {
        this.studentLeaveId = studentLeaveId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Leave getLeave() {
        return leave;
    }

    public void setLeave(Leave leave) {
        this.leave = leave;
    }
}
