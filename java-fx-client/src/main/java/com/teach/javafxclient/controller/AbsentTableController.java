package com.teach.javafxclient.controller;

import com.teach.javafxclient.MainApplication;
import com.teach.javafxclient.controller.base.MessageDialog;
import com.teach.javafxclient.request.DataRequest;
import com.teach.javafxclient.request.DataResponse;
import com.teach.javafxclient.request.HttpRequestUtil;
import com.teach.javafxclient.request.OptionItem;
import com.teach.javafxclient.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.teach.javafxclient.MainApplication.setCanClose;

public class AbsentTableController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> studentNumColumn;
    @FXML
    private TableColumn<Map,String> studentNameColumn;
    @FXML
    private TableColumn<Map,String> classNameColumn;
    @FXML
    private TableColumn<Map,String> courseNumColumn;
    @FXML
    private TableColumn<Map,String> courseNameColumn;
    @FXML
    private TableColumn<Map,String> timeColumn;
    @FXML
    private TableColumn<Map,String> editColumn;

    private ArrayList<Map> absentList = new ArrayList();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> courseComboBox;
    private List<OptionItem> courseList;
    private AbsentEditController absentEditController = null;
    private Stage stage = null;
    public List<OptionItem> getStudentList(){return studentList;}
    public List<OptionItem> getCourseList(){return courseList;}

    @FXML
    private void onQueryButtonClick(){
        String studentNum = null;
        String courseNum = null;
        OptionItem op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            studentNum = op.getValue();
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            courseNum = op.getValue();
        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("studentNum",studentNum);
        req.add("courseNum",courseNum);
        res = HttpRequestUtil.request("/api/absent/getAbsentListByNum",req);
        if(res != null && res.getCode()==0){
            absentList = (ArrayList<Map>) res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData(){
        observableList.clear();
        Map map;
        Button editButton;
        for(int j = 0;j < absentList.size(); j++ ){
            map = absentList.get(j);
            editButton = new Button("编辑");
            editButton.setId("edit" + j);
            editButton.setOnAction(e->{
                editItem(((Button)e.getSource()).getId());
            });
            map.put("edit",editButton);
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        dataTableView.setItems(observableList);
    }

    public void editItem(String name){
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(4,name.length()));
        Map data = absentList.get(j);
        initDialog();
        absentEditController.showDialog(data);
        setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    public void initialize() {


        studentNumColumn.setCellValueFactory(new MapValueFactory("studentNum"));  //设置列值工程属性
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));

        DataRequest req =new DataRequest();
        studentList = HttpRequestUtil.requestOptionItemList("/api/student/getStudentItemOptionList",req); //从后台获取所有学生信息列表集合
        courseList = HttpRequestUtil.requestOptionItemList("/api/course/getCourseItemOptionList",req); //从后台获取所有学生信息列表集合
        OptionItem item = new OptionItem(null,"0","请选择");
        studentComboBox.getItems().addAll(item);
        studentComboBox.getItems().addAll(studentList);
        courseComboBox.getItems().addAll(item);
        courseComboBox.getItems().addAll(courseList);
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }

    private void initDialog() {
        if(stage!= null)
            return;
        FXMLLoader fxmlLoader;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("absent-edit-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 260, 140);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("出勤管理对话框！");
            stage.setOnCloseRequest(event ->{
                setCanClose(true);
            });
            absentEditController = (AbsentEditController) fxmlLoader.getController();
            absentEditController.setAbsentTableController(this);
            absentEditController.init();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void doClose(String cmd,Map data){
        setCanClose(true);
        stage.close();
        if(!"ok".equals(cmd))
            return;
        DataResponse res;
        Integer studentId = CommonMethod.getInteger(data,"studentId");
        if(studentId == null) {
            MessageDialog.showDialog("没有选中学生不能添加保存！");
            return;
        }
        Integer courseId = CommonMethod.getInteger(data,"courseId");
        if(courseId == null) {
            MessageDialog.showDialog("没有选中课程不能添加保存！");
            return;
        }
        DataRequest req =new DataRequest();
        req.add("studentId",studentId);
        req.add("courseId",courseId);
        req.add("absentId",CommonMethod.getInteger(data,"absentId"));
        req.add("time",CommonMethod.getInteger(data,"time"));
        res = HttpRequestUtil.request("/api/absent/absentSave",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            onQueryButtonClick();
        }else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    private void onAddButtonClick() {
        initDialog();
        absentEditController.showDialog(null);
        setCanClose(false);
        stage.showAndWait();
    }
    @FXML
    private void onEditButtonClick() {
//        dataTableView.getSelectionModel().getSelectedItems();
        Map data = dataTableView.getSelectionModel().getSelectedItem();
        if(data == null) {
            MessageDialog.showDialog("没有选中，不能修改！");
            return;
        }
        initDialog();
        absentEditController.showDialog(data);
        setCanClose(false);
        stage.showAndWait();
    }
    @FXML
    private void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer absentId = CommonMethod.getInteger(form,"absentId");
        DataRequest req = new DataRequest();
        req.add("absentId", absentId);
        DataResponse res = HttpRequestUtil.request("/api/absent/absentDelete",req);
        if(res.getCode() == 0) {
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }





}
