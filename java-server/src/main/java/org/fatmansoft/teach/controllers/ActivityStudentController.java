package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.ActivityRepository;
import org.fatmansoft.teach.repository.ActivityStudentRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins="*",maxAge = 3600)
@RestController
@RequestMapping("/api/activityStudent")
public class ActivityStudentController {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ActivityStudentRepository activityStudentRepository;

    public synchronized Integer getNewActivityStudentId() {
        Integer id = activityStudentRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    ;


    @PostMapping("/getActivityStudentList")
    public DataResponse getActivityStudentList(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if (studentId == null)
            studentId = 0;
        Integer activityId = dataRequest.getInteger("activityId");
        if (activityId == null)
            activityId = 0;
        List<ActivityStudent> sList = activityStudentRepository.findByStudentActivity(studentId, activityId);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        for (ActivityStudent s : sList) {
            m = new HashMap();
            m.put("activityStudentId", s.getActivityStudentId() + "");
            m.put("studentId", s.getStudent().getStudentId() + "");
            m.put("activityId", s.getActivity().getActivityId() + "");
            m.put("studentNum", s.getStudent().getPerson().getNum());
            m.put("studentName", s.getStudent().getPerson().getName());
            m.put("num", s.getActivity().getNum());
            m.put("name", s.getActivity().getName());
            m.put("date", s.getActivity().getDate());
            m.put("location", s.getActivity().getLocation());
            m.put("description", s.getActivity().getDescription());
            m.put("signUp", s.getSignUp());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }



    @PostMapping("/searchActivityStudentList")
    public DataResponse searchActivityStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String name = dataRequest.getString("name");
        String signUpData = dataRequest.getString("signUpData");
        String signUp = ComDataUtil.getInstance().getDictionaryLabelByValue("BMM", signUpData);

        List<ActivityStudent> sList = activityStudentRepository.findByNameAndSignUp(name, signUp);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        for (ActivityStudent s : sList) {
            m = new HashMap();
            m.put("activityStudentId", s.getActivityStudentId() + "");
            m.put("studentId", s.getStudent().getStudentId() + "");
            m.put("activityId", s.getActivity().getActivityId() + "");
            m.put("num", s.getActivity().getNum());
            m.put("name", s.getActivity().getName());
            m.put("date", s.getActivity().getDate());
            m.put("location", s.getActivity().getLocation());
            m.put("description", s.getActivity().getDescription());
            m.put("signUp", s.getSignUp());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }


    @PostMapping("/signUp")
    public DataResponse signUp(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentNum = dataRequest.getInteger("studentNum");
        if (studentNum == null)
            studentNum = 0;
        Integer activityId = dataRequest.getInteger("activityId");
        if (activityId == null)
            activityId = 0;
        List<ActivityStudent> sList = activityStudentRepository.findByStudentActivity(studentNum, activityId);
        ActivityStudent a = sList.get(0); // 或者根据需要选择其他对象
        a.setSignUp("是"); // 或者根据需要设置为其他值

        activityStudentRepository.save(a);  //
        return CommonMethod.getReturnData(a.getActivityStudentId());
    }


    @PostMapping("/withdraw")
    public DataResponse withdraw(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentNum = dataRequest.getInteger("studentNum");
        if (studentNum == null)
            studentNum = 0;
        Integer activityId = dataRequest.getInteger("activityId");
        if (activityId == null)
            activityId = 0;
        List<ActivityStudent> sList = activityStudentRepository.findByStudentActivity(studentNum, activityId);
        ActivityStudent a = sList.get(0); // 或者根据需要选择其他对象
        a.setSignUp("否"); // 或者根据需要设置为其他值

        activityStudentRepository.save(a);  //
        return CommonMethod.getReturnData(a.getActivityStudentId());
    }
}


