package com.teach.javafxclient.controller;


import com.teach.javafxclient.AppStore;
import com.teach.javafxclient.request.DataRequest;
import com.teach.javafxclient.request.DataResponse;
import com.teach.javafxclient.request.HttpRequestUtil;
import com.teach.javafxclient.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.Map;

public class StudentCourseController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,Integer> markColumn;
    @FXML
    private TableColumn<Map,Integer> rankingColumn;
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
    private TextField numTextField;

    private Integer courseId = null;

    private ArrayList<Map> courseList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    String studentNum = null;

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
        studentNum = AppStore.getJwt().getUsername();
        req.put("studentNum", studentNum);
        res = HttpRequestUtil.request("/api/studentCourse/getCourseList",req);
        if(res !=null && res.getCode()==0){
            courseList = (ArrayList<Map>) res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));
        nameColumn.setCellValueFactory(new MapValueFactory("name"));
        creditColumn.setCellValueFactory(new MapValueFactory("credit"));
        preCourseColumn.setCellValueFactory(new MapValueFactory("preCourse"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        rankingColumn.setCellValueFactory(new MapValueFactory<>("ranking"));
        teacherColumn.setCellValueFactory(new MapValueFactory("teacher"));
        addressColumn.setCellValueFactory(new MapValueFactory("address"));
        refMatColumn.setCellValueFactory(new MapValueFactory("refMat"));
        setTableViewData();
    }

    @FXML
    protected void onQueryButtonClick(){
        String num = numTextField.getText();
        DataRequest req = new DataRequest();
        DataResponse res;
        if(num == ""){
            req.put("studentNum",studentNum);
            res = HttpRequestUtil.request("/api/studentCourse/getCourseList",req);
        }else{
            req.put("num",num);
            res = HttpRequestUtil.request("/api/studentCourse/getCourse",req);
        }
        if(res != null && res.getCode()==0){
            courseList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }


}
