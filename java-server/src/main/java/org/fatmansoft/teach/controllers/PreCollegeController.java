package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.MessageResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.PreCollegeRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/preCollege")
public class PreCollegeController {
    @Autowired
    private PreCollegeRepository preCollegeRepository;
    @Autowired
    private StudentRepository studentRepository;

    public synchronized Integer getNewInfoId(){
        Integer  id = preCollegeRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    }

    public Map getMapFromPreCollege(PreCollege p){
        Map m = new HashMap();
        if(p == null){
            return m;
        }
        m.put("infoId",p.getInfoId());
        String political = p.getPolitical();
        m.put("political",political);
        m.put("politicalName", ComDataUtil.getInstance().getDictionaryLabelByValue("MMM", political));
        m.put("highSchool",p.getHighSchool());
        m.put("graduation",p.getGraduation());
        m.put("health",p.getHealth());
        m.put("hometown",p.getHometown());
        m.put("nation",p.getNation());
        m.put("change",p.getChange());
        Student s = p.getStudent();
        if(s == null){
            return m;
        }
        m.put("name",s.getPerson().getName());
        m.put("num",s.getPerson().getNum());
        return m;
    }

    public List getPreCollegeMapList(String numName){
        List dataList = new ArrayList<>();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        List<PreCollege> pList =new ArrayList();
        for(int i = 0;i < sList.size();i++){
            Integer studentId = sList.get(i).getStudentId();
            PreCollege p;
            Optional<PreCollege> pOp = preCollegeRepository.findByStudentStudentId(studentId);
            if(pOp.isPresent()){
                p = pOp.get();
                dataList.add(getMapFromPreCollege(p));
            }
        }
        return dataList;
    }

    @PostMapping("/getPreCollegeItemOptionList")
    public OptionItemList getPreCollegeItemOptionList(@Valid @RequestBody DataRequest dataRequest){
        List<PreCollege> pList = preCollegeRepository.findAll();
        List<OptionItem> itemList = new ArrayList<>();
        for(PreCollege p : pList){
            itemList.add(new OptionItem(p.getInfoId(),p.getStudent().getPerson().getNum(),p.getStudent().getPerson().getNum()+"-"+p.getStudent().getPerson().getName()));
        }
        return new OptionItemList(0,itemList);
    }

    @PostMapping("/getPreCollegeList")
    public DataResponse getPreCollegeList(@Valid @RequestBody DataRequest dataRequest){
        String numName = dataRequest.getString("numName");
        List dataList = getPreCollegeMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/preCollegeDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse preCollegeDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer infoId = dataRequest.getInteger("infoId");  //获取student_id值
        PreCollege p =null;
        Optional<PreCollege> op;
        if(infoId != null) {
            op= preCollegeRepository.findById(infoId);   //查询获得实体对象
            if(op.isPresent()) {
                p = op.get();
            }
        }
        if(p != null) {
            preCollegeRepository.delete(p);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getPreCollegeInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getPreCollegeInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer infoId = dataRequest.getInteger("infoId");
        PreCollege p = null;
        Optional<PreCollege> op;
        if(infoId != null) {
            op= preCollegeRepository.findById(infoId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                p = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromPreCollege(p)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/preCollegeEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse preCollegeEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer infoId = dataRequest.getInteger("infoId");
        Map form = dataRequest.getMap("form");
        String num = CommonMethod.getString(form,"num");
        Student s = null;
        PreCollege p =null;
        Optional<Student> sOp = studentRepository.findByPersonNum(num);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("学生不存在，请检查学号是否正确!");
        }else{
            s = sOp.get();
        }
        if(infoId != null){
            Optional<PreCollege> pOp = preCollegeRepository.findById(infoId);
            if(pOp.isPresent()){
                p = pOp.get();
            }
        }
        if(p == null){
            Optional<PreCollege> pOp = preCollegeRepository.findByStudentStudentId(s.getStudentId());
            if(pOp.isPresent()){
                return CommonMethod.getReturnMessageError("学生学前信息已存在，不能添加,请在原信息上修改！");
            }
            p =new PreCollege();
            infoId = getNewInfoId();
            p.setInfoId(infoId);
            preCollegeRepository.saveAndFlush(p);
        }
        p.setStudent(s);
        p.setPolitical(CommonMethod.getString(form,"political"));
        p.setHighSchool(CommonMethod.getString(form,"highSchool"));
        p.setGraduation(CommonMethod.getString(form,"graduation"));
        p.setHealth(CommonMethod.getString(form,"health"));
        p.setHometown(CommonMethod.getString(form,"hometown"));
        p.setNation(CommonMethod.getString(form,"nation"));
        p.setChange(CommonMethod.getString(form,"change"));
        preCollegeRepository.saveAndFlush(p);
        return CommonMethod.getReturnData(p.getInfoId());
    }



}
