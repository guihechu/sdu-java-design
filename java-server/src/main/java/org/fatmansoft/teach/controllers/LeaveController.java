package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Leave;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.LeaveRepository;
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
@RequestMapping("/api/leave")
public class LeaveController {
    //学生准备申请的请假
    @Autowired
    private LeaveRepository leaveRepository;  //家庭数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    public synchronized Integer getNewLeaveId(){
        Integer  id = leaveRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    /**
     * getMapFromLeave 将请假表属性数据转换复制MAp集合里
     * @param
     * @return
     */
    public Map getMapFromLeave(Leave e) {
        Map m = new HashMap();
        Student s;
        if(e == null)
            return m;
        m.put("startDate",e.getStartDate());
        m.put("endDate",e.getEndDate());
        m.put("leaveReason",e.getLeaveReason());
        //请假类型类型的值转换成数据类型名
        m.put("examine",e.getExamine());
        s = e.getStudent();
        if(s == null)
            return m;
        m.put("leaveId", e.getLeaveId());
        m.put("studentId", s.getStudentId());
        m.put("num",s.getPerson().getNum());
        return m;
    }
    /**
     *  getLeaveMapList 根据输入参数查询得到请假数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     * @param numName 输入参数
     * @return  Map List 集合
     */
    public List getLeaveMapList(String numName) {
        List dataList = new ArrayList();
        List<Leave> eList = leaveRepository.findLeaveListByNumName(numName);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromLeave(eList.get(i)));
        }
        return dataList;
    }
    public List getLeave1MapList(String num) {
        List dataList = new ArrayList();
        List<Leave> eList = leaveRepository.findByStudentStudentNum(num);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromLeave(eList.get(i)));
        }
        return dataList;
    }
    /**
     *  getLeaveList 学生管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */

    @PostMapping("/getLeaveItemOptionList")
    public OptionItemList getLeaveItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Leave> sList = leaveRepository.findLeaveListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Leave e : sList) {
            itemList.add(new OptionItem(e.getLeaveId(), e.getStudent().getPerson().getNum(), e.getStudent().getPerson().getNum()+"-"+e.getStudent().getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getLeaveList")
    public DataResponse getEducationList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getLeaveMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //学生端
    /**
     *  getLeave1List 学生管理 点击查询按钮请求
     *  前台请求参数 num 学号 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */
    @PostMapping("/getLeave1List")
    public DataResponse getLeave1List(@Valid @RequestBody DataRequest dataRequest) {
        String num= dataRequest.getString("num");
        List dataList = getLeave1MapList(num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    /**
     * getLeaveInfo 前端点击请假列表时前端获取请假详细信息请求服务
     * @param dataRequest 从前端获取 leaveId 查询请假信息的主键 leave_id
     * @return  根据leaveId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getLeaveInfo")
    public DataResponse getLeaveInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Leave e= null;
        Optional<Leave> op;
        if(leaveId != null) {
            op= leaveRepository.findById(leaveId); //根据主键从数据库查询信息
            if(op.isPresent()) {
                e = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromLeave(e)); //这里回传包含请假信息的Map对象
    }
    /**
     * LeaveEditSave 前端请假信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Leave 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * leaveId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * 审批状态自动设为未审批
     * @return  新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/leaveEditSave")
    public DataResponse leaveEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        String name = CommonMethod.getString(form, "name");
        Calendar c1 = Calendar.getInstance();
        Leave f = null;
        Student s ;
        Optional<Leave> op;
        Integer studentId;
        Optional<Student> nOp = studentRepository.findByPersonNum(num); //查询是否存在num的人员
        List<Student> sList = studentRepository.findByPersonName(name);
        if (leaveId != null) {
            op = leaveRepository.findById(leaveId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                f = op.get();//获得family
            }
        }
        if (f == null) {
            s = nOp.get();
            studentId = s.getStudentId();
            f = new Leave();   // 创建实体对象
            f.setLeaveId(getNewLeaveId());//获取新的leave主键
            f.setStudent(studentRepository.findById(studentId).get());

        }
        //判断填写日期是否正确
        if (CommonMethod.getDate(form,"endDate").before(CommonMethod.getDate(form,"startDate"))){
            return CommonMethod.getReturnMessageError("开始日期应早于结束日期！");
        }
        if(CommonMethod.getDate(form,"startDate").before(c1.getTime())
                ||CommonMethod.getDate(form,"endDate").before(c1.getTime())){
            return CommonMethod.getReturnMessageError("日期不能早于今天！");
        }
        f.setLeaveReason((String) form.get("leaveReason"));
        f.setStartDate((String) form.get("startDate"));
        f.setEndDate((String) form.get("endDate"));
        f.setExamine((String) form.get("examine"));
        leaveRepository.save(f);  // 修改保存请假信息
        return CommonMethod.getReturnData(f.getLeaveId());  // 将leaveId返回前端

    }
    /**
     * leaveDelete 删除学生信息Web服务 Leave页面的列表里点击删除按钮则可以删除已经存在的请假信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，删除leave同时,与之关联的studentLeave,approveLeave,rejectLeave均会删除信息
     * @param dataRequest  前端leaveId 删除的学生的主键 leave_id
     * @return  正常操作
     */
    @PostMapping("/leaveDelete")
    public DataResponse leaveDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");  //获取student_id值
        Leave s = null;
        Optional<Leave> op;
        if (leaveId != null) {
            op = leaveRepository.findById(leaveId);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            leaveRepository.delete(s);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

}
