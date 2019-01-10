package cn.greatoo.easymill.ui.configure.devicesConfig;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.UserFrame;
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
	public static UserFrame stackerFrame = new UserFrame();	
	public static UserFrame cncFrame = new UserFrame();
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
		if (editMode) {
			isDisSelect(bts, editBt);			
			contentGridPane.setVisible(false);
			addBt.setDisable(false);
			comboBox.setDisable(false);
			editMode = false;
		} else {	
			isClicked(bts, editBt);
			nameText.setText((String)comboBox.getValue());
			contentGridPane.setVisible(true);
			addBt.setDisable(true);
			comboBox.setDisable(true);
			saveBt.setDisable(false);
			editMode = true;
		}
	}
	
	@FXML
	public void addBtAction(ActionEvent event) {	
		if (!editMode) {	
			isClicked(bts, addBt);			
			contentGridPane.setVisible(true);
			editBt.setDisable(true);
			comboBox.setDisable(true);
			saveBt.setDisable(true);
			editMode = true;
		} else {
			isDisSelect(bts, addBt);
			contentGridPane.setVisible(false);
			editBt.setDisable(false);
			comboBox.setDisable(false);
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
		String name = nameText.getText();
		Coordinates location = new Coordinates();
		location.setX(Float.parseFloat(XText.getText()));
		location.setY(Float.parseFloat(YText.getText()));
		location.setZ(Float.parseFloat(ZText.getText()));
		location.setW(Float.parseFloat(WText.getText()));
		location.setP(Float.parseFloat(PText.getText()));
		location.setR(Float.parseFloat(RText.getText()));
		if(name.equals("STACKER")) {
			stackerFrame.setName(name);
			stackerFrame.setNumber(Integer.parseInt(NrText.getText()));
			stackerFrame.setzSafeDistance(Float.parseFloat(ZSafeText.getText()));			
			stackerFrame.setLocation(location);
		}else {
			cncFrame.setName(name);
			cncFrame.setNumber(Integer.parseInt(NrText.getText()));
			cncFrame.setzSafeDistance(Float.parseFloat(ZSafeText.getText()));			
			cncFrame.setLocation(location);
		}
		
	}
	@Override
	public void setMessege(String mess) {
		
		
	}
	
	
}
