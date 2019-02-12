package cn.greatoo.easymill.ui.set;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.ui.main.Controller;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class OpenViewController extends Controller {
	@FXML
	private GridPane saveGridPane;
	@FXML
	private HBox totalHBox;
	@FXML
	private HBox hbox;
	@FXML
	private Button btnLoad;
	@FXML
	private TableView<Program> table;
	@FXML
	private TableColumn<Program, String> nameColumn;
	@FXML
	TableColumn<Program, String> lastOpenedColumn;
	@FXML
	TableColumn<Program, Program> deleteProcessColumn;
	@FXML
	private TextField fulltxtName;
	
	private ObservableList<Program> Programs;
	private Label newProsessLable;
	@FXML
	public void load(MouseEvent event) {
		fulltxtName.setText(table.getSelectionModel().selectedItemProperty().getValue().getName());
		DBHandler.getInstance().setProgramName(fulltxtName.getText());
		newProsessLable.setText(fulltxtName.getText());
	}

	public void init(Label newProsessLable) {
		this.newProsessLable = newProsessLable;
		Programs = FXCollections.observableArrayList();
		Programs.addAll(DBHandler.getInstance().getProgramBuffer().values());
		table.setEditable(false);
		table.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(final ObservableValue<? extends Number> arg0, final Number oldValue, final Number newValue) {
				if ((newValue != null) && (newValue.intValue() >= 0)) {
					btnLoad.setDisable(false);
				} else {
					btnLoad.setDisable(true);
				}
			}
			
		});
		btnLoad.setDisable(true);
		table.getSelectionModel().setCellSelectionEnabled(false);
		
		nameColumn.setCellValueFactory(new Callback<CellDataFeatures<Program, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(final CellDataFeatures<Program, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getName());
		     }
		  });
		
		lastOpenedColumn.setCellValueFactory(new Callback<CellDataFeatures<Program, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(final CellDataFeatures<Program, String> p) {
		    	 if (p.getValue().getTimeLastOpen() == null) {
		    		 return null;
		    	 } else {
		    		 Date date = new Date(p.getValue().getTimeLastOpen().getTime());
		    		 return new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd, HH:mm").format(date));
		    	 }
		     }
		  });
		deleteProcessColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Program,Program>, ObservableValue<Program>>() {

			@Override
			public ObservableValue<Program> call(CellDataFeatures<Program, Program> p) {
				return new ReadOnlyObjectWrapper<Program>(p.getValue());
			}
			
	    });
		deleteProcessColumn.setCellFactory(new Callback<TableColumn<Program,Program>, TableCell<Program,Program>>() {

			@Override
			public TableCell<Program, Program> call(TableColumn<Program, Program> arg0) {
				return new DeleteButton();
			}
				
		});

		nameColumn.prefWidthProperty().bind(table.widthProperty().divide(10).multiply(4));
		nameColumn.setResizable(false);

		lastOpenedColumn.prefWidthProperty().bind(table.widthProperty().divide(10).multiply(4));
		lastOpenedColumn.setResizable(false);

		deleteProcessColumn.prefWidthProperty().bind(table.widthProperty().divide(10).multiply(2));
		deleteProcessColumn.setResizable(false);
		setPrograms(Programs);
	}
	
	public void setPrograms(final ObservableList<Program> Programs) {
		table.setItems(Programs);
	}
	
	
private class DeleteButton extends TableCell<Program, Program> {
		
		static final String CSS_CLASS_DELETE_OPEN_BTN = "delete-open";
		final Button deleteButton;
		final String deleteIconPath = "M 3.6875 1.5 C 3.6321551 1.5180584 3.5743859 1.5512948 3.53125 1.59375 L 1.59375 3.53125 C 1.423759 3.7005604 1.4751389 4.0379792 1.6875 4.25 L 7.46875 10 L 1.6875 15.75 C 1.4751389 15.962191 1.423759 16.297908 1.59375 16.46875 L 3.53125 18.40625 C 3.7020918 18.577772 4.0386598 18.55526 4.25 18.34375 L 10 12.5625 L 15.75 18.34375 C 15.962021 18.55526 16.297908 18.577772 16.46875 18.40625 L 18.40625 16.46875 C 18.57556 16.297908 18.524691 15.962191 18.3125 15.75 L 12.53125 10 L 18.3125 4.25 C 18.524691 4.0379792 18.57556 3.7005604 18.40625 3.53125 L 16.46875 1.59375 C 16.297908 1.4244396 15.962021 1.4759897 15.75 1.6875 L 10 7.46875 L 4.25 1.6875 C 4.1439896 1.5809791 4.0004088 1.5136129 3.875 1.5 C 3.8124658 1.4931936 3.7428449 1.4819416 3.6875 1.5 z ";
		Program p;
		
		DeleteButton() {
			deleteButton = createButton(deleteIconPath, CSS_CLASS_DELETE_OPEN_BTN, "", 32, 0, new EventHandler<ActionEvent>() {
				 @Override public void handle(ActionEvent actionEvent) {
					 //getPresenter().deleteProcess(p.getId());
				 }
			});
		}
		
		@Override
		protected void updateItem(Program item, boolean empty) {
			super.updateItem(item, empty);
			if(!empty) {
				setGraphic(deleteButton);
				this.p = item;
			}
				
		}
	}

@Override
public void setMessege(String mess) {
	// TODO Auto-generated method stub
	
}
}
