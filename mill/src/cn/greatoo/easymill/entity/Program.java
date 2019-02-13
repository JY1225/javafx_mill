package cn.greatoo.easymill.entity;

import java.sql.Timestamp;
import java.util.List;
public class Program {			

	private int id;	
	private String name;
	private Timestamp timeCreate;
	private Timestamp timeLastOpen;
	private cn.greatoo.easymill.entity.Step unloadstacker;
	private cn.greatoo.easymill.entity.Step loadCNC;
	private cn.greatoo.easymill.entity.Step unloadCNC;
	private cn.greatoo.easymill.entity.Step loadstacker;
	private List<Step> Steps;
	private RobotSetting robotSetting;
	private boolean isHasTeach;
	
	private float orientation;
	private int layers;
	private int amount;
	private float studHeight_Workpiece;
	
	public Program(String name,cn.greatoo.easymill.entity.Step unloadstacker,cn.greatoo.easymill.entity.Step loadCNC,
			cn.greatoo.easymill.entity.Step unloadCNC,cn.greatoo.easymill.entity.Step loadstacker,
			Timestamp timeCreate, Timestamp timeLastOpen,RobotSetting robotSetting,boolean isHasTeach,
			float orientation, int layers,int amount,float studHeight_Workpiece) {
		this.name = name;
		this.unloadstacker = unloadstacker;
		this.loadCNC = loadCNC;
		this.unloadCNC = unloadCNC;
		this.loadstacker = loadstacker;
		this.timeCreate = timeCreate;
		this.timeLastOpen = timeLastOpen;
		this.robotSetting = robotSetting;
		this.isHasTeach = isHasTeach;
		this.orientation = orientation;
		this.layers = layers;
		this.amount = amount;
		this.studHeight_Workpiece = studHeight_Workpiece;
	}	

	public Program() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public cn.greatoo.easymill.entity.Step getUnloadstacker() {
		return unloadstacker;
	}

	public void setUnloadstacker(cn.greatoo.easymill.entity.Step unloadstacker) {
		this.unloadstacker = unloadstacker;
	}

	public cn.greatoo.easymill.entity.Step getLoadCNC() {
		return loadCNC;
	}

	public void setLoadCNC(cn.greatoo.easymill.entity.Step loadCNC) {
		this.loadCNC = loadCNC;
	}

	public cn.greatoo.easymill.entity.Step getUnloadCNC() {
		return unloadCNC;
	}

	public void setUnloadCNC(cn.greatoo.easymill.entity.Step unloadCNC) {
		this.unloadCNC = unloadCNC;
	}

	public cn.greatoo.easymill.entity.Step getLoadstacker() {
		return loadstacker;
	}

	public void setLoadstacker(cn.greatoo.easymill.entity.Step loadstacker) {
		this.loadstacker = loadstacker;
	}

	public Timestamp getTimeCreate() {
		return timeCreate;
	}

	public void setTimeCreate(Timestamp timeCreate) {
		this.timeCreate = timeCreate;
	}

	public Timestamp getTimeLastOpen() {
		return timeLastOpen;
	}

	public void setTimeLastOpen(Timestamp timeLastOpen) {
		this.timeLastOpen = timeLastOpen;
	}
	
	public List<Step> getProgramSteps() {
		return Steps;
	}
	public RobotSetting getRobotSetting() {
		return robotSetting;
	}

	public void setRobotSetting(RobotSetting robotSetting) {
		this.robotSetting = robotSetting;
	}

	public boolean isHasTeach() {
		return isHasTeach;
	}

	public void setHasTeach(boolean isHasTeach) {
		this.isHasTeach = isHasTeach;
	}

	public float getOrientation() {
		return orientation;
	}


	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}


	public int getLayers() {
		return layers;
	}


	public void setLayers(int layers) {
		this.layers = layers;
	}


	public int getAmount() {
		return amount;
	}


	public void setAmount(int amount) {
		this.amount = amount;
	}


	public float getStudHeight_Workpiece() {
		return studHeight_Workpiece;
	}


	public void setStudHeight_Workpiece(float studHeight_Workpiece) {
		this.studHeight_Workpiece = studHeight_Workpiece;
	}
}
