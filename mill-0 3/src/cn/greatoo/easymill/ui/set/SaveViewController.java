package cn.greatoo.easymill.ui.set;

import java.sql.SQLException;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.process.DuplicateProcessFlowNameException;
import cn.greatoo.easymill.process.ProcessFlow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
public class SaveViewController {
	@FXML
	private GridPane saveGridPane;
	@FXML
	private Button saveBt;
	@FXML
	private TextField fulltxtName;
	private ProcessFlow processFlow;

	public void init() {		
		
	}
	// Event Listener on Button[#saveBt].onMouseClicked
	@FXML
	public void save(MouseEvent event) throws IllegalArgumentException, DuplicateProcessFlowNameException, SQLException {		
		processFlow = new ProcessFlow("",null,null);
		
		processFlow.setName(fulltxtName.getText());
		DBHandler.getInstance().save(processFlow);
	}		
	// Event Listener on FullTextField[#fulltxtName].onMouseClicked
	@FXML
	public void nameChanged(MouseEvent event) {
		
	}
	
	
}
