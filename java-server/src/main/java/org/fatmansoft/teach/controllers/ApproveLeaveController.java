package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.ApproveLeave;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.StudentLeave;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.ApproveLeaveRepository;
import org.fatmansoft.teach.repository.StudentLeaveRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/approveLeave")
public class ApproveLeaveController {
    //通过的请假申请
    @Autowired
    private StudentLeaveRepository studentLeaveRepository;  //家庭数据操作自动注入
    @Autowired
    private ApproveLeaveRepository approveLeaveRepository;  //家庭数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    public synchronized Integer getNewApproveLeaveId(){
        Integer  id = approveLeaveRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    /**
     * getMapFromApproveLeave 将通过的请假属性数据转换复制MAp集合里
     * @param
     * @return
     */
    public Map getMapFromApproveLeave(ApproveLeave f) {
        Map m = new HashMap();
        StudentLeave e;
        if(f == null)
            return m;
        e = f.getStudentLeave();
        if(e == null)
            return m;
        m.put("studentLeaveId", e.getStudentLeaveId());
        m.put("approveLeaveId", f.getApproveLeaveId());
        m.put("num",e.getStudent().getPerson().getNum());
        m.put("name",e.getStudent().getPerson().getName());
        m.put("startDate",e.getLeave().getStartDate());
        m.put("endDate",e.getLeave().getEndDate());
        m.put("leaveReason",e.getLeave().getLeaveReason());
        m.put("examine",e.getLeave().getExamine());
        return m;
    }
    /**
     *  getApproveLeaveMapList 根据输入参数查询得到通过请假数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     * @param numName 输入参数
     * @return  Map List 集合
     */
    public List getApproveLeaveMapList(String numName) {
        List dataList = new ArrayList();
        List<ApproveLeave> eList = approveLeaveRepository.findApproveLeaveListByNumName(numName);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromApproveLeave(eList.get(i)));
        }
        return dataList;
    }
    /**
     *  getApproveLeave1MapList 根据输入参数查询得到通过请假数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     * @param num 输入参数
     * @return  Map List 集合
     */
    public List getApproveLeave1MapList(String num) {
        List dataList = new ArrayList();
        List<ApproveLeave> eList = approveLeaveRepository.findByStudentNum(num);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromApproveLeave(eList.get(i)));
        }
        return dataList;
    }
    /**
     *  getApproveLeaveList 通过的请假管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储通过请假信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */

    @PostMapping("/getApproveLeaveItemOptionList")
    public OptionItemList getApproveLeaveItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<ApproveLeave> sList = approveLeaveRepository.findApproveLeaveListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (ApproveLeave e : sList) {
            itemList.add(new OptionItem(e.getApproveLeaveId(), e.getStudentLeave().getStudent().getPerson().getNum(), e.getStudentLeave().getStudent().getPerson().getNum()+"-"+e.getStudentLeave().getStudent().getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getApproveLeaveList")
    public DataResponse getApproveLeaveList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getApproveLeaveMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //学生端
    @PostMapping("/getApproveLeave1List")
    public DataResponse getApproveLeave1List(@Valid @RequestBody DataRequest dataRequest) {
        String num= dataRequest.getString("num");
        List dataList = getApproveLeave1MapList(num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    /**
     * ApproveLeaveEditSave 前端通过的请假信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new  ApproveLeave 计算新的id， 复制相关属性，保存
     * 审批状态自动为已审批
     * @return  新建修改学生的主键 approveLeave_id 返回前端
     */
    @PostMapping("/approveLeaveEditSave")
    public DataResponse ApproveLeaveEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer approveLeaveId = dataRequest.getInteger("approveLeaveId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        ApproveLeave f = null;
        StudentLeave sl;
        Optional<ApproveLeave> op;
        Optional<Student> nOp = studentRepository.findByPersonNum(num); //查询是否存在num的人员
        sl = studentLeaveRepository.findStudentLeaveByLeaveReason(CommonMethod.getString(form,"leaveReason")).get();
        if (approveLeaveId != null) {
            op = approveLeaveRepository.findById(approveLeaveId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                f = op.get();//获得family
            }
        }
        if(sl.getLeave().getExamine().equals("已审批")){
            return CommonMethod.getReturnMessageError("该请假信息已经审批！");
        }
        f = new ApproveLeave();   // 创建实体对象
        f.setApproveLeaveId(getNewApproveLeaveId());//获取新的ApproveLeave主键
        f.setStudentLeave(sl);
        sl.getLeave().setExamine("已审批");
        sl.getLeave().setLeaveReason((String) form.get("leaveReason"));
        sl.getLeave().setStartDate((String) form.get("startDate"));
        sl.getLeave().setEndDate((String) form.get("endDate"));
        sl.getStudent().getPerson().setNum((String) form.get("num"));
        approveLeaveRepository.save(f);  // 修改通过的请假信息
        return CommonMethod.getReturnData(f.getApproveLeaveId());  // 将approveLeaveId返回前端

    }

}


