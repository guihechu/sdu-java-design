package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.MessageResponse;
import org.fatmansoft.teach.repository.FeeRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api/fee")
public class FeeController {
    @Autowired
    private FeeRepository feeRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private BaseService baseService;
    public synchronized Integer getNewFeeId(){
        Integer  id = feeRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    }

    public Map getMapFromFee(Fee f) {
        Map m = new HashMap();
        Student s= null;
        if(f == null)
            return m;
        m.put("day",f.getDay());
        m.put("money",f.getMoney());
        Optional<Student> op =studentRepository.findById(f.getStudentId());
        if(op.isPresent()){
            s = op.get();
        }
        if(s == null)
            return m;
        m.put("num", s.getPerson().getNum());
        m.put("feeId",f.getFeeId());
        return m;
    }

    public List getFeeMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Fee> fList;
        if(studentId != null){
            fList = feeRepository.findListByStudent(studentId);
        }else{
            fList = feeRepository.findAll();
        }
        if(fList == null || fList.size() == 0)
            return dataList;
        for(int i = 0; i < fList.size();i++) {
            dataList.add(getMapFromFee(fList.get(i)));
        }
        return dataList;
    }

    @PostMapping("/getFeeList")
    public DataResponse getFeeList(@Valid @RequestBody DataRequest dataRequest) {
        String num= dataRequest.getString("num");
        Integer studentId = null;
        Optional<Student> Op = studentRepository.findByPersonNum(num);
        if(Op.isPresent()){
            studentId = Op.get().getStudentId();
            List dataList = getFeeMapList(studentId);
        }
        List dataList = getFeeMapList(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/feeDelete")
    public DataResponse feeDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");  //获取student_id值
        Fee f= null;
        Optional<Fee> op;
        if(feeId != null) {
            op= feeRepository.findById(feeId);   //查询获得实体对象
            if(op.isPresent()) {
                f = op.get();
            }
        }
        if(f != null) {
            feeRepository.delete(f);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getFeeInfo")
    public DataResponse getFeeInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Fee f= null;
        Optional<Fee> op;
        if(feeId != null) {
            op= feeRepository.findById(feeId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                f = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromFee(f)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/feeEditSave")
    public DataResponse feeEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Integer studentId = null;
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");
        Double money = CommonMethod.getDouble(form,"money");
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Optional<Student> sop = studentRepository.findByPersonNum(num);
        if(sop.isPresent()){
            studentId = sop.get().getStudentId();
        }else {
            return new DataResponse(400,null,"无法找到对应的学生信息，请检查学号是否正确。");
        }
        Fee f= null;
        Optional<Fee> op;
        Integer personId;
        if(feeId != null) {
            op= feeRepository.findById(feeId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                f = op.get();
            }
        }
        if(f == null) {
            feeId = getNewFeeId();
            f = new Fee();
            f.setFeeId(feeId);
            feeRepository.saveAndFlush(f);
        }
        f.setDay(day);
        f.setMoney(money);
        f.setStudentId(studentId);
        feeRepository.save(f);  //修改保存学生信息
        return CommonMethod.getReturnData(f.getFeeId());
    }

}
