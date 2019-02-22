package cn.greatoo.easymill.ui.set.cnc;

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

public class CNCPickViewController extends Controller {
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
		refresh();
		setTextFieldListener(this);
		programName = DBHandler.getInstance().getProgramName();
		if (programName != null) {
			XField.setText(String.valueOf(
					DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().getX()));
			YField.setText(String.valueOf(
					DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().getY()));
			ZField.setText(String.valueOf(
					DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().getZ()));
		}
		XField.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth()
				.setX(newValue);
            }
        });
		
		YField.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth()
				.setY(newValue);
            }
        });
		
		ZField.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth()
				.setZ(newValue);
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
		XField.setText(String.valueOf(DBHandler.getInstance().getOProgram().getUnloadCNC().getSmooth().getX()));
		YField.setText(String.valueOf(DBHandler.getInstance().getOProgram().getUnloadCNC().getSmooth().getY()));
		ZField.setText(String.valueOf(DBHandler.getInstance().getOProgram().getUnloadCNC().getSmooth().getZ()));
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().setX(DBHandler.getInstance().getOProgram().getUnloadCNC().getSmooth().getX());
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().setY(DBHandler.getInstance().getOProgram().getUnloadCNC().getSmooth().getY());
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getSmooth().setZ(DBHandler.getInstance().getOProgram().getUnloadCNC().getSmooth().getZ());
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
