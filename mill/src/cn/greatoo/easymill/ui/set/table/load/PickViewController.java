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
	private String programName;
	public void init() {
		setTextFieldListener(this);
		refresh();
		programName = DBHandler.getInstance().getProgramName();	
		XField.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().getX()));
		YField.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().getY()));
		ZField.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().getZ()));
		
		XField.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().setX(newValue);
            }
        });
		
		YField.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().setY(newValue);
            }
        });
		
		ZField.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().setZ(newValue);
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
		XField.setText(String.valueOf(DBHandler.getInstance().getOProgram().getUnloadstacker().getSmooth().getX()));
		YField.setText(String.valueOf(DBHandler.getInstance().getOProgram().getUnloadstacker().getSmooth().getY()));
		ZField.setText(String.valueOf(DBHandler.getInstance().getOProgram().getUnloadstacker().getSmooth().getZ()));
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().setX(DBHandler.getInstance().getOProgram().getUnloadstacker().getSmooth().getX());
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().setY(DBHandler.getInstance().getOProgram().getUnloadstacker().getSmooth().getY());
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getSmooth().setZ(DBHandler.getInstance().getOProgram().getUnloadstacker().getSmooth().getZ());
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
