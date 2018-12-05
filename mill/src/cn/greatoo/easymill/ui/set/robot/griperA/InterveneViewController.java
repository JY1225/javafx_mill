package cn.greatoo.easymill.ui.set.robot.griperA;

import javafx.fxml.FXML;

import javafx.scene.control.TextField;

import javafx.event.ActionEvent;

import javafx.scene.input.TouchEvent;

import javafx.scene.layout.GridPane;

public class InterveneViewController {
	@FXML
	private GridPane gridPane;
	@FXML
	private TextField beTextField;
	@FXML
	private TextField afTextField;

	public void init() {
		beTextField.setDisable(true);
		afTextField.setDisable(true);
		
	}
	@FXML
	public void beBtAction(ActionEvent event) {
		beTextField.setDisable(false);
		afTextField.setDisable(true);
	}
	
	@FXML
	public void afBtAction(ActionEvent event) {
		beTextField.setDisable(true);
		afTextField.setDisable(false);
	}
	@FXML
	public void touchPressed(TouchEvent event) {
		beTextField.setDisable(true);
		afTextField.setDisable(true);
	}
}
