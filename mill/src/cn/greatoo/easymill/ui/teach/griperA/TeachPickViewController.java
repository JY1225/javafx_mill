package cn.greatoo.easymill.ui.teach.griperA;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import cn.greatoo.easymill.entity.Coordinates;
import javafx.event.ActionEvent;

import javafx.scene.control.CheckBox;

import javafx.scene.layout.GridPane;

public class TeachPickViewController {
	@FXML
	private GridPane generalGridPane;
	@FXML
	private TextField xTextField;
	@FXML
	private Button xResetBt;
	@FXML
	private TextField wTextField;
	@FXML
	private Button wResetBt;
	@FXML
	private TextField yTextField;
	@FXML
	private Button yResetBt;
	@FXML
	private TextField pTextField;
	@FXML
	private Button pResetBt;
	@FXML
	private TextField zTextField;
	@FXML
	private Button zResetBt;
	@FXML
	private TextField rTextField;
	@FXML
	private Button rResetBt;
	@FXML
	private CheckBox checkBox;
	@FXML
	private Button changeBt;
	private Coordinates unloadStacherOffset = new Coordinates();
	public void init() {
		
		
	}
	@FXML
	public void xResetBtAction(ActionEvent event) {
		
	}
	
	@FXML
	public void wResetBtAction(ActionEvent event) {
		
	}
	
	@FXML
	public void yResetBtAction(ActionEvent event) {
		
	}
	
	@FXML
	public void pResetBtAction(ActionEvent event) {
		
	}
	
	@FXML
	public void zResetBtAction(ActionEvent event) {
		
	}
	
	@FXML
	public void rResetBtAction(ActionEvent event) {
		
	}
	
	@FXML
	public void checkBoxAction(ActionEvent event) {
		
	}
	
	@FXML
	public void changeBtAction(ActionEvent event) {
		unloadStacherOffset.setX(Float.parseFloat(xTextField.getText()));
		unloadStacherOffset.setY(Float.parseFloat(yTextField.getText()));
		unloadStacherOffset.setZ(Float.parseFloat(zTextField.getText()));
		unloadStacherOffset.setW(Float.parseFloat(wTextField.getText()));
		unloadStacherOffset.setP(Float.parseFloat(pTextField.getText()));
		unloadStacherOffset.setR(Float.parseFloat(rTextField.getText()));
		
	}
	
}
