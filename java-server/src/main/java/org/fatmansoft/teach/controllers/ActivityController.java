package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.ActivityRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
@CrossOrigin(origins="*",maxAge = 3600)
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private PersonRepository personRepository;
    public synchronized Integer getNewActivityId(){
        Integer id = activityRepository.getMaxId();
        if(id==null)
            id = 1;
        else
            id = id+1;
        return id;
    }
    public Map getMapFromActivity(Activity c){
        Map m = new HashMap();
        if(c==null)
            return m;
        m.put("activityId",c.getActivityId());
        m.put("num",c.getNum());
        m.put("name",c.getName());
        m.put("date",c.getDate());
        m.put("location",c.getLocation());
        m.put("description",c.getDescription());
        return m;
    }

    /*
   lol
     */
    public List getActivityMapList(String numName){
        List dataList = new ArrayList();
        List<Activity> cList = activityRepository.findActivityListByNumName(numName);
        if(cList == null || cList.size() == 0)
            return dataList;
        for(int i = 0; i < cList.size();i++){
            dataList.add(getMapFromActivity(cList.get(i)));
        }
        return dataList;
    }

    @PostMapping("/getActivityItemOptionList")
    public OptionItemList getActivityItemOptionList(@Valid @RequestBody DataRequest dataRequest){
        List<Activity> cList = activityRepository.findActivityListByNumName("");
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for(Activity c : cList){
            itemList.add(new OptionItem(c.getActivityId(),c.getNum(),c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0,itemList);
    }

    @PostMapping("/getActivityList")
    public DataResponse getActivityList(@Valid @RequestBody DataRequest dataRequest){
        String numName = dataRequest.getString("numName");
        List dataList = getActivityMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/activityDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse activityDelete(@Valid @RequestBody DataRequest dataRequest){
        Integer activityId = dataRequest.getInteger("activityId");
        Activity c = null;
        Optional<Activity> op;
        if(activityId != null){
            op = activityRepository.findById(activityId);
            if(op.isPresent()){
                c = op.get();
                if(c != null){
                    activityRepository.delete(c);
                }
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/getActivityInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getActivityInfo(@Valid @RequestBody DataRequest dataRequest){
        Integer activityId = dataRequest.getInteger("activityId");
        Activity c = null;
        Optional<Activity> op;
        if(activityId != null){
            op = activityRepository.findByActivityId(activityId);
            if(op.isPresent()){
                c = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromActivity(c));
    }


    /*@PostMapping("/activityEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse activityEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        Activity a= null;
        Optional<Activity> op;
        if(activityId != null) {
            op= activityRepository.findById(activityId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }

        a.setName(CommonMethod.getString(form,"name"));
        a.setNum(CommonMethod.getString(form,"num"));
        a.setDate(CommonMethod.getString(form,"date"));
        a.setLocation(CommonMethod.getString(form,"location"));
        a.setDescription(CommonMethod.getString(form,"description"));
        activityRepository.save(a);  //修改保存学生信息
        return CommonMethod.getReturnData(a.getActivityId());  // 将studentId返回前端
    }*/
    @PostMapping("/activityEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse activityEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer activityId = dataRequest.getInteger("activityId");
        String name = dataRequest.getString("name");
        String num = dataRequest.getString("num");
        String date = dataRequest.getString("date");
        String location = dataRequest.getString("location");
        String description = dataRequest.getString("description");
        Activity c = null;
        Optional<Activity> op;
        if(activityId != null){
            op = activityRepository.findById(activityId);
            if(op.isPresent()){
                c = op.get();
            }
        }
        Optional<Activity> cOp = activityRepository.findByNum(num);
        if(cOp.isPresent()){
            if(c == null || !c.getNum().equals(num)){
                return CommonMethod.getReturnMessageError("新活动编号已经存在，不能添加或修改！");
            }
        }
        if(c == null){
            activityId = getNewActivityId();
            c = new Activity();
            c.setActivityId(getNewActivityId());
            c.setNum(num);
            activityRepository.saveAndFlush(c);
        }else{
            activityId = c.getActivityId();
        }
        if(!num.equals((c.getNum()))){
            op = activityRepository.findByActivityId(activityId);
            if(op.isPresent()){
                c.setNum(num);
                activityRepository.saveAndFlush(c);
            }
        }
        c.setName(name);
        c.setDate(date);
        c.setLocation(location);
        c.setDescription(description);

        activityRepository.saveAndFlush(c);
        return CommonMethod.getReturnData(c.getActivityId());
    }

}