package com.teach.javafxclient.controller;

import com.teach.javafxclient.AppStore;
import com.teach.javafxclient.controller.base.MessageDialog;
import com.teach.javafxclient.controller.base.ToolController;
import com.teach.javafxclient.request.DataRequest;
import com.teach.javafxclient.request.DataResponse;
import com.teach.javafxclient.request.HttpRequestUtil;
import com.teach.javafxclient.request.OptionItem;
import com.teach.javafxclient.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityStudentController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, String> dateColumn;
    @FXML
    private TableColumn<Map, String> locationColumn;
    @FXML
    private TableColumn<Map, String> descriptionColumn;
    @FXML
    private TableColumn<Map, String> signUpColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField descriptionField;
    @FXML
    private ComboBox<OptionItem> signUpComboBox;

    @FXML
    private TextField numNameTextField;

    private Integer activityNum = null;

    private ArrayList<Map> activityList = new ArrayList<>();
    private List<OptionItem> signUpList;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    String studentNum = null;

    private void setTableViewData(){
        observableList.clear();
        for(int j = 0;j < activityList.size();j++){
            observableList.addAll(FXCollections.observableArrayList(activityList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    public void initialize(){
        DataResponse res;
        DataRequest req = new DataRequest();
        studentNum = AppStore.getJwt().getUsername();
        req.put("studentNum", studentNum);
        res = HttpRequestUtil.request("/api/activityStudent/getActivityStudentList",req);
        if(res !=null && res.getCode()==0){
            activityList = (ArrayList<Map>) res.getData();
        }
        nameColumn.setCellValueFactory(new MapValueFactory("name"));
        dateColumn.setCellValueFactory(new MapValueFactory("date"));
        locationColumn.setCellValueFactory(new MapValueFactory("location"));
        descriptionColumn.setCellValueFactory(new MapValueFactory<>("description"));
        signUpColumn.setCellValueFactory(new MapValueFactory<>("signUp"));
        signUpList = HttpRequestUtil.getDictionaryOptionItemList("BMM");
        signUpComboBox.getItems().addAll(signUpList);
        setTableViewData();
    }

    @FXML
    private void onQueryButtonClick() {
        String name = numNameTextField.getText();
        OptionItem registrationOption = signUpComboBox.getSelectionModel().getSelectedItem();
        String signUpData =  registrationOption.getValue();

        DataResponse res;
        DataRequest req = new DataRequest();

        // 添加请求参数
        req.add("name", name);
        req.add("signUpData", signUpData);

        // 发送HTTP请求
        res = HttpRequestUtil.request("/api/activityStudent/searchActivityStudentList", req);

        if (res != null && res.getCode() == 0) {
            activityList = (ArrayList<Map>) res.getData();
        }

        setTableViewData();
    }


    // 假设后端API的报名接口是/api/activityStudent/signUp，需要传递活动ID和学生编号
    @FXML
    protected void onSignUpButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能报名");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要报名吗");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer selectedActivityId = CommonMethod.getInteger(form, "activityId");

        if (selectedActivityId != null) {
            DataRequest req = new DataRequest();
            req.put("studentNum", studentNum);
            req.put("activityId", selectedActivityId);

            DataResponse res = HttpRequestUtil.request("/api/activityStudent/signUp", req);

            if (res != null && res.getCode() == 0) {
                // 报名成功，更新UI或给出提示
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("报名成功");
                alert.setHeaderText("你已成功报名参加该活动");
                alert.showAndWait();

                // 刷新活动列表或更新TableView
                onQueryButtonClick();

            }
        } else {
            // 没有选择活动，给出提示
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("未选择活动");
            alert.setHeaderText("请选择要报名的活动");
            alert.showAndWait();
        }
    }

    // 假设后端API的退出接口是/api/activityStudent/withdraw，同样需要传递活动ID和学生编号
    @FXML
    protected void onQuitButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能退出");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要退出吗");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer selectedActivityId = CommonMethod.getInteger(form, "activityId");

        if (selectedActivityId != null) {
            DataRequest req = new DataRequest();
            req.put("studentNum", studentNum);
            req.put("activityId", selectedActivityId);

            DataResponse res = HttpRequestUtil.request("/api/activityStudent/withdraw", req);

            if (res != null && res.getCode() == 0) {
                // 退出成功，更新UI或给出提示
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("退出成功");
                alert.setHeaderText("你已成功退出该活动");
                alert.showAndWait();

                // 刷新活动列表或更新TableView
                onQueryButtonClick();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("未选择活动");
            alert.setHeaderText("请选择要退出的活动");
            alert.showAndWait();
        }
    }
}
