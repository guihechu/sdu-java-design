package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Absent;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.AbsentRepository;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api/absent")
public class AbsentController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AbsentRepository absentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;

    public synchronized Integer getNewAbsentId() {
        Integer id = absentRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    @PostMapping("/getAbsentListByNum")
    public DataResponse getAbsentListByNum(@Valid @RequestBody DataRequest dataRequest) {
        String studentNum = dataRequest.getString("studentNum");
        Optional<Student> sOp =studentRepository.findByPersonNum(studentNum);
        Integer studentId;
        if(sOp.isPresent()){
            studentId = sOp.get().getStudentId();
        }else{
            studentId = 0;
        }
        String courseNum = dataRequest.getString("courseNum");
        Optional<Course> cOp= courseRepository.findByNum(courseNum);
        Integer courseId;
        if(cOp.isPresent()){
            courseId = cOp.get().getCourseId();
        }else {
            courseId = 0;
        }
        List<Absent> sList;
        if(studentId != 0&&courseId == 0) sList = absentRepository.findByStudentStudentId(studentId);
        else sList = absentRepository.findByStudentCourse(studentId, courseId);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        for (Absent a : sList) {
            m = new HashMap();
            m.put("absentId", a.getAbsentId()+"");
            m.put("studentId",a.getStudent().getStudentId()+"");
            m.put("courseId",a.getCourse().getCourseId()+"");
            m.put("studentNum",a.getStudent().getPerson().getNum());
            m.put("studentName",a.getStudent().getPerson().getName());
            m.put("className",a.getStudent().getClassName());
            m.put("courseNum",a.getCourse().getNum());
            m.put("courseName",a.getCourse().getName());
            m.put("time",""+a.getTime());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/absentDelete")
    public DataResponse absentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer absentId = dataRequest.getInteger("absentId");
        Absent s = null;
        Optional<Absent> op;
        if (absentId != null) {
            op = absentRepository.findById(absentId);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            absentRepository.delete(s);
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/absentSave")
    public DataResponse absentSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId= dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer time = dataRequest.getInteger("time");
        Integer absentId = dataRequest.getInteger("absentId");
        Optional<Absent> op;
        Absent a = null;
        if (absentId != null) {
            op = absentRepository.findById(absentId);
            if (op.isPresent()) {
                a = op.get();
            }
        }
        if (a == null) {
            Optional<Score> sOp = scoreRepository.findByStudentAndCourse(studentId,courseId);
            if(sOp.isEmpty()){
                return CommonMethod.getReturnMessageError("学生未选择改课程，不能添加！");
            }
            List<Absent> aList = absentRepository.findByStudentCourse(studentId,courseId);
            if(aList.size()!=0){
                return CommonMethod.getReturnMessageError("已有该选项，不能添加！");
            }
            a = new Absent();
            a.setAbsentId(getNewAbsentId());
            Student student = null;
            Optional<Student> sop = studentRepository.findById(studentId);
            if (sop.isPresent()) {
                student = sop.get();
            }
            Course course = null;
            Optional<Course> cop = courseRepository.findById(courseId);
            if (cop.isPresent()) {
                course = cop.get();
            }
            a.setStudent(student);
            a.setCourse(course);
        }
        a.setTime(time);
        absentRepository.save(a);
        return CommonMethod.getReturnMessageOK();
    }





}
