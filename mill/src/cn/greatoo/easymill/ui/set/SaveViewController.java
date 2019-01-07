package cn.greatoo.easymill.ui.set;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.process.DuplicateProcessFlowNameException;
import cn.greatoo.easymill.process.ProcessFlow;
import cn.greatoo.easymill.process.ProcessFlowManager;
import cn.greatoo.easymill.ui.general.AbstractFormView;
import cn.greatoo.easymill.ui.general.NotificationBox;
import cn.greatoo.easymill.ui.general.NotificationBox.Type;
import cn.greatoo.easymill.util.Translator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import cn.greatoo.easymill.ui.general.AbstractFormPresenter;
public class SaveViewController {
	@FXML
	private GridPane saveGridPane;
	@FXML
	private Button saveBt;
	@FXML
	private TextField fulltxtName;
	
	public ProcessFlow processFlow;
	private static ProcessFlowManager processFlowManager;
	private AbstractFormView abstractFormView;
	private GeneralViewController generalViewController;
	private SaveViewController saveViewController;
	private static Logger logger = LogManager.getLogger(SaveViewController.class.getName());
	private static NotificationBox notificationBox;
	private static final String DUPLICATE_NAME = "ProcessSavePresenter.duplicateName";
	private static final String SAVE_SUCCESSFULL = "ProcessSavePresenter.saveOK";
	private static final String UPDATE_SUCCESSFULL = "ProcessSavePresenter.updateOK";
	public void init() {	
//		fulltxtName.focusedProperty().addListener(new ChangeListener<Boolean>() {
//        @Override
//        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
//                 // TODO Auto-generated method stub
//        }
//	});		
		processFlow= GeneralViewController.getprocessFlow();
		setProcessFlow(processFlow);
		fulltxtName.setText(processFlow.getName());	
	}
	// Event Listener on Button[#saveBt].onMouseClicked
	@FXML
	public void save(MouseEvent event) throws DuplicateProcessFlowNameException  {		
     	 save(fulltxtName.getText());
	}	
	
	public void save(String saveProcessName) {
		try {
			//in this case a new name is given, so we need to check whether we can create a copy or not
			//when a process already exists with this name, we will create a warning and will not save
			
			//processFlow = new ProcessFlow("",null,null);
			//AbstractRobotActionSettings robotActionSettings= new AbstractRobotActionSettings(null,null,null,null,null,true);
			int saveProcessID = (DBHandler.getInstance().getProcessFlowIdForName(saveProcessName));
			if(saveProcessID != -1) {
				saveAsExisting();
			} else {
				saveAsNewProcess();
			}
			
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	
	private void saveAsExisting() {
		try {
			//processFlowManager =new ProcessFlowManager();
			saveViewController = new SaveViewController();
			ProcessFlowManager.updateProcessFlow(processFlow);
			//reset the flag to indicate the latest changes are saved
			//processFlow.setChangesSinceLastSave(false);
			showNotification("   Changes successfully saved", Type.OK);
		} catch (DuplicateProcessFlowNameException e) {
			//We will come here when a user tries to save an existing process which is not the activeProcess
			
			//showNotification(Translator.getTranslation(DUPLICATE_NAME), Type.WARNING);
			hideNotification();
		}
	}
    public void saveAsNewProcess() {   	
    	try {
    		ProcessFlowManager.saveProcessFlow(processFlow);   		
			//getMenuPresenter().getParent().getProcessFlowPresenter().getView().refreshProcessFlowName();
    		//abstractFormView.showNotification(Translator.getTranslation(SAVE_SUCCESSFULL), Type.OK);
		} catch (DuplicateProcessFlowNameException e) {
			//this should never happen
			//abstractFormView.showNotification(Translator.getTranslation(DUPLICATE_NAME), Type.WARNING);
		}
	}
	
	// Event Listener on FullTextField[#fulltxtName].onMouseClicked
	@FXML
	public void nameChanged(MouseEvent event) {
		 //String saveProcessName =fulltxtName.getText();
		//processFlow.setName(saveProcessName);		
		//processFlow=processFlowManager.getProcessFlow();
	}
	public void setProcessFlow(ProcessFlow processflow) {
		this.processFlow = processflow;
	}
	
	public static void showNotification(final String notification, NotificationBox.Type type) {
		notificationBox = SetMenuViewController.getnotificationBox();
		notificationBox.showNotification(notification, type);
		notificationBox.setVisible(true);
		notificationBox.setManaged(true);
	}
	
	public void hideNotification() {
		notificationBox = SetMenuViewController.getnotificationBox();
		notificationBox.setVisible(false);
		notificationBox.setManaged(false);
	}
}
