package com.teach.javafxclient.controller;

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


public class Honor1Controller {
    @FXML
    private TableView<Map> dataTableView;  //荣誉信息表
    @FXML
    private TableColumn<Map,String> honorNumColumn;   //荣誉信息表 编号列
    @FXML
    private TableColumn<Map,String> honorDayColumn;   //荣誉信息表 获得时间列
    @FXML
    private TableColumn<Map,String> honorNameColumn; //荣誉信息表 荣誉名称列
    @FXML
    private TableColumn<Map,String> honorTypeColumn; //荣誉信息表 荣誉等级列
    @FXML
    private TableColumn<Map,String> honorGradeColumn; //荣誉信息表 荣誉等级列

    private Integer honorId = null;
    private ArrayList<Map> honorList = new ArrayList();  // 荣誉信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < honorList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(honorList.get(j)));
        }
        dataTableView.setItems(observableList);
    }
    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req = new DataRequest();
        res = HttpRequestUtil.request("/api/student/getStudentIntroduceData", req); //从后台获取所有学生信息列表集合
        Map data =(Map)res.getData();
        Map info = (Map)data.get("info");
        String num=CommonMethod.getString(info,"num");
        req.put("num",num);
        res = HttpRequestUtil.request("/api/honor/getHonor1List", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            honorList = (ArrayList<Map>) res.getData();
        }
        honorNumColumn.setCellValueFactory(new MapValueFactory("honorNum"));  //设置列值工程属性
        honorDayColumn.setCellValueFactory(new MapValueFactory<>("honorDay"));
        honorTypeColumn.setCellValueFactory(new MapValueFactory<>("honorTypeName"));
        honorGradeColumn.setCellValueFactory(new MapValueFactory<>("honorGradeName"));
        honorNameColumn.setCellValueFactory(new MapValueFactory<>("honorName"));
        setTableViewData();
    }
}
