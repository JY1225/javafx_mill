package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.util.UIConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class MCodeConfigViewController {
	@FXML
	private GridPane mCodeGridPane;
	@FXML
	private Button addBt;
	@FXML
	private Button deleBt;
	@FXML
	private TextField rs1TextFied;
	@FXML
	private TextField rs2TextFied;
	@FXML
	private TextField rs3TextFied;
	@FXML
	private TextField rs4TextFied;
	@FXML
	private TextField rs5TextFied;
	@FXML
	private TextField rs6TextFied;
	@FXML
	private ScrollPane spDetails;
	private static GridPane spGrid;
	private static List<MCodeNode> gmcNodeList;
	public void init() {
		gmcNodeList = new ArrayList<MCodeNode>();
		spGrid = new GridPane();
		spDetails.setContent(spGrid);
		spGrid.setAlignment(Pos.TOP_CENTER);
		spGrid.setVgap(10);
		spGrid.setHgap(10);
		spDetails.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spDetails.setHbarPolicy(ScrollBarPolicy.NEVER);
		spDetails.setPannable(false);
		spDetails.setFitToHeight(false);
		spDetails.setFitToWidth(false);
		spDetails.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT*9);
		spDetails.setStyle("-fx-background:transparent;");//透明
		addNodesToScrollPane();
	}
	@FXML
	public void addBtAction(ActionEvent event) {
		MCodeNode newMCode = new MCodeNode(gmcNodeList.size() + 1);
		gmcNodeList.add(newMCode);
		addNodesToScrollPane();

	}
	
	@FXML
	public void deleBtAction(ActionEvent event) {		
		gmcNodeList.remove(gmcNodeList.size() - 1);
		addNodesToScrollPane();
	}
	private static void addNodesToScrollPane() {
		//Reset content
		spGrid.getChildren().removeAll(spGrid.getChildren());
		int column = 0; int row = 0;
		for (MCodeNode mcodeNode: gmcNodeList) {
			spGrid.add(mcodeNode, column, row++, MCodeNode.NB_COLUMNS, 2);
			row++;
		}
	}
}
