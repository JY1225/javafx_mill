package cn.greatoo.easymill.ui.set.cnc;

import cn.greatoo.easymill.entity.Coordinates;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CNCPickViewController {
	@FXML
	private GridPane gridPane;
	@FXML
	private TextField XField;
	@FXML
	private TextField YField;
	@FXML
	private TextField ZField;
	@FXML
	private Button resetBt;
	public static Coordinates cncPicSmooth = new Coordinates();
	
	public void init() {
		XField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	cncPicSmooth.setX(Float.parseFloat(XField.getText()));  
	        }
		});	
		YField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	cncPicSmooth.setY(Float.parseFloat(YField.getText()));  
	        }
		});	
		ZField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	cncPicSmooth.setZ(Float.parseFloat(ZField.getText()));  
	        }
		});	
		
	}

	@FXML
	public void resetBtAction(ActionEvent event) {
		
	}
	
}
