package cn.greatoo.easymill.ui.set.table.load;

import java.util.Map;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.entity.WorkPiece.Material;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class PickViewController {
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

	public static Smooth unloadStackerSmooth = new Smooth();
	public void init() {
		String programName = DBHandler.getInstance().getProgramName();
		if(programName != null) {
			Program program = DBHandler.getInstance().getProgramBuffer().get(programName);
			unloadStackerSmooth = program.getUnloadstacker().getSmooth();			
			XField.setText(String.valueOf(unloadStackerSmooth.getX()));
			YField.setText(String.valueOf(unloadStackerSmooth.getY()));
			ZField.setText(String.valueOf(unloadStackerSmooth.getZ()));
		}
		
		XField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	unloadStackerSmooth.setX(Float.parseFloat(XField.getText()));  
	        }
		});	
		YField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	unloadStackerSmooth.setY(Float.parseFloat(YField.getText()));  
	        }
		});	
		ZField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	unloadStackerSmooth.setZ(Float.parseFloat(ZField.getText()));  
	        }
		});	
	}
	
	@FXML
	public void XFieldAction(ActionEvent event) {
		
	}
	
	@FXML
	public void YField(ActionEvent event) {
		
	}
	
	@FXML
	public void ZField(ActionEvent event) {
		
	}
	
	@FXML
	public void resetBtAction(ActionEvent event) {
		
	}
	
}
