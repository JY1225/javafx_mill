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
	
	public Program(String name,cn.greatoo.easymill.entity.Step unloadstacker,cn.greatoo.easymill.entity.Step loadCNC,
			cn.greatoo.easymill.entity.Step unloadCNC,cn.greatoo.easymill.entity.Step loadstacker, Timestamp timeCreate, Timestamp timeLastOpen) {
		this.name = name;
		this.unloadstacker = unloadstacker;
		this.loadCNC = loadCNC;
		this.unloadCNC = unloadCNC;
		this.loadstacker = loadstacker;
		this.timeCreate = timeCreate;
		this.timeLastOpen = timeLastOpen;
	}
	public enum Step {
		UNLOADSTACKER, LOADCNC, UNLOADCNC, LOADSTACKER;
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
	
}
