package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.GenericMCode;
import cn.greatoo.easymill.cnc.MCodeAdapter;
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
	
	public void init(CNCMachine cnc) {
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
		if(cnc != null) {
			refresh(cnc.getMCodeAdapter());
		}
		
	}
	
	
	//初始化---------------------------
	public void refresh(final MCodeAdapter mCodeAdapter) {
		if (mCodeAdapter != null) {
			buildMCodeNodes(mCodeAdapter);
			rs1TextFied.setText(mCodeAdapter.getRobotServiceInputNames().get(0));
			rs2TextFied.setText(mCodeAdapter.getRobotServiceInputNames().get(1));
			rs3TextFied.setText(mCodeAdapter.getRobotServiceInputNames().get(2));
			rs4TextFied.setText(mCodeAdapter.getRobotServiceInputNames().get(3));
			rs5TextFied.setText(mCodeAdapter.getRobotServiceInputNames().get(4));
			rs6TextFied.setText(mCodeAdapter.getRobotServiceOutputNames().get(0));
		} else {
			rs1TextFied.setText("RS1");
			rs2TextFied.setText("RS2");
			rs3TextFied.setText("RS3");
			rs4TextFied.setText("RS4");
			rs5TextFied.setText("RS5");
			rs6TextFied.setText("RSA");
		}
		for (MCodeNode mcodeNode: gmcNodeList) {
			mcodeNode.refresh(mCodeAdapter);
		}
	}
	
	static void buildMCodeNodes(final MCodeAdapter mCodeAdapter) {
		if (mCodeAdapter != null) {
			for (GenericMCode mCode: mCodeAdapter.getGenericMCodes()) {
				gmcNodeList.add(new MCodeNode(mCode.getIndex() + 1));
			}
		}
		addNodesToScrollPane();
	}
	//end-------------------------------------------------
	
	public static List<String> getMCodeNames() {
		List<String> mCodeNames = new ArrayList<String>();
		for (MCodeNode mcodeNode: gmcNodeList) {
			mCodeNames.add(mcodeNode.getName());
		}
		return mCodeNames;
	}
	
	public static List<Set<Integer>> getMCodeRobotServiceInputs() {
		List<Set<Integer>> robotServiceInputs = new ArrayList<Set<Integer>>();
		for (MCodeNode mcodeNode: gmcNodeList) {
			robotServiceInputs.add(mcodeNode.getMCodeRobotServiceInputs());
		}
		return robotServiceInputs;
	}
	
	public static List<Set<Integer>> getMCodeRobotServiceOutputs() {
		List<Set<Integer>> robotServiceOutputs = new ArrayList<Set<Integer>>();
		for (MCodeNode mcodeNode: gmcNodeList) {
			robotServiceOutputs.add(mcodeNode.getMCodeRobotServiceOutputs());
		}
		return robotServiceOutputs;
	}
	
	public  List<String> getRobotServiceInputNames() {
		List<String> robotServiceInputNames = new ArrayList<String>();
		robotServiceInputNames.add(rs1TextFied.getText());
		robotServiceInputNames.add(rs2TextFied.getText());
		robotServiceInputNames.add(rs3TextFied.getText());
		robotServiceInputNames.add(rs4TextFied.getText());
		robotServiceInputNames.add(rs5TextFied.getText());
		return robotServiceInputNames;
	}
	
	public  List<String> getRobotServiceOutputNames() {
		List<String> robotServiceOutputNames = new ArrayList<String>();
		robotServiceOutputNames.add(rs6TextFied.getText());
		return robotServiceOutputNames;
	}
	
	//------------------------------------------
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
