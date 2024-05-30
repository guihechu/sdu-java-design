package com.teach.javafxclient.controller;

import com.teach.javafxclient.controller.base.ToolController;
import com.teach.javafxclient.request.*;
import com.teach.javafxclient.util.CommonMethod;
import com.teach.javafxclient.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * StudentController 登录交互控制类 对应 student_panel.fxml  对应于学生管理的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class CourseController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,Integer> creditColumn;
    @FXML
    private TableColumn<Map,String> preCourseColumn;
    @FXML
    private TableColumn<Map,String> teacherColumn;
    @FXML
    private TableColumn<Map,String> addressColumn;
    @FXML
    private TableColumn<Map,String> refMatColumn;


    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField creditField;
    @FXML
    private TextField preCourseNumField;
    @FXML
    private ComboBox<OptionItem> teacherComboBox;
    private List<OptionItem> teacherList;
    @FXML
    private TextField addressField;
    @FXML
    private TextField refMatField;

    @FXML
    private TextField numNameTextField;
    public List<OptionItem> getTeacherList(){return teacherList;}

    private Integer courseId = null;
    private ArrayList<Map> courseList = new ArrayList();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();


    private void setTableViewData(){
        observableList.clear();
        for(int j = 0;j < courseList.size();j++){
            observableList.addAll(FXCollections.observableArrayList(courseList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    public void initialize(){
        DataResponse res;
        DataRequest req = new DataRequest();
        req.put("numName","");
        res = HttpRequestUtil.request("/api/course/getCourseList",req);
        if(res != null && res.getCode()==0){
            courseList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));
        nameColumn.setCellValueFactory(new MapValueFactory("name"));
        creditColumn.setCellValueFactory(new MapValueFactory("credit"));
        preCourseColumn.setCellValueFactory(new MapValueFactory("preCourse"));
        teacherColumn.setCellValueFactory(new MapValueFactory("teacher"));
        addressColumn.setCellValueFactory(new MapValueFactory("address"));
        refMatColumn.setCellValueFactory(new MapValueFactory("refMat"));
        DataRequest re = new DataRequest();
        teacherList = HttpRequestUtil.requestOptionItemList("/api/teacher/getTeacherItemOptionList",re);
        OptionItem item = new OptionItem(null,"0","请选择");
        teacherComboBox.getItems().addAll(item);
        teacherComboBox.getItems().addAll(teacherList);
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
    }

    public void clearPanel(){
        courseId = null;
        numField.setText("");
        nameField.setText("");
        creditField.setText("");
        preCourseNumField.setText("");
        teacherComboBox.getEditor().setText("");
        addressField.setText("");
        refMatField.setText("");
    }

    protected void changeCourseInfo(){
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null){
            clearPanel();
            return;
        }
        courseId = CommonMethod.getInteger(form,"courseId");
        DataRequest req = new DataRequest();
        req.put("courseId",courseId);
        DataResponse res =HttpRequestUtil.request("/api/course/getCourseInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        creditField.setText(CommonMethod.getString(form, "credit"));
        preCourseNumField.setText(CommonMethod.getString(form, "preCourse"));
        teacherComboBox.getEditor().setText(CommonMethod.getString(form,"teacher"));
        addressField.setText(CommonMethod.getString(form,"address"));
        refMatField.setText(CommonMethod.getString(form,"refMat"));
    }
    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeCourseInfo();
    }

    @FXML
    protected void onQueryButtonClick(){
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res =HttpRequestUtil.request("/api/course/getCourseList",req);
        if(res != null && res.getCode()==0){
            courseList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }

    @FXML
    protected void onAddButtonClick(){
        clearPanel();
    }

    @FXML
    protected void onDeleteButtonClick(){
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null){
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        courseId = CommonMethod.getInteger(form,"courseId");
        DataRequest req = new DataRequest();
        req.put("courseId",courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/courseDelete",req);
        if(res.getCode() == 0){
            MessageDialog.showDialog("删除成功");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    protected void onSaveButtonClick(){
        if(numField.getText().equals("")){
            MessageDialog.showDialog("课序号为空，不能修改");
            return;
        }
        DataRequest req = new DataRequest();
        req.put("courseId",courseId);
        req.put("num",numField.getText());
        req.put("name",nameField.getText());
        req.put("credit",creditField.getText());
        req.put("preCourseNum",preCourseNumField.getText());
        OptionItem op;
        op = teacherComboBox.getSelectionModel().getSelectedItem();
        if(op != null){
            req.put("teacherId",op.getId());
        }
        req.put("address",addressField.getText());
        req.put("refMat",refMatField.getText());

        DataResponse res = HttpRequestUtil.request("/api/course/courseEditSave",req);
        if(res.getCode()== 0){
            courseId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功");
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
        onQueryButtonClick();
    }

    public void doNew(){clearPanel();}
    public void doSave(){onSaveButtonClick();}
    public void doDelete(){onDeleteButtonClick();}

}
