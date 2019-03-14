package cn.greatoo.easymill.ui.set;

import cn.greatoo.easymill.db.util.DBHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
/**
 * 通用界面Controller
 *
 */
public class GeneralViewController {
	@FXML
	private GridPane generalGridPane;
	@FXML
	private TextField fulltxtName;
	@FXML
	private Button addProcessBt;
	@FXML
	private Button removeProcessBt;
	@FXML
	private CheckBox singleCycle;
	public static String name;

	public void init() {
		fulltxtName.setText(DBHandler.getInstance().getProgramName());
		singleCycle.selectedProperty().setValue(DBHandler.getInstance().getProgramBuffer().get(DBHandler.getInstance().getProgramName()).isSingleCycle());
		singleCycle.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				DBHandler.getInstance().getProgramBuffer().get(DBHandler.getInstance().getProgramName()).setSingleCycle(newValue);			
			}
		});
		
	}
	@FXML
	public void nameChanged(MouseEvent event) {
		fulltxtName.getText();
		fulltxtName.setPromptText("请选择一个流程名称");	
	}
	
	@FXML
	public void addPrpcess(MouseEvent event) {
		
	}
	
	@FXML
	public void removeProcess(MouseEvent event) {
	
	}

	@FXML
	public void isSingleCycle(ActionEvent event) {
		singleCycle.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
				//getPresenter().setSingleCycle(newValue);
				
			}
		});
	}

	@FXML
	public void isCelect(MouseEvent event) {
		
	}
	public TextField getFulltxtName() {
		return fulltxtName;
	}
	public void setFulltxtName(TextField fulltxtName) {
		this.fulltxtName = fulltxtName;
	}
}
