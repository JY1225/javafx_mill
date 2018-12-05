package cn.greatoo.easymill.ui.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.greatoo.easymill.ui.general.dialog.AbstractDialogView;
import cn.greatoo.easymill.ui.general.dialog.ConfirmationDialogPresenter;
import cn.greatoo.easymill.ui.general.dialog.ConfirmationDialogView;
import cn.greatoo.easymill.util.ThreadManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public abstract class Controller {
	private static String arrowRightPath = "M 1.6875 0 L 0 1.65625 L 3.375 5 L 0.0625 8.3125 L 1.71875 10 L 6.65625 5.0625 L 6.5625 4.96875 L 6.625 4.90625 L 1.6875 0 z ";
	protected SVGPath imagePath;
	protected SVGPath svgPauseLeft;

	private static final int BUTTON_WIDTH = 209;
	private static final int BUTTON_HEIGHT = 43;

	private static final int ICON_WIDTH = 20;
	private static final int ICON_MARGIN = 6;
	private static final int ICON_ARROW_WIDTH = 10;
	
	private static final String CSS_CLASS_MENU_ICON = "menu-icon";
	private static final String CSS_CLASS_LEFT_MENU_ITEM_LABEL = "left-menu-item-label";
	private static final String CSS_CLASS_LEFT_MENU_ITEM_PANEL = "left-menu-item-panel";
	private static final String CSS_CLASS_LEFT_MENU_BUTTON = "left-menu-button";
	static final String NEW_ICON = "M 2.5 0 L 2.5 20 L 17.5 20 L 17.5 6.25 L 11.25 0 L 2.5 0 z M 5 2.5 L 10 2.5 L 10 7.5 L 15 7.5 L 15 17.5 L 5 17.5 L 5 2.5 z";
	
	private static final String CSS_CLASS_BTN_SELECTED = "selected";
	// 按钮被选中，背景变颜色
		public void isClicked(List<Button> bts,Button button) {
			for (int i = 0; i < bts.size(); i++) {
				if (bts.get(i) == button ) {
					if(!button.getStyleClass().contains(CSS_CLASS_BTN_SELECTED)) {
						button.getStyleClass().add(CSS_CLASS_BTN_SELECTED);
					}
				} else {
					if (bts.get(i).getStyleClass().contains(CSS_CLASS_BTN_SELECTED)) {
						bts.get(i).getStyleClass().remove(CSS_CLASS_BTN_SELECTED);
					}
				}
			}
		}
		

		//private ProcessFlow activeProcessFlow;
		
		public void newProcess(StackPane stackPane) {		
			if(false/*activeProcessFlow.hasChangesSinceLastSave()*/) {
				ThreadManager.submit(new Thread() {
					@Override
					public void run() {
						if (askConfirmation(stackPane,"未保存数据", "当前程序包含未保存数据，如果继续，未保存的数据将会丢失。")) {
							createNewProcess();
						}
					}
				});
			} else {
				ThreadManager.submit(new Thread() {
					@Override
					public void run() {
						if (askConfirmation(stackPane,"新程序", "是否要创建新程序")) {
							createNewProcess();
						}
					}
				});
			}
		}
		
		public void createNewProcess() {
			Platform.runLater(new Thread() {
				@Override
				public void run() {
//					activeProcessFlow.loadFromOtherProcessFlow(processFlowManager.createNewProcessFlow());
//					refreshParent();
				}
			});
		}
//		
//		public void setDisVisible(Node node) {
//			if(node != null) {
//				node.setVisible(false);
//			}
//		}
		
		@SuppressWarnings("static-access")
		public void setDisVisible (final int row, GridPane gridPane,Node parent) {
		    ObservableList<Node> childrens = gridPane.getChildren();
		    
		    for (Node node : childrens) {
		    	try {
		    	int r = gridPane.getRowIndex(node);				
		        if(r == row ) {
		        	if(node != parent) {
						node.setVisible(false);
					}else
						node.setVisible(true);
		        }		   
		    }catch (Exception e) {
		    	
		    }
		    }
		}
		
		@SuppressWarnings("static-access")
		public void setDisVisible (final int row, final int column, GridPane gridPane,Node parent) {
		    ObservableList<Node> childrens = gridPane.getChildren();
		    
		    for (Node node : childrens) {
		    	try {
		    	int r = gridPane.getRowIndex(node);
				int c = gridPane.getColumnIndex(node);
		        if(r == row && c == column) {
		        	if(node != parent) {
						node.setVisible(false);
					}else
						node.setVisible(true);
		        }		   
		    }catch (Exception e) {
		    	
		    }
		    }
		}
		
		public boolean askConfirmation(StackPane stackPane,final String title, final String message) {
			final ConfirmationDialogView view = new ConfirmationDialogView(title, message);
			ConfirmationDialogPresenter confirmationDialogPresenter = new ConfirmationDialogPresenter(view);
			Platform.runLater(new Thread() {
				@Override
				public void run() {
					showDialog(stackPane,view);
				}
			});
			boolean returnValue = false;
			try {
				returnValue = confirmationDialogPresenter.getResult();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			Platform.runLater(new Thread() {
				@Override
				public void run() {
					hideDialog(stackPane);
				}
			});
			return returnValue;
		}
		
		public void showDialog(StackPane stackPane,final AbstractDialogView<?> dialog) {
			stackPane.getChildren().add(dialog);
			
		}
		
		public void hideDialog(StackPane stackPane) {
			List<Node> toRemove = new ArrayList<Node>();
			for (Node node : stackPane.getChildren()) {
				if (node instanceof AbstractDialogView<?>) {
					toRemove.add(node);
				}
			}
			stackPane.getChildren().removeAll(toRemove);
		}
		protected void addMenuItem(VBox prosessVBox,Button button, final int index, final String iconPath, final String text,
				final boolean isRightNav, final EventHandler<ActionEvent> clickedEventHandler) {
			prosessVBox.setPadding(new Insets(1, 1, 0, 0));
			HBox hbox = new HBox();
			StackPane iconPane = new StackPane();
			SVGPath icon = new SVGPath();
			icon.setContent(iconPath);
			icon.getStyleClass().add(CSS_CLASS_MENU_ICON);
			hbox.setAlignment(Pos.CENTER);
			iconPane.getChildren().add(icon);
			iconPane.setPrefSize(ICON_WIDTH + 2 * ICON_MARGIN, BUTTON_HEIGHT);
			hbox.getChildren().add(iconPane);
			Label label = new Label(text);
			label.getStyleClass().add(CSS_CLASS_LEFT_MENU_ITEM_LABEL);
			label.setPrefSize(BUTTON_WIDTH - 2 * ICON_WIDTH - 4 * ICON_MARGIN, BUTTON_HEIGHT);
			// Added for test purposes
			label.setLabelFor(button);
			hbox.getChildren().add(label);
			HBox.setHgrow(label, Priority.ALWAYS);
			StackPane arrowPane = new StackPane();
			arrowPane.setPrefSize(ICON_ARROW_WIDTH + 2 * ICON_MARGIN, BUTTON_HEIGHT);
			if (isRightNav) {
				SVGPath rightArrow = new SVGPath();
				rightArrow.setContent(arrowRightPath);
				arrowPane.getChildren().add(rightArrow);
				rightArrow.getStyleClass().add(CSS_CLASS_MENU_ICON);
			}
			hbox.getChildren().add(arrowPane);
			hbox.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
			hbox.getStyleClass().add(CSS_CLASS_LEFT_MENU_ITEM_PANEL);
			button.setGraphic(hbox);
			button.setOnAction(clickedEventHandler);
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BUTTON);
		}
}