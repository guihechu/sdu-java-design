package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;
import java.util.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api/score")
public class ScoreController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private BaseService baseService;

    public synchronized Integer getNewScoreId(){
        Integer  id = scoreRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };

    @PostMapping("/getScoreList")
    public DataResponse getScoreList(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if(studentId == null)
            studentId = 0;
        Integer courseId = dataRequest.getInteger("courseId");
        if(courseId == null)
            courseId = 0;
        List<Score> sList = scoreRepository.findByStudentCourse(studentId, courseId);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        for (Score s : sList) {
            m = new HashMap();
            m.put("scoreId", s.getScoreId()+"");
            m.put("studentId",s.getStudent().getStudentId()+"");
            m.put("courseId",s.getCourse().getCourseId()+"");
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("studentName",s.getStudent().getPerson().getName());
            m.put("className",s.getStudent().getClassName());
            m.put("courseNum",s.getCourse().getNum());
            m.put("courseName",s.getCourse().getName());
            m.put("credit",""+s.getCourse().getCredit());
            m.put("mark",""+s.getMark());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getScoreListByNum")
    public DataResponse getScoreListByNum(@Valid @RequestBody DataRequest dataRequest) {
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
        List<Score> sList;
        if(studentId != 0&&courseId == 0) sList = scoreRepository.findByStudentStudentId(studentId);
        else sList = scoreRepository.findByStudentCourse(studentId, courseId);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        for (Score s : sList) {
            m = new HashMap();
            m.put("scoreId", s.getScoreId()+"");
            m.put("studentId",s.getStudent().getStudentId()+"");
            m.put("courseId",s.getCourse().getCourseId()+"");
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("studentName",s.getStudent().getPerson().getName());
            m.put("className",s.getStudent().getClassName());
            m.put("courseNum",s.getCourse().getNum());
            m.put("courseName",s.getCourse().getName());
            m.put("credit",""+s.getCourse().getCredit());
            m.put("mark",""+s.getMark());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/scoreDelete")
    public DataResponse scoreDelete(@Valid @RequestBody DataRequest dataRequest){
        Integer scoreId = dataRequest.getInteger("scoreId");
        Score s = null;
        Optional<Score> op;
        if(scoreId != null){
            op = scoreRepository.findById(scoreId);
            if(op.isPresent()){
                s = op.get();
            }
        }
        if(s != null){
            scoreRepository.delete(s);
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/scoreSave")
    public DataResponse scoreSave(@Valid @RequestBody DataRequest dataRequest){
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer mark = dataRequest.getInteger("mark");
        Integer scoreId = dataRequest.getInteger("scoreId");
        Optional<Score> op;
        Score s =null;
        if(scoreId != null){
            op = scoreRepository.findById(scoreId);
            if(op.isPresent()){
                s = op.get();
            }
        }
        if(s == null){
            s = new Score();
            s.setScoreId(getNewScoreId());
            Student student = null;
            Optional<Student> sop = studentRepository.findByPersonNum(studentId.toString());
            if(sop.isPresent()){
                student = sop.get();
            }
            Course course = null;
            Optional<Course> cop= courseRepository.findById(courseId);
            if(cop.isPresent()){
                course = cop.get();
            }
            s.setStudent(student);
            s.setCourse(course);
        }
        s.setMark(mark);
        scoreRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }



}
