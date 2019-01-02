package cn.greatoo.easymill.ui.set;

import java.sql.SQLException;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.process.ProcessFlow;
import cn.greatoo.easymill.util.FullTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import cn.greatoo.easymill.process.DuplicateProcessFlowNameException;
import cn.greatoo.easymill.robot.AbstractRobotActionSettings;
public class SaveViewController {
	@FXML
	private GridPane saveGridPane;
	@FXML
	private Button saveBt;
	@FXML
	private TextField fulltxtName;
	private ProcessFlow processFlow;

	public void init() {		
	fulltxtName.focusedProperty().addListener(new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
                 // TODO Auto-generated method stub
        	if(!arg2){
        		System.out.println("fulltxtName = "+fulltxtName.getText());	
        		
        	}
        }
	});
	}
	// Event Listener on Button[#saveBt].onMouseClicked
	@FXML
	public void save(MouseEvent event) throws IllegalArgumentException, DuplicateProcessFlowNameException {		
		try{				
			processFlow = new ProcessFlow("",null,null);
			AbstractRobotActionSettings robotActionSettings= new AbstractRobotActionSettings(null,null,null,null,null,true);
			
			processFlow.setName(fulltxtName.getText());
			DBHandler.getInstance().save(processFlow);
			DBHandler.getInstance().saveRobotActionSettings(robotActionSettings);

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}		
	// Event Listener on FullTextField[#fulltxtName].onMouseClicked
	@FXML
	public void nameChanged(MouseEvent event) {
		 String Processflowname =fulltxtName.getText();
		processFlow.setName(fulltxtName.getText());
	}
	
	
}
