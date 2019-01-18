package cn.greatoo.easymill.ui.teach.griperA;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import java.sql.SQLException;

import cn.greatoo.easymill.db.util.CoordinatesHandler;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Program;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	public static Coordinates unloadStacherOffset = new Coordinates();

	public void init() {
		String programName = DBHandler.getInstance().getProgramName();
		if (programName != null) {
			Program program = DBHandler.getInstance().getProgramBuffer().get(programName);
			unloadStacherOffset = program.getUnloadstacker().getOffset();
			xTextField.setText(String.valueOf(unloadStacherOffset.getX()));
			yTextField.setText(String.valueOf(unloadStacherOffset.getY()));
			zTextField.setText(String.valueOf(unloadStacherOffset.getZ()));
			wTextField.setText(String.valueOf(unloadStacherOffset.getW()));
			pTextField.setText(String.valueOf(unloadStacherOffset.getP()));
			rTextField.setText(String.valueOf(unloadStacherOffset.getR()));			
		}
		xTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	unloadStacherOffset.setX(Float.parseFloat(xTextField.getText()));
	        }
		});	
		yTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	unloadStacherOffset.setY(Float.parseFloat(yTextField.getText()));
	        }
		});	
		zTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	unloadStacherOffset.setZ(Float.parseFloat(zTextField.getText()));
	        }
		});	
		wTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	unloadStacherOffset.setW(Float.parseFloat(wTextField.getText()));
	        }
		});	
		pTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	unloadStacherOffset.setP(Float.parseFloat(pTextField.getText()));
	        }
		});	
		rTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	unloadStacherOffset.setR(Float.parseFloat(rTextField.getText()));
	        }
		});	
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
		try {
			CoordinatesHandler.saveCoordinates(unloadStacherOffset);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
