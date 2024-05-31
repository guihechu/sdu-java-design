package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api/studentCourse")
public class StudentCourseController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private StudentRepository studentRepository;
    Integer studentId = null;
    @PostMapping("/getCourseList")
    public DataResponse getCourseList(@Valid @RequestBody DataRequest dataRequest){
        String studentNum = dataRequest.getString("studentNum");
        Optional<Student> sOp = studentRepository.findByPersonNum(studentNum);
        studentId = sOp.get().getStudentId();
        List dataList = new ArrayList<>();
        List<Score> sList = scoreRepository.findByStudentStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for(int i = 0;i < sList.size();i++){
            Map m =new HashMap();
            Score s = sList.get(i);
            if(s.getMark()!=null){
                m.put("mark",s.getMark().toString());
            }
            if(s.getRanking()!=null){
                m.put("ranking",s.getRanking().toString());
            }
            Integer courseId = s.getCourse().getCourseId();
            Optional<Course> cOp = courseRepository.findByCourseId(courseId);
            Course c = cOp.get();
            m.put("courseId",c.getCourseId());
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit());
            Object preCourse=c.getPreCourse();
            if(preCourse != null){
                m.put("preCourse",c.getPreCourse().getNum());
            }
            Object teacher = c.getTeacher();
            if(teacher != null){
                m.put("teacher",c.getTeacher().getPerson().getName());
            }
            m.put("address",c.getAddress());
            m.put("refMat",c.getRefMat());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getCourse")
    public DataResponse getCourse(@Valid @RequestBody DataRequest dataRequest){
        List dataList = new ArrayList<>();
        Map m =new HashMap<>();
        String num = dataRequest.getString("num");
        Optional<Course> op = courseRepository.findByNum(num);
        if(op.isEmpty())
            return CommonMethod.getReturnData(dataList);
        Course c =op.get();
        Integer courseId = c.getCourseId();
        Optional<Score> sOp = scoreRepository.findByStudentAndCourse(studentId,courseId);
        if(sOp.isPresent()){
            if(sOp.get().getMark()!=null)
                m.put("mark",sOp.get().getMark().toString());
            if(sOp.get().getRanking()!=null)
                m.put("ranking",sOp.get().getRanking().toString());
        }
        m.put("courseId",courseId);
        m.put("num",c.getNum());
        m.put("name",c.getName());
        m.put("credit",c.getCredit());
        Object preCourse=c.getPreCourse();
        if(preCourse != null){
            m.put("preCourse",c.getPreCourse().getNum());
        }
        Object teacher = c.getTeacher();
        if(teacher != null){
            m.put("teacher",c.getTeacher().getPerson().getName());
        }
        m.put("address",c.getAddress());
        m.put("refMat",c.getRefMat());
        dataList.add(m);
        return CommonMethod.getReturnData(dataList);
    }

}
