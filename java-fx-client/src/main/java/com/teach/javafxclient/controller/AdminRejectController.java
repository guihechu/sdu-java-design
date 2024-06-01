package com.teach.javafxclient.controller;

import com.teach.javafxclient.request.DataRequest;
import com.teach.javafxclient.request.DataResponse;
import com.teach.javafxclient.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.Map;

/**
 * 管理员端退回的请假管理,对应的adminReject_panel.fxml
 */
public class AdminRejectController {
    @FXML
    private TableView<Map> dataTableView;  //管理员退回学生请假信息表
    @FXML
    private TableColumn<Map,String> numColumn; //同studentLeave
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> leaveReasonColumn;
    @FXML
    private TableColumn<Map,String> startDateColumn;
    @FXML
    private TableColumn<Map,String> endDateColumn;
    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer adminRejectId = null;  //当前编辑修改的请假的主键
    private ArrayList<Map> adminRejectList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < adminRejectList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(adminRejectList.get(j)));
        }
        dataTableView.setItems(observableList);
    }
    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req = new DataRequest();
        req.put("num","");
        res = HttpRequestUtil.request("/api/rejectLeave/getRejectLeave1List", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            adminRejectList = (ArrayList<Map>) res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        leaveReasonColumn.setCellValueFactory(new MapValueFactory<>("leaveReason"));
        startDateColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new MapValueFactory<>("endDate"));
        setTableViewData();
    }
    @FXML
    protected void onFreshButtonClick(){initialize();}
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/rejectLeave/getRejectLeaveList",req);
        if(res != null && res.getCode()== 0) {
            adminRejectList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }

}
