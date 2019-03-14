package cn.greatoo.easymill.ui.teach.griperB;

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

public class TeachGriperBPutViewController {
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
	public static Coordinates loadStackerOffset = new Coordinates();
	
	public void init() {
		String programName = DBHandler.getInstance().getProgramName();
		if (programName != null) {
			Program program = DBHandler.getInstance().getProgramBuffer().get(programName);
			loadStackerOffset = program.getLoadstacker().getOffset();
			xTextField.setText(String.valueOf(loadStackerOffset.getX()));
			yTextField.setText(String.valueOf(loadStackerOffset.getY()));
			zTextField.setText(String.valueOf(loadStackerOffset.getZ()));
			wTextField.setText(String.valueOf(loadStackerOffset.getW()));
			pTextField.setText(String.valueOf(loadStackerOffset.getP()));
			rTextField.setText(String.valueOf(loadStackerOffset.getR()));			
		}
		xTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	loadStackerOffset.setX(Float.parseFloat(xTextField.getText()));
	        }
		});	
		yTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	loadStackerOffset.setY(Float.parseFloat(yTextField.getText()));
	        }
		});	
		zTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	loadStackerOffset.setZ(Float.parseFloat(zTextField.getText()));
	        }
		});	
		wTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	loadStackerOffset.setW(Float.parseFloat(wTextField.getText()));
	        }
		});	
		pTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	loadStackerOffset.setP(Float.parseFloat(pTextField.getText()));
	        }
		});	
		rTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	loadStackerOffset.setR(Float.parseFloat(rTextField.getText()));
	        }
		});	
	}
	@FXML
	public void xResetBtAction(ActionEvent event) {
		// TODO Autogenerated
	}
	// Event Listener on Button[#wResetBt].onAction
	@FXML
	public void wResetBtAction(ActionEvent event) {
		// TODO Autogenerated
	}
	// Event Listener on Button[#yResetBt].onAction
	@FXML
	public void yResetBtAction(ActionEvent event) {
		// TODO Autogenerated
	}
	// Event Listener on Button[#pResetBt].onAction
	@FXML
	public void pResetBtAction(ActionEvent event) {
		// TODO Autogenerated
	}
	// Event Listener on Button[#zResetBt].onAction
	@FXML
	public void zResetBtAction(ActionEvent event) {
		// TODO Autogenerated
	}
	// Event Listener on Button[#rResetBt].onAction
	@FXML
	public void rResetBtAction(ActionEvent event) {
		// TODO Autogenerated
	}
	// Event Listener on CheckBox[#checkBox].onAction
	@FXML
	public void checkBoxAction(ActionEvent event) {
		// TODO Autogenerated
	}
	// Event Listener on Button[#changeBt].onAction
	@FXML
	public void changeBtAction(ActionEvent event) {
		loadStackerOffset.setX(Float.parseFloat(xTextField.getText()));
		loadStackerOffset.setY(Float.parseFloat(yTextField.getText()));
		loadStackerOffset.setZ(Float.parseFloat(zTextField.getText()));
		loadStackerOffset.setW(Float.parseFloat(wTextField.getText()));
		loadStackerOffset.setP(Float.parseFloat(pTextField.getText()));
		loadStackerOffset.setR(Float.parseFloat(rTextField.getText()));
		try {
			CoordinatesHandler.saveCoordinates(loadStackerOffset);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}