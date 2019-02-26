package cn.greatoo.easymill.ui.set.cnc;

import java.util.ArrayList;
import java.util.List;

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

public class CNCPutViewController extends Controller {
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
	@FXML
	private Button beBt;
	@FXML
	private Button aftBt;
	private List<Button> bts;
	private String programName;
	public void init() {
		setTextFieldListener(this);
		bts = new ArrayList<Button>();
		bts.add(beBt);
		bts.add(aftBt);
		refresh();
		programName = DBHandler.getInstance().getProgramName();
		XField.setText(String.valueOf(
				DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth().getX()));
		YField.setText(String.valueOf(
				DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth().getY()));
		ZField.setText(String.valueOf(
				DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth().getZ()));

		if (DBHandler.getInstance().getProgramBuffer().get(programName).getRobotSetting()
				.isReleaseBeforeMachine()) {
			isClicked(bts, beBt);
		} else {
			isClicked(bts, aftBt);
		}

		XField.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth()
				.setX(newValue);
            }
        });
		
		YField.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth()
				.setY(newValue);
            }
        });
		
		ZField.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth()
				.setZ(newValue);
            }
        });		

	}

	private void refresh() {
		XField.setText("");
		YField.setText("");
		ZField.setText("");
		isClicked(bts, null);
	}

	@FXML
	public void resetBtAction(ActionEvent event) {
		XField.setText(String.valueOf(DBHandler.getInstance().getOProgram().getLoadCNC().getSmooth().getX()));
		YField.setText(String.valueOf(DBHandler.getInstance().getOProgram().getLoadCNC().getSmooth().getY()));
		ZField.setText(String.valueOf(DBHandler.getInstance().getOProgram().getLoadCNC().getSmooth().getZ()));
		DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth().setX(DBHandler.getInstance().getOProgram().getLoadCNC().getSmooth().getX());
		DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth().setY(DBHandler.getInstance().getOProgram().getLoadCNC().getSmooth().getY());
		DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth().setZ(DBHandler.getInstance().getOProgram().getLoadCNC().getSmooth().getZ());
	}

	@FXML
	public void beBtAction(ActionEvent event) {
		isClicked(bts, beBt);
		DBHandler.getInstance().getProgramBuffer().get(DBHandler.getInstance().getProgramName()).getRobotSetting()
				.setReleaseBeforeMachine(true);
	}

	@FXML
	public void aftBtAction(ActionEvent event) {
		isClicked(bts, aftBt);
		DBHandler.getInstance().getProgramBuffer().get(DBHandler.getInstance().getProgramName()).getRobotSetting()
				.setReleaseBeforeMachine(false);
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
