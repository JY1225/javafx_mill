package cn.greatoo.easymill.ui.set.cnc;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.ui.general.NotificationBox;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.NumericTextField;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class CNCFinishedWPViewController extends Controller{
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
		setTextFieldListener(this);
		buildAlarmHBox(generalGridPane,0, 0, 2, 1);
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
		
		fullnumL.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	if(newValue > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getLength()
            			||Float.valueOf(fullnumW.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWidth()
            			||Float.valueOf(fullnumH.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getHeight()
            			||Float.valueOf(fullnumWT.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWeight()) {
            		showNotification("已加工工件不得大于待加工工件", NotificationBox.Type.WARNING);
            	}else {
            		hideNotification();
            		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
            		.setLength(newValue);
            	}
            }
        });

		fullnumW.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	if(newValue > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWidth()
            			||Float.valueOf(fullnumL.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getLength()
            			||Float.valueOf(fullnumH.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getHeight()
            			||Float.valueOf(fullnumWT.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWeight()) {
            		showNotification("已加工工件不得大于待加工工件", NotificationBox.Type.WARNING);
            	}else {
            		hideNotification();
            		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
					.setWidth(newValue);
            	}
            }
        });

		fullnumH.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	if(newValue > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getHeight()
            			||Float.valueOf(fullnumL.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getLength()
            			||Float.valueOf(fullnumW.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWidth()
            			||Float.valueOf(fullnumWT.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWeight()) {
            		showNotification("已加工工件不得大于待加工工件", NotificationBox.Type.WARNING);
            	}else {
            		hideNotification();
            		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
					.setHeight(newValue);
            	}
            }
        });

		fullnumWT.setOnChange(new ChangeListener<Float>() {
            @Override
            public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
            	if(newValue > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWeight()
            			||Float.valueOf(fullnumL.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getLength()
            			||Float.valueOf(fullnumW.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWidth()
            			||Float.valueOf(fullnumH.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getHeight()) {
            		showNotification("已加工工件不得大于待加工工件", NotificationBox.Type.WARNING);
            	}else {
            		hideNotification();
            		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
					.setWeight(newValue);
            	}
            }
        });
		
	}

	private void refresh() {
		fullnumL.setText("");
		fullnumW.setText("");
		fullnumH.setText("");
		fullnumWT.setText("");
	}

	@FXML
	public void calculateBtAction(ActionEvent event) {
		if(Float.valueOf(fullnumL.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getLength()
    			||Float.valueOf(fullnumW.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWidth()
    			||Float.valueOf(fullnumH.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getHeight()
    	) {
    		showNotification("已加工工件不得大于待加工工件", NotificationBox.Type.WARNING);
    	}else {
    		hideNotification();    		
    	}
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece().calculateWeight();
		fullnumWT.setText(String.valueOf(DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece().getWeight()));
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece().setWeight(Float.parseFloat(fullnumWT.getText()));
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
			.setWeight(Float.parseFloat(fullnumWT.getText()));
	}

	@FXML
	public void LResetBtAction(ActionEvent event) {
		if(Float.valueOf(fullnumW.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWidth()
    			||Float.valueOf(fullnumH.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getHeight()
    			||Float.valueOf(fullnumWT.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWeight()
    	) {
    		showNotification("已加工工件不得大于待加工工件", NotificationBox.Type.WARNING);
    	}else {
    		hideNotification();    		
    	}
		fullnumL.setText(String.valueOf(DBHandler.getInstance().getOProgram().getUnloadCNC().getWorkPiece().getLength()));
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
		.setLength(DBHandler.getInstance().getOProgram().getUnloadCNC().getWorkPiece().getLength());
	}
		
	@FXML
	public void wResetBtAction(ActionEvent event) {
		if(Float.valueOf(fullnumL.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getLength()
    			||Float.valueOf(fullnumH.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getHeight()
    			||Float.valueOf(fullnumWT.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWeight()
    	) {
    		showNotification("已加工工件不得大于待加工工件", NotificationBox.Type.WARNING);
    	}else {
    		hideNotification();    		
    	}
		fullnumW.setText(String.valueOf(DBHandler.getInstance().getOProgram().getUnloadCNC().getWorkPiece().getWidth()));
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
		.setWidth(DBHandler.getInstance().getOProgram().getUnloadCNC().getWorkPiece().getWidth());
	}
	
	@FXML
	public void hResetBtAction(ActionEvent event) {
		if(Float.valueOf(fullnumL.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getLength()
    			||Float.valueOf(fullnumW.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWidth()
    			||Float.valueOf(fullnumWT.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWeight()
    	) {
    		showNotification("已加工工件不得大于待加工工件", NotificationBox.Type.WARNING);
    	}else {
    		hideNotification();    		
    	}
		fullnumH.setText(String.valueOf(DBHandler.getInstance().getOProgram().getUnloadCNC().getWorkPiece().getHeight()));
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
		.setHeight(DBHandler.getInstance().getOProgram().getUnloadCNC().getWorkPiece().getHeight());
	}
	
	@FXML
	public void resetBtAction(ActionEvent event) {
		if(Float.valueOf(fullnumL.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getLength()
    			||Float.valueOf(fullnumW.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getWidth()
    			||Float.valueOf(fullnumH.getText()) > DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadstacker().getWorkPiece().getHeight()
    	) {
    		showNotification("已加工工件不得大于待加工工件", NotificationBox.Type.WARNING);
    	}else {
    		hideNotification();    		
    	}
		fullnumWT.setText(String.valueOf(DBHandler.getInstance().getOProgram().getUnloadCNC().getWorkPiece().getWeight()));
		DBHandler.getInstance().getProgramBuffer().get(programName).getUnloadCNC().getWorkPiece()
		.setWeight(DBHandler.getInstance().getOProgram().getUnloadCNC().getWorkPiece().getWeight());
	}
	
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fullnumL.setFocusListener(listener);
		fullnumW.setFocusListener(listener);
		fullnumH.setFocusListener(listener);
		fullnumWT.setFocusListener(listener);
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}

}
