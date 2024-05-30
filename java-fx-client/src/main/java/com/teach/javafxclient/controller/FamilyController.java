package com.teach.javafxclient.controller;

import com.teach.javafxclient.controller.base.LocalDateStringConverter;
import com.teach.javafxclient.controller.base.MessageDialog;
import com.teach.javafxclient.controller.base.ToolController;
import com.teach.javafxclient.request.DataRequest;
import com.teach.javafxclient.request.DataResponse;
import com.teach.javafxclient.request.HttpRequestUtil;
import com.teach.javafxclient.request.OptionItem;
import com.teach.javafxclient.util.CommonMethod;
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

public class FamilyController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> idCardColumn;
    @FXML
    private TableColumn<Map,String> relationColumn;
    @FXML
    private TableColumn<Map,String> birthdayColumn;
    @FXML
    private TableColumn<Map,Integer> emailColumn;
    @FXML
    private TableColumn<Map,Integer> phoneColumn;
    @FXML
    private TableColumn<Map,Integer> addressColumn;


    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField idCardField;
    @FXML
    private TextField relationField;
    @FXML
    private DatePicker birthdayPick;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;

    @FXML
    private TextField nameTextField;

    private  Integer familyId = null;
    private ArrayList<Map> familyList = new ArrayList();
    private ObservableList<Map> observableList= FXCollections.observableArrayList();

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < familyList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(familyList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req =new DataRequest();
        req.put("name","");
        res = HttpRequestUtil.request("/api/family/getFamilyList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            familyList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        idCardColumn.setCellValueFactory(new MapValueFactory("idCard"));
        relationColumn.setCellValueFactory(new MapValueFactory<>("relation"));
        birthdayColumn.setCellValueFactory(new MapValueFactory<>("birthday"));
        emailColumn.setCellValueFactory(new MapValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new MapValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new MapValueFactory<>("address"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        birthdayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    protected void changeFamilyInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        familyId = CommonMethod.getInteger(form,"familyId");
        DataRequest req = new DataRequest();
        req.put("familyId",familyId);
        DataResponse res = HttpRequestUtil.request("/api/family/getFamilyInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        idCardField.setText(CommonMethod.getString(form,"idCard"));
        relationField.setText(CommonMethod.getString(form, "relation"));
        birthdayPick.getEditor().setText(CommonMethod.getString(form, "birthday"));
        emailField.setText(CommonMethod.getString(form, "email"));
        phoneField.setText(CommonMethod.getString(form, "phone"));
        addressField.setText(CommonMethod.getString(form, "address"));
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeFamilyInfo();
    }


    public void clearPanel(){
        familyId = null;
        numField.setText("");
        nameField.setText("");
        idCardField.setText("");
        relationField.setText("");
        birthdayPick.getEditor().setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
    }

    @FXML
    protected void onQueryButtonClick() {
        String name = nameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("name",name);
        DataResponse res = HttpRequestUtil.request("/api/family/getFamilyList",req);
        if(res != null && res.getCode()== 0) {
            familyList = (ArrayList<Map>)res.getData();
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
        familyId = CommonMethod.getInteger(form,"familyId");
        DataRequest req = new DataRequest();
        req.put("familyId", familyId);
        DataResponse res = HttpRequestUtil.request("/api/family/familyDelete",req);
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
            MessageDialog.showDialog("学生学号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num",numField.getText());
        form.put("name",nameField.getText());
        form.put("idCard",idCardField.getText());
        form.put("relation",relationField.getText());
        form.put("birthday",birthdayPick.getEditor().getText());
        form.put("email",emailField.getText());
        form.put("phone",phoneField.getText());
        form.put("address",addressField.getText());
        DataRequest req = new DataRequest();
        req.put("familyId", familyId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/family/familyEditSave",req);
        if(res.getCode() == 0) {
            familyId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
        onQueryButtonClick();
    }

    public void doNew(){
        clearPanel();
    }
    public void doSave(){
        onSaveButtonClick();
    }
    public void doDelete(){
        onDeleteButtonClick();
    }


}
