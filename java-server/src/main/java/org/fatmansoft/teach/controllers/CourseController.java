package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.CourseRepository;
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
    @PreAuthorize("hasRole('ADMIN')")
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
        return CommonMethod.getReturnData(getMapFromCourse(c));
    }

    @PostMapping("/courseEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer courseId = dataRequest.getInteger("courseId");
        Map form = dataRequest.getMap("form");
        String num = CommonMethod.getString(form,"num");
        Course c = null;
        Optional<Course> op;
        if(courseId != null){
            op = courseRepository.findById(courseId);
            if(op.isPresent()){
                c = op.get();
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
        c.setName(CommonMethod.getString(form,"name"));
        c.setCredit(CommonMethod.getInteger(form,"credit"));
        Course preCourse = null;
        Optional<Course> aOp= courseRepository.findByCourseId(CommonMethod.getInteger(form,"preCourse"));
        if(aOp.isPresent()){
            preCourse = aOp.get();
        }
        c.setPreCourse(preCourse);
        courseRepository.saveAndFlush(c);
        return CommonMethod.getReturnData(c.getCourseId());
    }
}

