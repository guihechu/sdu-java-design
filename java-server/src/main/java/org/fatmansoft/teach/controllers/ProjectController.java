package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Project;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.ProjectRepository;
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
@RequestMapping("/api/project")
public class ProjectController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public synchronized Integer getNewProjectId(){  //synchronized 同步方法
        Integer  id = projectRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    }

    public Map getMapFromProject(Project p) {
        Map m = new HashMap();
        Student s = null;
        if(p == null)
            return m;
        m.put("projectId",p.getProjectId());
        m.put("name",p.getName());
        m.put("description",p.getDescription());
        String type = p.getType();
        m.put("type",type);
        m.put("typeName",ComDataUtil.getInstance().getDictionaryLabelByValue("SJM", type));
        m.put("startDate",p.getStartDate());
        m.put("endDate",p.getEndDate());
        m.put("status",p.getStatus());
        s = p.getStudent();
        if(s == null)
            return m;
        m.put("studentId", s.getStudentId());
        m.put("num", s.getPerson().getNum());
        m.put("studentName",s.getPerson().getName());
        return m;
    }

    public List getProjectMapList(String name) {
        List dataList = new ArrayList();
        List<Project> pList = projectRepository.findByName(name);  //数据库查询操作
        if(pList == null || pList.size() == 0)
            return dataList;
        for(int i = 0; i < pList.size();i++) {
            dataList.add(getMapFromProject(pList.get(i)));
        }
        return dataList;
    }

    @PostMapping("/getProjectItemOptionList")
    public OptionItemList getProjectItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Project> pList = projectRepository.findByName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList();
        for (Project p : pList) {
            itemList.add(new OptionItem(p.getProjectId(), p.getStudent().getPerson().getNum(), p.getStudent().getPerson().getNum()+"-"+p.getStudent().getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getProjectList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getProjectList(@Valid @RequestBody DataRequest dataRequest) {
        String name= dataRequest.getString("name");
        List dataList = getProjectMapList(name);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/projectDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse projectDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer projectId = dataRequest.getInteger("projectId");  //获取student_id值
        Project p = null;
        Optional<Project> op;
        if(projectId != null) {
            op= projectRepository.findById(projectId);   //查询获得实体对象
            if(op.isPresent()) {
                p = op.get();
            }
        }
        if(p != null) {
            projectRepository.delete(p);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getProjectInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getProjectInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer projectId = dataRequest.getInteger("projectId");
        Project p= null;
        Optional<Project> op;
        if(projectId != null) {
            op= projectRepository.findById(projectId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                p = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromProject(p)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/projectEditSave")
    public DataResponse projectEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer projectId = dataRequest.getInteger("projectId");
        Map from = dataRequest.getMap("form");
        String num =CommonMethod.getString(from,"num");
        Project p =null;
        Student s = null;
        if(projectId != null) {
            Optional<Project> pOp = projectRepository.findById(projectId);
            if(pOp.isPresent()){
                p = pOp.get();
            }
        }
        Optional<Student> sOP = studentRepository.findByPersonNum(num);
        if(sOP.isPresent()){
            s = sOP.get();
        }else{
            return CommonMethod.getReturnMessageError("学生不存在，请检查学号是否正确！");
        }
        if(p == null){
            projectId = getNewProjectId();
            p = new Project();
            p.setProjectId(projectId);
            projectRepository.saveAndFlush(p);
        }else {
            projectId = p.getProjectId();
        }
        p.setName(CommonMethod.getString(from,"name"));
        p.setDescription(CommonMethod.getString(from,"description"));
        p.setType(CommonMethod.getString(from,"type"));
        p.setStartDate(CommonMethod.getString(from,"startDate"));
        p.setEndDate(CommonMethod.getString(from,"endDate"));
        p.setStatus(CommonMethod.getString(from,"status"));
        p.setStudent(s);
        projectRepository.saveAndFlush(p);
        return CommonMethod.getReturnData(projectId);
    }


}
