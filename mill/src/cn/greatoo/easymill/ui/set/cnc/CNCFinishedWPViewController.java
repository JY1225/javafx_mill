package cn.greatoo.easymill.ui.set.cnc;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.util.NumericTextField;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;

public class CNCFinishedWPViewController implements TextInputControlListener {
	@FXML
	private GridPane generalGridPane;
	@FXML
	private NumericTextField fullnumL;
	@FXML
	private NumericTextField fullnumW;
	@FXML
	private NumericTextField fullnumH;
	@FXML
	private Button hResetBt;
	@FXML
	private NumericTextField fullnumWT;
	@FXML
	private Button calculateBt;
	@FXML
	private Button resetBt;
	@FXML
	private Button wResetBt;
	@FXML
	private Button LResetBt;
	private String programName;	

	public void init() {	
		refresh();
		programName = DBHandler.getInstance().getProgramName();
		if (programName != null) {
			fullnumL.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC()
				.getWorkPiece().getLength()));
			fullnumW.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC()
				.getWorkPiece().getWidth()));
			fullnumH.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC()
				.getWorkPiece().getHeight()));
			fullnumWT.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC()
				.getWorkPiece().getWeight()));
		}
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
				.setType(WorkPiece.Type.FINISHED);
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
				.setShape(WorkPiece.WorkPieceShape.CUBIC);
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
		.setMaterial(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getMaterial());
		fullnumL.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
						.setLength(Float.parseFloat(fullnumL.getText()));
			}
		});

		fullnumW.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
						.setWidth(Float.parseFloat(fullnumW.getText()));
			}
		});

		fullnumH.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
						.setHeight(Float.parseFloat(fullnumH.getText()));
			}
		});

		fullnumWT.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
						.setWeight(Float.parseFloat(fullnumWT.getText()));
			}
		});
		setTextFieldListener(this);
	}

	private void refresh() {
		fullnumL.setText("");
		fullnumW.setText("");
		fullnumH.setText("");
		fullnumWT.setText("");
	}
	@FXML
	public void hResetBtAction(ActionEvent event) {

	}

	@FXML
	public void calculateBtAction(ActionEvent event) {
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
				.setWeight(Float.parseFloat(fullnumWT.getText()));
	}

	// Event Listener on Button[#resetBt].onAction
	@FXML
	public void resetBtAction(ActionEvent event) {
		// TODO Autogenerated
	}

	// Event Listener on Button[#wResetBt].onAction
	@FXML
	public void wResetBtAction(ActionEvent event) {
		// TODO Autogenerated
	}

	// Event Listener on Button[#LResetBt].onAction
	@FXML
	public void LResetBtAction(ActionEvent event) {
		// TODO Autogenerated
	}

	public void setTextFieldListener(final TextInputControlListener listener) {
		fullnumL.setFocusListener(listener);
		fullnumW.setFocusListener(listener);
		fullnumH.setFocusListener(listener);
		fullnumWT.setFocusListener(listener);
	}

	@Override
	public void closeKeyboard() {
		// TODO Auto-generated method stub

	}

	@Override
	public void textFieldFocussed(TextInputControl textInputControl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void textFieldLostFocus(TextInputControl textInputControl) {
		// TODO Auto-generated method stub

	}
}
