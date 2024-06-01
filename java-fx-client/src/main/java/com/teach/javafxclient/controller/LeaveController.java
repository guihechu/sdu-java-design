package com.teach.javafxclient.controller;

import com.teach.javafxclient.AppStore;
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


public class LeaveController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  //请假信息表
    @FXML
    private TableColumn<Map,String> leaveReasonColumn; //请假理由列
    @FXML
    private TableColumn<Map,String> startDateColumn;//请假开始时间
    @FXML
    private TableColumn<Map,String> endDateColumn;//请假结束时间
    @FXML
    private TableColumn<Map,String> examineColumn;//审批
    @FXML
    private DatePicker startDatePick;//请假开始时间输入域
    @FXML
    private DatePicker endDatePick;//请假结束时间输入域
    @FXML
    private TextField leaveReasonField;//请假原因输入域
    @FXML
    private Label examine;//审批
    @FXML
    private ComboBox<OptionItem> leaveTypeComboBox;  //请假类型输入域
    /**
     *确定请假类型
     */
    private Integer leaveId = null;  //当前编辑修改的请假的主键
    private List<OptionItem> leaveTypeList;   //性请假类型择列表数据
    private ArrayList<Map> leaveList = new ArrayList();  // 信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表
    /**
     *
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < leaveList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(leaveList.get(j)));
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
        //String num=CommonMethod.getString(info,"num");
        String num = AppStore.getJwt().getUsername();
        req.put("num",num);
        res = HttpRequestUtil.request("/api/leave/getLeave1List", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            leaveList = (ArrayList<Map>) res.getData();
        }
        leaveReasonColumn.setCellValueFactory(new MapValueFactory<>("leaveReason"));
        startDateColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new MapValueFactory<>("endDate"));
        examineColumn.setCellValueFactory(new MapValueFactory<>("examine"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        startDatePick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));;
        endDatePick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));;
    }

    protected void changeActivityInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            return;
        }
        leaveId = CommonMethod.getInteger(form, "leaveId");
        DataRequest req = new DataRequest();
        req.put("leaveId", leaveId);
        DataResponse res = HttpRequestUtil.request("/api/leave/getLeaveInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        leaveReasonField.setText(CommonMethod.getString(form,"leaveReason"));
        endDatePick.getEditor().setText(CommonMethod.getString(form, "endDate"));
        startDatePick.getEditor().setText(CommonMethod.getString(form, "startDate"));
        examine.setText(CommonMethod.getString(form, "examine"));
    }

    /**
     * 点击学生列表的某一行，根据leaveId ,从后台查询学生的基本信息，切换学生的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeActivityInfo();
    }


    @FXML
    protected void onApplyButtonClick(){
        Map form = new HashMap();
        DataRequest req = new DataRequest();
        req.put("leaveId",leaveId);
        DataResponse res;
        //res = HttpRequestUtil.request("/api/student/getStudentIntroduceData", req); //从后台获取所有学生信息列表集合
        //Map data =(Map)res.getData();
        //Map info = (Map)data.get("info");
        String num = AppStore.getJwt().getUsername();
        //String num=CommonMethod.getString(info,"num");
        String examine ="未审批";
        form.put("leaveReason",leaveReasonField.getText());
        form.put("startDate",startDatePick.getEditor().getText());
        form.put("endDate",endDatePick.getEditor().getText());
        form.put("examine",examine);
        req.put("form",form);
        form.put("num",num);
        form = dataTableView.getSelectionModel().getSelectedItem();
        res = HttpRequestUtil.request("/api/leave/leaveEditSave",req);
        if(res.getCode() == 0) {
            leaveId = CommonMethod.getIntegerFromObject(res.getData());;
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
        res = HttpRequestUtil.request("/api/studentLeave/studentLeaveEditSave", req); //从后台获取所有学生信息列表集合
        int ret = MessageDialog.choiceDialog("确认要申请请假吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        if(res.getCode() == 0) {
            leaveId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("申请成功！");
            initialize();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }


    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对学生的增，删，改操作
     */


    public void clearPanel(){
        leaveId = null;
        leaveReasonField.setText("");
        startDatePick.getEditor().setText("");
        endDatePick.getEditor().setText("");
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
        leaveId = CommonMethod.getInteger(form,"leaveId");
        DataRequest req = new DataRequest();
        req.put("leaveId", leaveId);
        DataResponse res = HttpRequestUtil.request("/api/leave/leaveDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            initialize();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }


    }
    public void doNew(){
        clearPanel();
    }
    public void doDelete(){
        onDeleteButtonClick();
    }
}
