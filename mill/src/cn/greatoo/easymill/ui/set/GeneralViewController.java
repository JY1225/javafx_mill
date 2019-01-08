package cn.greatoo.easymill.ui.set;

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
	private String name;

	public void init() {
	
		singleCycle.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
			//	System.out.println("222222222222222222");
				
				
			}
		});
		
//		fulltxtName.focusedProperty().addListener(new ChangeListener<Boolean>() {
//	        @Override
//	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
//	                 // TODO Auto-generated method stub 
//	        	if(!arg2){
//	        		System.out.println("光标点击离开文本框，获取文本框的当前值，并把值写入数据库 ");
//	        		System.out.println("fulltxtName = "+fulltxtName.getText());	
//	        		
//	        	}
//	        }
//		});
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

}
