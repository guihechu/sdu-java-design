package com.teach.javafxclient.controller;

import com.teach.javafxclient.AppStore;
import com.teach.javafxclient.request.DataRequest;
import com.teach.javafxclient.request.DataResponse;
import com.teach.javafxclient.request.HttpRequestUtil;
import com.teach.javafxclient.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.Map;

/**
 * RejectLeaveController 登录交互控制类 对应 studentLeave_reject_panel.fxml  对应于退回的学生请假管理的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class RejectLeaveController {
    @FXML
    private TableView<Map> dataTableView;  //学生端退回请假信息表
    @FXML
    private TableColumn<Map,String> leaveReasonColumn; //学生信息表 理由列
    @FXML
    private TableColumn<Map,String> startDateColumn;
    @FXML
    private TableColumn<Map,String> endDateColumn;

    private Integer rejectLeaveId = null;  //当前编辑修改的请假的主键
    private ArrayList<Map> rejectLeaveList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < rejectLeaveList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(rejectLeaveList.get(j)));
        }
        dataTableView.setItems(observableList);
    }
    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req = new DataRequest();
        //res = HttpRequestUtil.request("/api/student/getStudentIntroduceData", req); //从后台获取所有学生信息列表集合
        //Map data =(Map)res.getData();
        //Map info = (Map)data.get("info");
        //String num= CommonMethod.getString(info,"num");
        String num = AppStore.getJwt().getUsername();
        req.put("num",num);
        res = HttpRequestUtil.request("/api/rejectLeave/getRejectLeave1List", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            rejectLeaveList = (ArrayList<Map>) res.getData();
        }
        leaveReasonColumn.setCellValueFactory(new MapValueFactory<>("leaveReason"));
        startDateColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new MapValueFactory<>("endDate"));
        setTableViewData();
    }
    @FXML
    protected void onFreshButtonClick(){initialize();}

}
