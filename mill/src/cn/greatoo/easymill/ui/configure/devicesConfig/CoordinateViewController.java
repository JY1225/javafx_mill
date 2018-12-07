package cn.greatoo.easymill.ui.configure.devicesConfig;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.ui.main.Controller;
import javafx.event.ActionEvent;

import javafx.scene.control.ComboBox;

import javafx.scene.layout.GridPane;

public class CoordinateViewController extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private GridPane contentGridPane;
	@FXML
	private Button editBt;
	@FXML
	private Button addBt;
	@FXML
	private ComboBox comboBox;
	@FXML
	private TextField nameText;
	@FXML
	private TextField NrText;
	@FXML
	private TextField ZSafeText;
	@FXML
	private TextField XText;
	@FXML
	private TextField YText;
	@FXML
	private TextField ZText;
	@FXML
	private TextField WText;
	@FXML
	private TextField PText;
	@FXML
	private TextField RText;
	@FXML
	private Button saveBt;

	private boolean editMode;
	List<Button> bts;
	@SuppressWarnings("unchecked")
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(editBt);
		bts.add(addBt);
		
		editMode = false;
		contentGridPane.setVisible(false);
		comboBox.getItems().add("CNC MACHINE");
		comboBox.getItems().add("STACKER");
		
		if(comboBox.getValue() != null) {
			editBt.setDisable(false);
		}else {
			editBt.setDisable(true);
		}
		
	}
	@FXML
	public void editBtAction(ActionEvent event) {
		isClicked(bts, editBt);
		if (editMode) {
			contentGridPane.setVisible(false);
			editMode = false;
		} else {			
			contentGridPane.setVisible(true);
			saveBt.setDisable(false);
			editMode = true;
		}
	}
	
	@FXML
	public void addBtAction(ActionEvent event) {	
		isClicked(bts, addBt);
		if (!editMode) {				
			contentGridPane.setVisible(true);
			saveBt.setDisable(true);
			editMode = true;
		} else {
			contentGridPane.setVisible(false);
			editMode = false;
		}
	}
	
	@FXML
	public void comboBoxAction(ActionEvent event) {		
		if(comboBox.getValue() != null) {
			editBt.setDisable(false);
		}else {
			editBt.setDisable(true);
		}
		
	}
	
	@FXML
	public void saveBtAction(ActionEvent event) {
		
	}
	
	
}
