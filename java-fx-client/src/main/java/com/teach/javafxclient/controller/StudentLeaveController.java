package com.teach.javafxclient.controller;

import com.teach.javafxclient.controller.base.MessageDialog;
import com.teach.javafxclient.request.DataRequest;
import com.teach.javafxclient.request.DataResponse;
import com.teach.javafxclient.request.HttpRequestUtil;
import com.teach.javafxclient.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.Map;

/**
 * StudentLeaveController 登录交互控制类 对应 adminLeave_panel.fxml  对应于管理员端管理学生请假的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class StudentLeaveController {
    @FXML
    private TableView<Map> dataTableView;  //学生请假信息表
    @FXML
    private TableColumn<Map,String> numColumn; // 学号列
    @FXML
    private TableColumn<Map,String> nameColumn; // 姓名列
    @FXML
    private TableColumn<Map,String> examineColumn; //审批列
    @FXML
    private TableColumn<Map,String> leaveReasonColumn; //请假理由列
    @FXML
    private TableColumn<Map,String> startDateColumn;//请假开始日期列
    @FXML
    private TableColumn<Map,String> endDateColumn;//请假结束日期列
    @FXML
    private Label num;//学号
    @FXML
    private Label name;//姓名
    @FXML
    private Label startDate;//开始日期
    @FXML
    private Label endDate;//结束日期
    @FXML
    private Label leaveReason;//请假原因
    @FXML
    private Label examine;  //请假类型输入域
    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer studentLeaveId = null;  //当前编辑修改的请假的主键
    private ArrayList<Map> studentLeaveList = new ArrayList();  //
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < studentLeaveList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(studentLeaveList.get(j)));
        }
        dataTableView.setItems(observableList);
    }
    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req = new DataRequest();
        req.put("num","");
        res = HttpRequestUtil.request("/api/studentLeave/getStudentLeave1List", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            studentLeaveList = (ArrayList<Map>) res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        leaveReasonColumn.setCellValueFactory(new MapValueFactory<>("leaveReason"));
        startDateColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new MapValueFactory<>("endDate"));
        examineColumn.setCellValueFactory(new  MapValueFactory<>("examine"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();

    }
    protected void changeStudentLeaveInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            return;
        }
        studentLeaveId = CommonMethod.getInteger(form, "studentLeaveId");
        DataRequest req = new DataRequest();
        req.put("studentLeaveId", studentLeaveId);
        DataResponse res = HttpRequestUtil.request("/api/studentLeave/getStudentLeaveInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        num.setText(CommonMethod.getString(form,"num"));
        name.setText(CommonMethod.getString(form,"name"));
        startDate.setText(CommonMethod.getString(form, "startDate"));
        endDate.setText(CommonMethod.getString(form, "endDate"));
        leaveReason.setText(CommonMethod.getString(form, "leaveReason"));
        examine.setText(CommonMethod.getString(form, "examine"));
    }

    /**
     * 点击请假列表的某一行，根据studentLeaveId ,从后台查询学生的请假信息息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeStudentLeaveInfo();
    }
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/studentLeave/getStudentLeaveList",req);
        if(res != null && res.getCode()== 0) {
            studentLeaveList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }
    //刷新
    @FXML
    protected void onFreshButtonClick(){initialize();}

    /**
     * 管理员端批准通过请假信息
     */
    @FXML
    protected void onApproveButtonClick(){
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        DataRequest req = new DataRequest();
        req.put("studentLeaveId",studentLeaveId);
        DataResponse res;
        req.put("form",form);
        res = HttpRequestUtil.request("/api/approveLeave/approveLeaveEditSave", req); //从后台获取所有学生信息列表集合
        int ret = MessageDialog.choiceDialog("确认要通过请假吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        if(res.getCode() == 0) {
            studentLeaveId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("通过成功！");
            initialize();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    /**
     * 管理员端退回请假信息
     */
    @FXML
    protected void  onRejectButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        DataRequest req = new DataRequest();
        req.put("studentLeaveId", studentLeaveId);
        DataResponse res;
        req.put("form", form);
        res = HttpRequestUtil.request("/api/rejectLeave/rejectLeaveEditSave", req); //从后台获取所有学生信息列表集合
        int ret = MessageDialog.choiceDialog("确认要退回请假吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        if (res.getCode() == 0) {
            studentLeaveId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("退回成功！");
            initialize();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
