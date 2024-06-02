package org.fatmansoft.teach.models;


import javax.persistence.*;

@Entity
@Table(name = "activity_student")
public class ActivityStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityStudentId;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String signUp = "否";

    public Integer getActivityStudentId() {
        return activityStudentId;
    }

    public void setActivityStudentId(Integer activityStudentId) {
        this.activityStudentId = activityStudentId;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getSignUp() {
        return signUp;
    }

    public void setSignUp(String signUp) {
        this.signUp = signUp;
    }
}