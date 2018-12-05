package cn.greatoo.easymill.ui.general.dialog;

import com.sun.java.accessibility.util.Translator;

import cn.greatoo.easymill.util.UIConstants;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ConfirmationDialogView extends AbstractDialogView<ConfirmationDialogPresenter> {

	private VBox vboxContents;
	private Label lblMessage;
	private HBox hboxButtons;
	private Button btnCancel;
	private Button btnOk;
	
	private String message;
	
	private static final String OK = "ConfirmationDialogView.ok";
	private static final String CANCEL = "ConfirmationDialogView.cancel";
	private static final String CSS_BUTTON_LEFT = "left";
	private static final String CSS_BUTTON_RIGHT = "right";
	private static final String CSS_BUTTON = "dialog-btn";
	private static final String CSS_MESSAGE = "dialog-msg";
	
	private static final double WIDTH = 420;
	private static final double HEIGHT = 200;
	
	public ConfirmationDialogView(String title, String message) {
		super(title, HEIGHT);
		setId("confirmationDialog");
		this.message = message;
		lblMessage.setText(message);
	}

	@Override
	protected Node getContents(double height) {
		vboxContents = new VBox();
		vboxContents.setPrefWidth(WIDTH);
		StackPane spMessage = new StackPane();
		lblMessage = new Label();
		lblMessage.setText(message);
		lblMessage.setAlignment(Pos.CENTER);
		lblMessage.setPrefWidth(WIDTH);
		lblMessage.setMaxWidth(WIDTH);
		lblMessage.setMinWidth(WIDTH);
		lblMessage.setPrefHeight(Double.MAX_VALUE);
		lblMessage.getStyleClass().add(CSS_MESSAGE);
		lblMessage.setWrapText(true);
		spMessage.getChildren().add(lblMessage);
		spMessage.setAlignment(Pos.CENTER);
		spMessage.setPrefWidth(WIDTH);
		spMessage.setPrefHeight(height - UIConstants.BUTTON_HEIGHT - TITLE_HEIGHT);
		hboxButtons = new HBox();
		btnCancel = new Button();
		btnCancel.setId("btnCancel");
		btnCancel.setGraphic(new Text("取消"));
		btnCancel.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_LEFT);
		btnCancel.setPrefHeight(UIConstants.BUTTON_HEIGHT);
		btnCancel.setPrefWidth(WIDTH/2 + 1);
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				getPresenter().setResult(false);
			}
		});
		btnOk = new Button();
		btnOk.setId("btnOk");
		btnOk.setGraphic(new Text("确认"));
		btnOk.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_RIGHT);
		btnOk.setPrefHeight(UIConstants.BUTTON_HEIGHT);
		btnOk.setPrefWidth(WIDTH/2);
		btnOk.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				getPresenter().setResult(true);
			}
		});
		hboxButtons.setSpacing(-1);
		hboxButtons.getChildren().addAll(btnCancel, btnOk);
		hboxButtons.setPrefSize(WIDTH, UIConstants.BUTTON_HEIGHT);
		HBox.setHgrow(btnCancel, Priority.ALWAYS);
		HBox.setHgrow(btnOk, Priority.ALWAYS);
		vboxContents.getChildren().addAll(spMessage, hboxButtons);
		VBox.setVgrow(spMessage, Priority.ALWAYS);
		setDialogSize(WIDTH, height);
		return vboxContents;
	}

}
