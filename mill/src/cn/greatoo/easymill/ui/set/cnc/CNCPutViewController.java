package cn.greatoo.easymill.ui.set.cnc;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.RobotSetting;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.ui.main.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CNCPutViewController extends Controller {
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
	@FXML
	private Button beBt;
	@FXML
	private Button aftBt;
	List<Button> bts;

	public void init() {
		bts = new ArrayList<Button>();
		bts.add(beBt);
		bts.add(aftBt);
		refresh();
		String programName = DBHandler.getInstance().getProgramName();
		if (programName != null) {
			XField.setText(String
				.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth().getX()));
			YField.setText(String
				.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth().getY()));
			ZField.setText(String
				.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth().getZ()));
		
		if (DBHandler.getInstance().getProgramBuffer().get(programName).getRobotSetting().isReleaseBeforeMachine()) {
			isClicked(bts, beBt);
		} else {
			isClicked(bts, aftBt);
		}
		}
		XField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth()
						.setX(Float.parseFloat(XField.getText()));
			}
		});
		YField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth()
						.setY(Float.parseFloat(YField.getText()));
			}
		});
		ZField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getLoadCNC().getSmooth()
						.setZ(Float.parseFloat(ZField.getText()));
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

}
