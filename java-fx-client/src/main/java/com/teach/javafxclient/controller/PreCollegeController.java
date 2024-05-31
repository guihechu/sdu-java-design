package com.teach.javafxclient.controller;

import com.teach.javafxclient.controller.base.LocalDateStringConverter;
import com.teach.javafxclient.controller.base.MessageDialog;
import com.teach.javafxclient.controller.base.ToolController;
import com.teach.javafxclient.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.teach.javafxclient.request.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreCollegeController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> politicalColumn;
    @FXML
    private TableColumn<Map,String> highSchoolColumn;
    @FXML
    private TableColumn<Map,String> graduationColumn;
    @FXML
    private TableColumn<Map,String> healthColumn;
    @FXML
    private TableColumn<Map,String> hometownColumn;
    @FXML
    private TableColumn<Map,String> nationColumn;
    @FXML
    private TableColumn<Map,String> changeColumn;

    @FXML
    private TextField numField;
    @FXML
    private ComboBox<OptionItem> politicalComboBox;
    @FXML
    private TextField highSchoolField;
    @FXML
    private DatePicker graduationPick;
    @FXML
    private TextField healthField;
    @FXML
    private TextField hometownField;
    @FXML
    private TextField nationField;
    @FXML
    private TextField changeField;
    @FXML
    private TextField numNameTextField;
    private Integer infoId = null;

    private ArrayList<Map> preCollegeList = new ArrayList<>();
    private List<OptionItem> politicalList;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    public void setDataTableView(){
        observableList.clear();
        for (int j = 0; j < preCollegeList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(preCollegeList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize(){
        DataResponse res;
        DataRequest req = new DataRequest();
        req.put("numName","");
        res = HttpRequestUtil.request("/api/preCollege/getPreCollegeList",req);
        if( req != null && res.getCode()== 0 ){
            preCollegeList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        politicalColumn.setCellValueFactory(new MapValueFactory<>("politicalName"));
        highSchoolColumn.setCellValueFactory(new MapValueFactory<>("highSchool"));
        graduationColumn.setCellValueFactory(new MapValueFactory<>("graduation"));
        healthColumn.setCellValueFactory(new MapValueFactory<>("health"));
        hometownColumn.setCellValueFactory(new MapValueFactory<>("hometown"));
        nationColumn.setCellValueFactory(new MapValueFactory<>("nation"));
        changeColumn.setCellValueFactory(new MapValueFactory<>("change"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setDataTableView();
        politicalList = HttpRequestUtil.getDictionaryOptionItemList("MMM");
        politicalComboBox.getItems().addAll(politicalList);
        graduationPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    public void clearPanel(){
        infoId = null;
        numField.setText("");
        politicalComboBox.getSelectionModel().select(-1);
        highSchoolField.setText("");
        healthField.setText("");
        graduationPick.getEditor().setText("");
        nationField.setText("");
        hometownField.setText("");
        changeField.setText("");
    }

    protected void changePreCollegeInfo(){
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null){
            clearPanel();
            return;
        }
        infoId = CommonMethod.getInteger(form,"infoId");
        DataRequest req = new DataRequest();
        req.put("infoId",infoId);
        DataResponse res = HttpRequestUtil.request("/api/preCollege/getPreCollegeInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form,"num"));
        politicalComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(politicalList, CommonMethod.getString(form, "political")));
        healthField.setText(CommonMethod.getString(form,"health"));
        highSchoolField.setText(CommonMethod.getString(form,"highSchool"));
        graduationPick.getEditor().setText(CommonMethod.getString(form,"graduation"));
        nationField.setText(CommonMethod.getString(form,"nation"));
        hometownField.setText(CommonMethod.getString(form,"hometown"));
        changeField.setText(CommonMethod.getString(form,"change"));
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changePreCollegeInfo();
    }

    @FXML
    protected void onQueryButtonClick(){
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/preCollege/getPreCollegeList",req);
        if(res != null && res.getCode() == 0){
            preCollegeList = (ArrayList<Map>) res.getData();
            setDataTableView();
        }
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
        int infoId= CommonMethod.getInteger(form,"infoId");
        DataRequest req = new DataRequest();
        req.put("infoId", infoId);
        DataResponse res = HttpRequestUtil.request("/api/preCollege/preCollegeDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    public void onSaveButtonClick(){
        if(numField.getText().equals("")){
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num",numField.getText());
        if(politicalComboBox.getSelectionModel() != null && politicalComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("political",politicalComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("highSchool",highSchoolField.getText());
        form.put("graduation",graduationPick.getEditor().getText());
        form.put("health",healthField.getText());
        form.put("nation",nationField.getText());
        form.put("hometown",hometownField.getText());
        form.put("change",changeField.getText());
        DataRequest req = new DataRequest();
        req.put("infoId", infoId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/preCollege/preCollegeEditSave",req);
        if(res.getCode() == 0) {
            infoId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
        onQueryButtonClick();
    }

    public void doNew(){
        clearPanel();
    }
    public void doSave(){
        onSaveButtonClick();
    }
    public void doDelete(){
        onDeleteButtonClick();
    }

}
