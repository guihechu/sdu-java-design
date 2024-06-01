package com.teach.javafxclient.controller;

import com.teach.javafxclient.request.OptionItem;
import com.teach.javafxclient.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbsentEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> courseComboBox;
    private List<OptionItem> courseList;
    @FXML
    private TextField timeField;
    private AbsentTableController absentTableController= null;
    private Integer absentId= null;
    @FXML
    public void initialize(){

    }

    @FXML
    public void okButtonClick(){
        Map data = new HashMap();
        OptionItem op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("studentId",op.getId());
        }
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("courseId", Integer.parseInt(op.getValue()));
        }
        data.put("absentId",absentId);
        data.put("time",timeField.getText());
        absentTableController.doClose("ok",data);
    }

    @FXML
    public void cancelButtonClick(){absentTableController.doClose("cancel",null);}

    public void setAbsentTableController(AbsentTableController absentTableController){
        this.absentTableController = absentTableController;
    }

    public void init(){
        studentList = absentTableController.getStudentList();
        courseList = absentTableController.getCourseList();
        studentComboBox.getItems().addAll(studentList);
        courseComboBox.getItems().addAll(courseList);
    }

    public void showDialog(Map data){
        if(data == null){
            absentId = null;
            studentComboBox.getSelectionModel().select(-1);
            courseComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            courseComboBox.setDisable(false);
            timeField.setText("");
        }else{
            absentId = CommonMethod.getInteger(data,"absentId");
            studentComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(studentList, CommonMethod.getString(data, "studentNum")));
            courseComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(courseList,CommonMethod.getString(data,"courseNum")));
            studentComboBox.setDisable(true);
            courseComboBox.setDisable(true);
            timeField.setText(CommonMethod.getString(data,"time"));
        }
    }

}
