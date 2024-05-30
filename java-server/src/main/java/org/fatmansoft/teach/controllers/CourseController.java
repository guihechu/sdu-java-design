package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
@CrossOrigin(origins="*",maxAge = 3600)
@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    public synchronized Integer getNewCourseId(){
        Integer id = courseRepository.getMaxId();
        if(id==null)
            id = 1;
        else
            id = id+1;
        return id;
    }
    public Map getMapFromCourse(Course c){
        Map m = new HashMap();
        if(c==null)
            return m;
        m.put("courseId",c.getCourseId());
        m.put("num",c.getNum());
        m.put("name",c.getName());
        m.put("credit",c.getCredit());
        Object preCourse=c.getPreCourse();
        if(preCourse != null){
            m.put("preCourse",c.getPreCourse().getName());
        }
        Object teacher = c.getTeacher();
        if(teacher != null){
            m.put("teacher",c.getTeacher().getPerson().getName());
        }
        m.put("address",c.getAddress());
        m.put("refMat",c.getRefMat());
        return m;
    }

    public Map getMapInfoFromCourse(Course c){
        Map m = new HashMap();
        if(c==null)
            return m;
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
        return m;
    }

    public List getCourseMapList(String numName){
        List dataList = new ArrayList();
        List<Course> cList = courseRepository.findCourseListByNumName(numName);
        if(cList == null || cList.size() == 0)
            return dataList;
        for(int i = 0; i < cList.size();i++){
            dataList.add(getMapFromCourse(cList.get(i)));
        }
        return dataList;
    }

    @PostMapping("/getCourseItemOptionList")
    public OptionItemList getCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest){
        List<Course> cList = courseRepository.findCourseListByNumName("");
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for(Course c : cList){
            itemList.add(new OptionItem(c.getCourseId(),c.getNum(),c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0,itemList);
    }

    @PostMapping("/getCourseList")
    public DataResponse getCourseList(@Valid @RequestBody DataRequest dataRequest){
        String numName = dataRequest.getString("numName");
        List dataList = getCourseMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/courseDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest){
        Integer courseId = dataRequest.getInteger("courseId");
        Course c = null;
        Optional<Course> op;
        if(courseId != null){
            op = courseRepository.findByCourseId(courseId);
            if(op.isPresent()){
                c = op.get();
                if(c != null){
                    courseRepository.delete(c);
                }
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/getCourseInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getCourseInfo(@Valid @RequestBody DataRequest dataRequest){
        Integer courseId = dataRequest.getInteger("courseId");
        Course c = null;
        Optional<Course> op;
        if(courseId != null){
            op = courseRepository.findById(courseId);
            if(op.isPresent()){
                c = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapInfoFromCourse(c));
    }

    @PostMapping("/courseEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer courseId = dataRequest.getInteger("courseId");
        Integer teacherId = dataRequest.getInteger("teacherId");
        double doubleValue = Double.parseDouble(dataRequest.getString("credit"));
        int intValue = (int)Math.round(doubleValue);
        Integer credit = Integer.valueOf(intValue);
        String name = dataRequest.getString("name");
        String num = dataRequest.getString("num");
        String address = dataRequest.getString("address");
        String refMat = dataRequest.getString("refMat");
        Course c = null;
        Teacher t = null;
        Optional<Course> op;
        if(courseId != null){
            op = courseRepository.findById(courseId);
            if(op.isPresent()){
                c = op.get();
            }
        }
        Optional<Teacher> tOp;
        if(teacherId != null){
            tOp = teacherRepository.findByTeacherId(teacherId);
            if(tOp.isPresent()){
                t = tOp.get();
            }
        }
        Optional<Course> cOp = courseRepository.findByNum(num);
        if(cOp.isPresent()){
            if(c == null || !c.getNum().equals(num)){
                return CommonMethod.getReturnMessageError("新课序号已经存在，不能添加或修改！");
            }
        }
        if(c == null){
            courseId = getNewCourseId();
            c = new Course();
            c.setCourseId(getNewCourseId());
            c.setNum(num);
            courseRepository.saveAndFlush(c);
        }else{
            courseId = c.getCourseId();
        }
        if(!num.equals((c.getNum()))){
            op = courseRepository.findByCourseId(courseId);
            if(op.isPresent()){
                c.setNum(num);
                courseRepository.saveAndFlush(c);
            }
        }
        c.setName(name);
        c.setCredit(credit);
        c.setAddress(address);
        c.setRefMat(refMat);
        c.setTeacher(t);
        Course preCourse = null;
        Optional<Course> aOp= courseRepository.findByNum(dataRequest.getString("preCourseNum"));
        if(aOp.isPresent()){
            preCourse = aOp.get();
        }
        c.setPreCourse(preCourse);
        courseRepository.saveAndFlush(c);
        return CommonMethod.getReturnData(c.getCourseId());
    }
}

