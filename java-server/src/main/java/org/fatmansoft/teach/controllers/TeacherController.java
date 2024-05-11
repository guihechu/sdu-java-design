package org.fatmansoft.teach.controllers;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.*;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")

public class TeacherController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， StudentController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的赋值，
    // StudentController中的方法可以直接使用
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private TeacherRepository teacherRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private ScoreRepository scoreRepository;  //成绩数据操作自动注入
    @Autowired
    private FeeRepository feeRepository;  //消费数据操作自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入


    /**
     * 获取 person 表的新的Id StringBoot 对SqLite 主键自增支持不好  插入记录是需要设置主键ID，编写方法获取新的 person_id
     *
     * @return
     */
    public synchronized Integer getNewPersonId() {  //synchronized 同步方法
        Integer id = personRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    ;

    /**
     * 获取 user 表的新的Id StringBoot 对SqLite 主键自增支持不好  插入记录是需要设置主键ID，编写方法获取新的 user_id
     *
     * @return
     */
    public synchronized Integer getNewUserId() {
        Integer id = userRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    ;

    /**
     * 获取 student 表的新的Id StringBoot 对SqLite 主键自增支持不好  插入记录是需要设置主键ID，编写方法获取新的 student_id
     *
     * @return
     */
    public synchronized Integer getNewTeacherId() {
        Integer id = teacherRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }


    /**
     * getMapFromStudent 将学生表属性数据转换复制MAp集合里
     *
     * @param
     * @return
     */
    public Map getMapFromTeacher(Teacher s) {
        Map m = new HashMap();
        Person p;
        if (s == null)
            return m;
        m.put("title", s.getTitle());
        m.put("degree", s.getDegree());
        p = s.getPerson();
        if (p == null)
            return m;
        m.put("teacherId", s.getTeacherId());
        m.put("personId", p.getPersonId());
        m.put("num", p.getNum());
        m.put("name", p.getName());
        m.put("dept", p.getDept());
        m.put("card", p.getCard());
        String gender = p.getGender();
        m.put("gender", gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email", p.getEmail());
        m.put("phone", p.getPhone());
        m.put("address", p.getAddress());
        m.put("introduce", p.getIntroduce());
        return m;
    }

    /**
     * getStudentMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     *
     * @param numName 输入参数
     * @return Map List 集合
     */
    public List getTeacherMapList(String numName) {
        List dataList = new ArrayList();
        List<Teacher> sList = teacherRepository.findTeacherListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTeacher(sList.get(i)));
        }
        return dataList;
    }

    /**
     * getStudentList 学生管理 点击查询按钮请求
     * 前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     *
     * @return
     */

    @PostMapping("/getTeacherItemOptionList")
    public OptionItemList getTeacherItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Teacher> sList = teacherRepository.findTeacherListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Teacher s : sList) {
            itemList.add(new OptionItem(s.getTeacherId(), s.getPerson().getNum(), s.getPerson().getNum() + "-" + s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    /**
     * studentDelete 删除学生信息Web服务 Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，应为user关联person,Student关联Person 所以要先删除Student,User，再删除Person
     *
     * @param dataRequest 前端studentId 药删除的学生的主键 student_id
     * @return 正常操作
     */

    @PostMapping("/teacherDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");  //获取student_id值
        Teacher s = null;
        Optional<Teacher> op;
        if (teacherId != null) {
            op = teacherRepository.findById(teacherId);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            Optional<User> uOp = userRepository.findByPersonPersonId(s.getPerson().getPersonId()); //查询对应该学生的账户
            if (uOp.isPresent()) {
                userRepository.delete(uOp.get()); //删除对应该学生的账户
            }
            Person p = s.getPerson();
            teacherRepository.delete(s);    //首先数据库永久删除学生信息
            personRepository.delete(p);   // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     *
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return 根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Teacher s = null;
        Optional<Teacher> op;
        if (teacherId != null) {
            op = teacherRepository.findById(teacherId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromTeacher(s)); //这里回传包含学生信息的Map对象
    }
    @PostMapping("/teacherEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer teacherId = dataRequest.getInteger("teacherId");
        Map form = dataRequest.getMap("form");
        String num = CommonMethod.getString(form,"num");
        Teacher t=null;
        Person p;
        User u;
        Optional<Teacher> op;
        Integer personId;
        if(teacherId!=null){
            op= teacherRepository.findById(teacherId);
            if(op.isPresent()){
                t = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num);
        if(nOp.isPresent()){
            if(t == null || !t.getPerson().getNum().equals(num)){
                return CommonMethod.getReturnMessageError("新学号已经存在，不能添加或修改！");
            }
        }
        if(t == null){
            personId = getNewPersonId();
            p = new Person();
            p.setPersonId(personId);
            p.setNum(num);
            p.setType("2");
            personRepository.saveAndFlush(p);
            String password = encoder.encode("123456");
            u= new User();
            u.setUserId(getNewUserId());
            u.setPerson(p);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_TEACHER));
            userRepository.saveAndFlush(u);
            t = new Teacher();
            t.setTeacherId(getNewTeacherId());
            t.setPerson(p);
            teacherRepository.saveAndFlush(t);
        }else{
            p = t.getPerson();
            personId = p.getPersonId();
        }if(!num.equals(p.getNum())){
            Optional<User>uOp = userRepository.findByPersonPersonId(personId);
            if(uOp.isPresent()){
                u = uOp.get();
                u.setUserName(num);
                userRepository.saveAndFlush(u);
            }
            p.setNum(num);
        }
        p.setName(CommonMethod.getString(form,"name"));
        p.setDept(CommonMethod.getString(form,"dept"));
        p.setCard(CommonMethod.getString(form,"card"));
        p.setGender(CommonMethod.getString(form,"gender"));
        p.setBirthday(CommonMethod.getString(form,"birthday"));
        p.setEmail(CommonMethod.getString(form,"email"));
        p.setPhone(CommonMethod.getString(form,"phone"));
        p.setAddress(CommonMethod.getString(form,"address"));
        personRepository.save(p);  // 修改保存人员信息
        t.setTitle(CommonMethod.getString(form,"title"));
        t.setDegree(CommonMethod.getString(form,"degree"));
        teacherRepository.save(t);  //修改保存学生信息
        return CommonMethod.getReturnData(t.getTeacherId());  // 将studentId返回前端
    }
}
