package com.teach.javafxclient.controller;

import com.teach.javafxclient.controller.base.LocalDateStringConverter;
import com.teach.javafxclient.controller.base.ToolController;
import com.teach.javafxclient.request.*;
import com.teach.javafxclient.util.CommonMethod;
import com.teach.javafxclient.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FeeController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> dayColumn;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,Double> moneyColumn;

    @FXML
    private TextField numField;
    @FXML
    private DatePicker dayPick;
    @FXML
    private  TextField moneyField;

    @FXML
    private TextField numTextField;

    private Integer feeId = null;
    private ArrayList<Map> feeList = new ArrayList();
    private ObservableList<Map> observableList =FXCollections.observableArrayList();

    private void setTableViewData(){
        observableList.clear();
        for(int j = 0;j < feeList.size();j++){
            observableList.addAll(FXCollections.observableArrayList(feeList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    private void initialize(){
        DataResponse res;
        DataRequest req = new DataRequest();
        req.put("num","");
        res = HttpRequestUtil.request("/api/fee/getFeeList",req);
        if(res != null && res.getCode() == 0){
            feeList = (ArrayList<Map>) res.getData();
        }
        dayColumn.setCellValueFactory(new MapValueFactory("day"));
        numColumn.setCellValueFactory(new MapValueFactory("num"));
        moneyColumn.setCellValueFactory(new MapValueFactory("money"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        dayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    public void clearPanel(){
        feeId = null;
        dayPick.getEditor().setText("");
        numField.setText("");
        moneyField.setText("");
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null){
            clearPanel();
            return;
        }
        feeId = CommonMethod.getInteger(form,"feeId");
        DataRequest req = new DataRequest();
        req.put("feeId",feeId);
        DataResponse res = HttpRequestUtil.request("/api/fee/getFeeInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form,"num"));
        dayPick.getEditor().setText(CommonMethod.getString(form,"day"));
        moneyField.setText(CommonMethod.getString(form,"money"));
    }

    @FXML
    protected void onQueryButtonClick(){
        String num = numTextField.getText();
        DataRequest req = new DataRequest();
        req.put("num",num);
        DataResponse res = HttpRequestUtil.request("/api/fee/getFeeList",req);
        if(res != null && res.getCode() == 0){
            feeList = (ArrayList<Map>) res.getData();
                    setTableViewData();
        }
    }

    @FXML
    protected void onAddButtonClick(){clearPanel();}

    @FXML
    protected void onDeleteButtonClick(){
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null){
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗？");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        feeId = CommonMethod.getInteger(form,"feeId");
        DataRequest req = new DataRequest();
        req.put("feeId",feeId);
        DataResponse res = HttpRequestUtil.request("/api/fee/feeDelete",req);
        if(res.getCode() == 0){
            MessageDialog.showDialog("删除成功");
            onQueryButtonClick();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    protected void onSaveButtonClick(){
        if( numField.getText().equals("")){
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num",numField.getText());
        form.put("day",dayPick.getEditor().getText());
        form.put("money",moneyField.getText());
        DataRequest req = new DataRequest();
        req.put("feeId",feeId);
        req.put("form",form);
        DataResponse res =HttpRequestUtil.request("/api/fee/feeEditSave",req);
        onQueryButtonClick();
        if(res.getCode() == 0){
            feeId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }


}
