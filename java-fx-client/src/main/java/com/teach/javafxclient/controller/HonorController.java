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

/**
 * HonorController 登录交互控制类 对应 honor_panel.fxml  对应荣誉信息管理的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class HonorController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  //荣誉信息表
    @FXML
    private TableColumn<Map,String> numColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map,String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map,String> honorNumColumn;   //荣誉信息表 编号列
    @FXML
    private TableColumn<Map,String> honorDayColumn;   //荣誉信息表 获得时间列
    @FXML
    private TableColumn<Map,String> honorNameColumn; //荣誉信息表 荣誉名称列
    @FXML
    private TableColumn<Map,String> honorGradeColumn; //荣誉信息表 荣誉等级列
    @FXML
    private TableColumn<Map,String> honorTypeColumn; //荣誉信息表 荣誉等级列

    @FXML
    private TextField numField; //学生信息  学号输入域
    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField honorNumField; //荣誉信息  编号输入域
    @FXML
    private DatePicker honorDayPick;  //荣誉信息  获得时间输入域
    @FXML
    private ComboBox<OptionItem> honorTypeComboBox;  //荣誉信息  荣誉等级输入域
    @FXML
    private ComboBox<OptionItem> honorGradeComboBox;  //荣誉信息  荣誉等级输入域
    @FXML
    private TextField honorNameField; //学生信息  荣誉名称输入域

    @FXML
    private TextField numNameTextField;  //查询 学号输入域
    private Integer honorId = null;
    private List<OptionItem> honorTypeList;   //荣誉类型选择列表数据
    private List<OptionItem> honorGradeList;   //荣誉等级选择列表数据
    private ArrayList<Map> honorList = new ArrayList();  // 荣誉信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    /**
     * 将学生荣誉信息数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < honorList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(honorList.get(j)));
        }
        dataTableView.setItems(observableList);
    }
    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req = new DataRequest();
        req.put("numName", "");
        res = HttpRequestUtil.request("/api/honor/getHonorList", req); //从后台获取所有学生荣誉信息信息列表集合
        if (res != null && res.getCode() == 0) {
            honorList = (ArrayList<Map>) res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));
        nameColumn.setCellValueFactory(new MapValueFactory("name"));
        honorNumColumn.setCellValueFactory(new MapValueFactory("honorNum"));  //设置列值工程属性
        honorDayColumn.setCellValueFactory(new MapValueFactory<>("honorDay"));
        honorTypeColumn.setCellValueFactory(new MapValueFactory<>("honorTypeName"));
        honorGradeColumn.setCellValueFactory(new MapValueFactory<>("honorGradeName"));
        honorNameColumn.setCellValueFactory(new MapValueFactory<>("honorName"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        honorGradeList = HttpRequestUtil.getDictionaryOptionItemList("HBM");
        honorGradeComboBox.getItems().addAll(honorGradeList);
        honorTypeList = HttpRequestUtil.getDictionaryOptionItemList("OBM");
        honorTypeComboBox.getItems().addAll(honorTypeList);
        honorDayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    /**
     * 清除学生荣誉信息表单中输入信息
     */
    public void clearPanel(){
        honorId = null;
        numField.setText("");
        nameField.setText("");
        honorNumField.setText("");
        honorGradeComboBox.getSelectionModel().select(-1);
        honorDayPick.getEditor().setText("");
        honorNameField.setText("");
    }

    protected void changeHonorInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        honorId = CommonMethod.getInteger(form, "honorId");
        DataRequest req = new DataRequest();
        req.put("honorId", honorId);
        DataResponse res = HttpRequestUtil.request("/api/honor/getHonorInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        honorNumField.setText(CommonMethod.getString(form, "honorNum"));
        honorNameField.setText(CommonMethod.getString(form, "honorName"));
        honorTypeComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(honorTypeList, CommonMethod.getString(form, "honorType")));
        honorGradeComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(honorGradeList, CommonMethod.getString(form, "honorGrade")));
        honorDayPick.getEditor().setText(CommonMethod.getString(form, "honorDay"));
    }
    /**
     * 点击学生列表的某一行，根据honorId ,从后台查询学生的基本信息，切换学生的编辑信息
     */
    public void onTableRowSelect(ListChangeListener.Change<? extends Integer>  change){
        changeHonorInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生荣誉信息列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/honor/getHonorList",req);
        if(res != null && res.getCode()== 0) {
            honorList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }

    }
    /**
     *  添加新荣誉信息， 清空输入信息， 输入相关信息，点击保存即可添加新的学生
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 点击删除按钮 删除当前编辑的学生荣誉信息的数据
     */
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
        honorId = CommonMethod.getInteger(form,"honorId");
        DataRequest req = new DataRequest();
        req.put("honorId", honorId);
        DataResponse res = HttpRequestUtil.request("/api/honor/honorDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    /**
     * 点击保存按钮，保存当前编辑的学生荣誉信息，如果是新添加的学生，后台添加学生
     */
    @FXML
    protected void onSaveButtonClick() {
        numField.getText();
        if( numField.getText().equals("")) {
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num",numField.getText());
        form.put("name",nameField.getText());
        form.put("honorNum",honorNumField.getText());
        form.put("honorName",honorNameField.getText());
        if(honorTypeComboBox.getSelectionModel() != null && honorTypeComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("honorType",honorTypeComboBox.getSelectionModel().getSelectedItem().getValue());
        if(honorGradeComboBox.getSelectionModel() != null && honorGradeComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("honorGrade",honorGradeComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("honorDay",honorDayPick.getEditor().getText());
        DataRequest req = new DataRequest();
        req.put("honorId", honorId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/honor/honorEditSave",req);
        if(res.getCode() == 0) {
            honorId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }

    }










}