package com.teach.javafxclient.controller;

import com.teach.javafxclient.controller.base.ToolController;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import com.teach.javafxclient.controller.base.LocalDateStringConverter;
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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> studentNameColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> descriptionColumn;
    @FXML
    private TableColumn<Map,String> typeColumn;
    @FXML
    private TableColumn<Map,String> startDateColumn;
    @FXML
    private TableColumn<Map,String> endDateColumn;
    @FXML
    private TableColumn<Map,String> statusColumn;

    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private ComboBox<OptionItem> typeComboBox;
    @FXML
    private DatePicker startDatePick;
    @FXML
    private DatePicker endDatePick;
    @FXML
    private TextField statusField;

    @FXML
    private TextField nameTextField;

    private Integer projectId = null;
    private ArrayList<Map> projectList = new ArrayList<>();
    private List<OptionItem> typeList;
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < projectList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(projectList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req =new DataRequest();
        req.put("name","");
        res = HttpRequestUtil.request("/api/project/getProjectList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            projectList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new MapValueFactory<>("description"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("typeName"));
        startDateColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new MapValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        typeList = HttpRequestUtil.getDictionaryOptionItemList("SJM");

        typeComboBox.getItems().addAll(typeList);
        startDatePick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
        endDatePick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    public void clearPanel(){
        projectId = null;
        numField.setText("");
        nameField.setText("");
        descriptionField.setText("");
        typeComboBox.getSelectionModel().select(-1);
        startDatePick.getEditor().setText("");
        endDatePick.getEditor().setText("");
        statusField.setText("");
    }

    protected void changeProjectInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        projectId = CommonMethod.getInteger(form,"projectId");
        DataRequest req = new DataRequest();
        req.put("projectId",projectId);
        DataResponse res = HttpRequestUtil.request("/api/project/getProjectInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        descriptionField.setText(CommonMethod.getString(form, "description"));
        typeComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(typeList, CommonMethod.getString(form, "type")));
        startDatePick.getEditor().setText(CommonMethod.getString(form, "startDate"));
        endDatePick.getEditor().setText(CommonMethod.getString(form, "endDate"));
        statusField.setText(CommonMethod.getString(form, "status"));
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeProjectInfo();
    }

    @FXML
    protected void onQueryButtonClick() {
        String name = nameField.getText();
        DataRequest req = new DataRequest();
        req.put("name",name);
        DataResponse res = HttpRequestUtil.request("/api/project/getProjectList",req);
        if(res != null && res.getCode()== 0) {
            projectList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }

    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        projectId = CommonMethod.getInteger(form,"projectId");
        DataRequest req = new DataRequest();
        req.put("projectId", projectId);
        DataResponse res = HttpRequestUtil.request("/api/project/projectDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        if( numField.getText().equals("")) {
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num",numField.getText());
        form.put("name",nameField.getText());
        form.put("description",descriptionField.getText());
        if(typeComboBox.getSelectionModel() != null && typeComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("type",typeComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("startDate",startDatePick.getEditor().getText());
        form.put("endDate",endDatePick.getEditor().getText());
        form.put("status",statusField.getText());
        DataRequest req = new DataRequest();
        req.put("projectId", projectId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/project/projectEditSave",req);
        if(res.getCode() == 0) {
            projectId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
        onQueryButtonClick();
    }


}
