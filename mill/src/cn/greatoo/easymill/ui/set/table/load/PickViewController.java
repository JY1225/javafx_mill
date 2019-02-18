package cn.greatoo.easymill.ui.set.table.load;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.NumericTextField;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class PickViewController  extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private NumericTextField XField;
	@FXML
	private NumericTextField YField;
	@FXML
	private NumericTextField ZField;
	@FXML
	private Button resetBt;

	public void init() {
		setTextFieldListener(this);
		refresh();
		String programName = DBHandler.getInstance().getProgramName();	
		if (programName != null) {
			XField.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().getX()));
			YField.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().getY()));
			ZField.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().getZ()));
		}
		
		XField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().setX(Float.parseFloat(XField.getText()));
	        }
		});	
		YField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().setY(Float.parseFloat(YField.getText()));  
	        }
		});	
		ZField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) { 
	        		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().setZ(Float.parseFloat(ZField.getText())); 
	        }
		});	
	}
	
	private void refresh() {
		XField.setText("");
		YField.setText("");
		ZField.setText("");
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

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTextFieldListener(TextInputControlListener listener) {
		XField.setFocusListener(listener);
		YField.setFocusListener(listener);
		ZField.setFocusListener(listener);
		
	}
	
}
