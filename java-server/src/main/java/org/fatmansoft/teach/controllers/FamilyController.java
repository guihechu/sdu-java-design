package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.FamilyRepository;
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
@RequestMapping("/api/family")
public class FamilyController {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FamilyRepository familyRepository;

    public synchronized Integer getNewFamilyId(){  //synchronized 同步方法
        Integer  id = familyRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };

    public Map getMapFromFamily(Family f) {
        Map m = new HashMap();
        if(f == null)
            return m;
        m.put("familyId", f.getFamilyId());
        m.put("studentId", f.getStudent().getStudentId());
        m.put("idCard",f.getIdCard());
        m.put("num",f.getStudent().getPerson().getNum());
        m.put("name",f.getName());
        m.put("relation",f.getRelation());
        m.put("birthday", f.getBirthday());  //时间格式转换字符串
        m.put("email",f.getEmail());
        m.put("phone",f.getPhone());
        m.put("address",f.getAddress());
        return m;
    }

    public List  getFamilyMapList(String name) {
        List dataList = new ArrayList();
        List<Family> sList = familyRepository.findByName(name);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromFamily(sList.get(i)));
        }
        return dataList;
    }

    @PostMapping("/getFamilyItemOptionList")
    public OptionItemList getFamilyItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Family> sList = familyRepository.findByName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList();
        for (Family s : sList) {
            itemList.add(new OptionItem(s.getFamilyId(), s.getStudent().getPerson().getNum(), s.getStudent().getPerson().getNum()+"-"+s.getName()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getFamilyList")
    public DataResponse getFamilyList(@Valid @RequestBody DataRequest dataRequest){
        String name = dataRequest.getString("name");
        List dataList = getFamilyMapList(name);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/familyDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse familyDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer familyId = dataRequest.getInteger("familyId");  //获取student_id值
        Family f= null;
        Optional<Family> op;
        if(familyId != null) {
            op= familyRepository.findById(familyId);   //查询获得实体对象
            if(op.isPresent()) {
                f = op.get();
            }
        }
        if(f != null) {
            familyRepository.delete(f);    //首先数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getFamilyInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getFamilyInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer familyId = dataRequest.getInteger("familyId");
        Family s= null;
        Optional<Family> op;
        if(familyId != null) {
            op= familyRepository.findById(familyId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromFamily(s)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/familyEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse familyEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer familyId = dataRequest.getInteger("familyId");
        Map form = dataRequest.getMap("form");
        String idCard = CommonMethod.getString(form,"idCard");
        String num = CommonMethod.getString(form,"num");
        Student s =null;
        Family f = null;
        Optional<Family> op;
        if(familyId != null){
            op = familyRepository.findById(familyId);
            if(op.isPresent()){
                f = op.get();
            }
        }
        Optional<Student> sOp = studentRepository.findByPersonNum(num);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法找到学生，请检查学号是否正确！");
        }else{
            s = sOp.get();
        }
        Optional<Family>fOp = familyRepository.findByIdCard(idCard);
        if(fOp.isPresent()){
            String studentNum = fOp.get().getStudent().getPerson().getNum();
            if(studentNum.equals(num)){
                return CommonMethod.getReturnMessageError("家庭成员已存在，不能添加!");
            }
        }
        if(f == null){
            familyId = getNewFamilyId();
            f = new Family();
            f.setFamilyId(familyId);
            familyRepository.saveAndFlush(f);
        }else{
            familyId = f.getFamilyId();
        }
        f.setStudent(s);
        f.setIdCard(idCard);
        f.setRelation(CommonMethod.getString(form,"relation"));
        f.setPhone(CommonMethod.getString(form,"phone"));
        f.setEmail(CommonMethod.getString(form,"email"));
        f.setAddress(CommonMethod.getString(form,"address"));
        f.setBirthday(CommonMethod.getString(form,"birthday"));
        f.setName(CommonMethod.getString(form,"name"));
        familyRepository.saveAndFlush(f);
        return CommonMethod.getReturnData(familyId);
    }


}
