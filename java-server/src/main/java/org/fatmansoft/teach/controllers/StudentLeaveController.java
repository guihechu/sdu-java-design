package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Leave;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.StudentLeave;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.LeaveRepository;
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
@RequestMapping("/api/studentLeave")
public class StudentLeaveController {
    //学生提交的请假申请
    @Autowired
    private StudentLeaveRepository studentLeaveRepository;  //家庭数据操作自动注入
    @Autowired
    private LeaveRepository leaveRepository;  //家庭数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    /**
     *  获取 studentLeave 表的新的Id StringBoot 对SqLite 主键自增支持不好  插入记录是需要设置主键ID，编写方法获取新的 studentLeave_id
     * @return
     */
    public synchronized Integer getNewStudentLeaveId(){
        Integer  id = studentLeaveRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    /**
     * getMapFromStudent 将学生表属性数据转换复制MAp集合里
     * @param
     * @return
     */
    public Map getMapFromStudentLeave(StudentLeave f) {
        Map m = new HashMap();
        Student s;
        Leave e;
        if(f == null)
            return m;
        s = f.getStudent();
        e = f.getLeave();
        if(s == null)
            return m;
        m.put("leaveId", e.getLeaveId());
        m.put("studentId", s.getStudentId());
        m.put("studentLeaveId", f.getStudentLeaveId());
        m.put("num",s.getPerson().getNum());
        m.put("name",s.getPerson().getName());
        m.put("dept",s.getPerson().getDept());
        m.put("className",s.getClassName());
        m.put("phone",s.getPerson().getPhone());
        m.put("startDate",e.getStartDate());
        m.put("endDate",e.getEndDate());
        m.put("leaveReason",e.getLeaveReason());
        m.put("examine",e.getExamine());
        return m;
    }
    /**
     *  getStudentLeaveMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     * @param numName 输入参数
     * @return  Map List 集合
     */
    public List getStudentLeaveMapList(String numName) {
        List dataList = new ArrayList();
        List<StudentLeave> eList = studentLeaveRepository.findStudentLeaveListByNumName(numName);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromStudentLeave(eList.get(i)));
        }
        return dataList;
    }
    /**
     *  getStudentLeave1MapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号
     * @param num 输入参数
     * @return  Map List 集合
     */
    public List getStudentLeave1MapList(String num) {
        List dataList = new ArrayList();
        List<StudentLeave> eList = studentLeaveRepository.findByStudentNum(num);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromStudentLeave(eList.get(i)));
        }
        return dataList;
    }
    /**
     *  getStudentLeaveList 学生管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */

    @PostMapping("/getStudentLeaveItemOptionList")
    public OptionItemList getStudentLeaveItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<StudentLeave> sList = studentLeaveRepository.findStudentLeaveListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (StudentLeave e : sList) {
            itemList.add(new OptionItem(e.getStudentLeaveId(), e.getStudent().getPerson().getNum(), e.getStudent().getPerson().getNum()+"-"+e.getStudent().getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getStudentLeaveList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentLeaveList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getStudentLeaveMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    /**
     *  getStudentLeave1List 学生管理 点击查询按钮请求
     *  前台请求参数 num 学号 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */
    @PostMapping("/getStudentLeave1List")
    public DataResponse getStudentLeave1List(@Valid @RequestBody DataRequest dataRequest) {
        String num= dataRequest.getString("num");
        List dataList = getStudentLeave1MapList(num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    /**
     * getStudentLeaveInfo 前端点击学生列表时前端获取学生详细信息请求服务
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 studentLeave_id
     * @return  根据studentLeaveId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getStudentLeaveInfo")
    public DataResponse getStudentLeaveInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentLeaveId = dataRequest.getInteger("studentLeaveId");
        StudentLeave e= null;
        Optional<StudentLeave> op;
        if(studentLeaveId != null) {
            op= studentLeaveRepository.findById(studentLeaveId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                e = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromStudentLeave(e)); //这里回传包含学生信息的Map对象
    }
    /**
     * StudentLeaveEditSave 前端学生请假申请提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空， new StudentLeave 计算新的id， 复制相关属性，保存,通过前端提交给管理员端,使管理员端看到请假信息
     * studentLeaveId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/studentLeaveEditSave")
    public DataResponse StudentLeaveEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentLeaveId = dataRequest.getInteger("studentLeaveId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Calendar c1 = Calendar.getInstance();
        StudentLeave f = null;
        Optional<StudentLeave> op;
        if (studentLeaveId != null) {
            op = studentLeaveRepository.findById(studentLeaveId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                f = op.get();//获得family
            }
        }
        f = new StudentLeave();   // 创建实体对象
        f.setStudentLeaveId(getNewStudentLeaveId());//获取新的family主键
        Leave l;
        Student s;
        l = leaveRepository.findByLeaveReason(CommonMethod.getString(form,"leaveReason")).get();
        s = studentRepository.findByPersonNum(CommonMethod.getString(form, "num")).get();
        f.setStudent(s);
        f.setLeave(l);
        if (CommonMethod.getDate(form,"endDate").before(CommonMethod.getDate(form,"startDate"))){
            return CommonMethod.getReturnMessageError("开始日期应早于结束日期！");
        }
        if(CommonMethod.getDate(form,"startDate").before(c1.getTime())
                ||CommonMethod.getDate(form,"endDate").before(c1.getTime())){
            return CommonMethod.getReturnMessageError("日期不能早于今天！");
        }
        l.setLeaveReason((String) form.get("leaveReason"));
        l.setStartDate((String) form.get("startDate"));
        l.setEndDate((String) form.get("endDate"));
        l.setExamine(CommonMethod.getString(form,"examine"));
        f.getStudent().getPerson().setNum((String) form.get("num"));
        studentLeaveRepository.save(f);  // 修改保存请假信息
        return CommonMethod.getReturnData(f.getStudentLeaveId());  // 将studentLeaveId返回前端

    }

}
