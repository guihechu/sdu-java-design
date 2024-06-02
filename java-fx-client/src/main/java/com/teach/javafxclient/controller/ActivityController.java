package com.teach.javafxclient.controller;

import com.teach.javafxclient.controller.base.MessageDialog;
import com.teach.javafxclient.controller.base.ToolController;
import com.teach.javafxclient.request.DataRequest;
import com.teach.javafxclient.request.DataResponse;
import com.teach.javafxclient.request.HttpRequestUtil;
import com.teach.javafxclient.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> numColumn;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, String> dateColumn;
    @FXML
    private TableColumn<Map, String> locationColumn;
    @FXML
    private TableColumn<Map, String> descriptionColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField numField;

    @FXML
    private TextField numNameTextField;

    private Integer activityId = null;
    private ArrayList<Map> activityList = new ArrayList();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < activityList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(activityList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    public void initialize() {
        DataResponse res;
        DataRequest req = new DataRequest();
        req.put("numName", "");
        res = HttpRequestUtil.request("/api/activity/getActivityList", req);
        if (res != null && res.getCode() == 0) {
            activityList = (ArrayList<Map>) res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));
        nameColumn.setCellValueFactory(new MapValueFactory("name"));
        dateColumn.setCellValueFactory(new MapValueFactory("date"));
        locationColumn.setCellValueFactory(new MapValueFactory("location"));
        descriptionColumn.setCellValueFactory(new MapValueFactory("description"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
    }

    public void clearPanel() {
        activityId = null;
        numField.setText("");
        dateField.setText("");
        nameField.setText("");
        locationField.setText("");
        descriptionField.setText("");
    }

    protected void changeActivityInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        activityId = CommonMethod.getInteger(form, "activityId");
        DataRequest req = new DataRequest();
        req.put("activityId", activityId);
        DataResponse res = HttpRequestUtil.request("/api/activity/getActivityInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        dateField.setText(CommonMethod.getString(form, "date"));
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        locationField.setText(CommonMethod.getString(form, "location"));
        descriptionField.setText(CommonMethod.getString(form, "description"));
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeActivityInfo();
    }

    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/activity/getActivityList", req);
        if (res != null && res.getCode() == 0) {
            activityList = (ArrayList<Map>) res.getData();
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
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        activityId = CommonMethod.getInteger(form, "activityId");
        DataRequest req = new DataRequest();
        req.put("activityId", activityId);
        DataResponse res = HttpRequestUtil.request("/api/activity/activityDelete", req);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("删除成功");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        if (numField.getText().equals("")) {
            MessageDialog.showDialog("活动编号为空，不能修改");
            return;
        }
        DataRequest req = new DataRequest();
        req.put("activityId", activityId);
        req.put("name", nameField.getText());
        req.put("num", numField.getText());
        req.put("date", dateField.getText());
        req.put("location", locationField.getText());
        req.put("description", descriptionField.getText());

        DataResponse res = HttpRequestUtil.request("/api/activity/activityEditSave", req);
        if (res.getCode() == 0) {
            activityId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功");
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
        onQueryButtonClick();
    }


    /*@FXML
    protected void onSaveButtonClick() {
        if( numField.getText().equals("")) {
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num",numField.getText());
        form.put("name",nameField.getText());
        form.put("date",dateField.getText());
        form.put("location",locationField.getText());
        form.put("description",descriptionField.getText());
        DataRequest req = new DataRequest();
        req.put("activityId", activityId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/activity/activityEditSave",req);
        if(res.getCode() == 0) {
            activityId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
        */


    public void doNew() {
        clearPanel();
    }

    public void doSave() {
        onSaveButtonClick();
    }

    public void doDelete() {
        onDeleteButtonClick();
    }
}


