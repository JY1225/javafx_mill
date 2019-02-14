package cn.greatoo.easymill.ui.set.cnc;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.Smooth;
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
	
	public void init() {
		refresh();
		String programName = DBHandler.getInstance().getProgramName();
		if (programName != null) {
			XField.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().getX()));
			YField.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().getY()));
			ZField.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().getZ()));
		}
		XField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().setX(Float.parseFloat(XField.getText()));  
	        }
		});	
		YField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().setY(Float.parseFloat(YField.getText()));  
	        }
		});	
		ZField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().setZ(Float.parseFloat(ZField.getText()));  
	        }
		});			
	}

	private void refresh() {
		XField.setText("");
		YField.setText("");
		ZField.setText("");
	}
	@FXML
	public void resetBtAction(ActionEvent event) {
		
	}
	
}
