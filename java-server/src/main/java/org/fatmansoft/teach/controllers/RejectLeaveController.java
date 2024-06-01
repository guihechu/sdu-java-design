package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.RejectLeave;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.StudentLeave;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.RejectLeaveRepository;
import org.fatmansoft.teach.repository.StudentLeaveRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/rejectLeave")
public class RejectLeaveController {
    //被退回的请假申请
    @Autowired
    private StudentLeaveRepository studentLeaveRepository;  //家庭数据操作自动注入
    @Autowired
    private RejectLeaveRepository rejectLeaveRepository;  //家庭数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    public synchronized Integer getNewRejectLeaveId(){
        Integer  id = rejectLeaveRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    /**
     * getMapFromRejectLeave 将退回的请假表属性数据转换复制MAp集合里
     * @param
     * @return
     */
    public Map getMapFromRejectLeave(RejectLeave f) {
        Map m = new HashMap();
        StudentLeave e;
        if(f == null)
            return m;
        e = f.getStudentLeave();
        if(e == null)
            return m;
        m.put("studentLeaveId", e.getStudentLeaveId());
        m.put("rejectLeaveId", f.getRejectLeaveId());
        m.put("num",e.getStudent().getPerson().getNum());
        m.put("name",e.getStudent().getPerson().getName());
        m.put("startDate",e.getLeave().getStartDate());
        m.put("endDate",e.getLeave().getEndDate());
        m.put("leaveReason",e.getLeave().getLeaveReason());
        m.put("examine",e.getLeave().getExamine());
        return m;
    }
    /**
     *  getRejectLeaveMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     * @param numName 输入参数
     * @return  Map List 集合
     */
    public List getRejectLeaveMapList(String numName) {
        List dataList = new ArrayList();
        List<RejectLeave> eList = rejectLeaveRepository.findRejectLeaveListByNumName(numName);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromRejectLeave(eList.get(i)));
        }
        return dataList;
    }
    public List getRejectLeave1MapList(String num) {
        List dataList = new ArrayList();
        List<RejectLeave> eList = rejectLeaveRepository.findByStudentNum(num);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromRejectLeave(eList.get(i)));
        }
        return dataList;
    }
    /**
     *  getRejectLeaveList 退回的请假管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */

    @PostMapping("/getRejectLeaveItemOptionList")
    public OptionItemList getRejectLeaveItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<RejectLeave> sList = rejectLeaveRepository.findRejectLeaveListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (RejectLeave e : sList) {
            itemList.add(new OptionItem(e.getRejectLeaveId(), e.getStudentLeave().getStudent().getPerson().getNum(), e.getStudentLeave().getStudent().getPerson().getNum()+"-"+e.getStudentLeave().getStudent().getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getRejectLeaveList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getRejectLeaveList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getRejectLeaveMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    /**
     *  getRejectLeave1List 退回的请假管理 点击查询按钮请求
     *  前台请求参数 num 学号 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */
    @PostMapping("/getRejectLeave1List")
    public DataResponse getRejectLeave1List(@Valid @RequestBody DataRequest dataRequest) {
        String num= dataRequest.getString("num");
        List dataList = getRejectLeave1MapList(num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    /**
     * RejectLeaveEditSave 前端通过的请假信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new  reject_Leave 计算新的id， 复制相关属性，保存
     * 审批状态自动为已审批
     * @return  新建修改学生的主键 rejectLeave_id 返回前端
     */
    @PostMapping("/rejectLeaveEditSave")
    public DataResponse RejectLeaveEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer rejectLeaveId = dataRequest.getInteger("rejectLeaveId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        Calendar c1 = Calendar.getInstance();
        RejectLeave f = null;
        StudentLeave sl;
        Optional<RejectLeave> op;
        Optional<Student> nOp = studentRepository.findByPersonNum(num); //查询是否存在num的人员
        sl = studentLeaveRepository.findStudentLeaveByLeaveReason(CommonMethod.getString(form,"leaveReason")).get();
        if (rejectLeaveId != null) {
            op = rejectLeaveRepository.findById(rejectLeaveId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                f = op.get();//获得family
            }
        }
        if(sl.getLeave().getExamine().equals("已审批")){
            return CommonMethod.getReturnMessageError("该请假信息已经审批！");
        }
        f = new RejectLeave();   // 创建实体对象
        f.setRejectLeaveId(getNewRejectLeaveId());//获取新的退回的请假主键
        f.setStudentLeave(sl);
        sl.getLeave().setExamine("已审批");
        sl.getLeave().setLeaveReason((String) form.get("leaveReason"));
        sl.getLeave().setStartDate((String) form.get("startDate"));
        sl.getLeave().setEndDate((String) form.get("endDate"));
        sl.getStudent().getPerson().setNum((String) form.get("num"));
        rejectLeaveRepository.save(f);  // 修改保存退回的请假信息
        return CommonMethod.getReturnData(f.getRejectLeaveId());  // 将rejectLeaveId返回前端
    }

}


