package cn.greatoo.easymill.entity;

public class Program {			

	private int id;	
	private String name;
	private String timeCreate;
	private String timeLastOpen;
	private cn.greatoo.easymill.entity.Step unloadstacker;
	private cn.greatoo.easymill.entity.Step loadCNC;
	private cn.greatoo.easymill.entity.Step unloadCNC;
	private cn.greatoo.easymill.entity.Step loadstacker;
	
	public Program(String name,cn.greatoo.easymill.entity.Step unloadStacker2,cn.greatoo.easymill.entity.Step unloadStacker3,
			cn.greatoo.easymill.entity.Step unloadStacker4,cn.greatoo.easymill.entity.Step unloadStacker5, String timeCreate, String timeLastOpen) {
		this.name = name;
		this.unloadstacker = unloadStacker2;
		this.loadCNC = unloadStacker3;
		this.unloadCNC = unloadStacker4;
		this.loadstacker = unloadStacker5;
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

	public String getTimeCreate() {
		return timeCreate;
	}

	public void setTimeCreate(String timeCreate) {
		this.timeCreate = timeCreate;
	}

	public String getTimeLastOpen() {
		return timeLastOpen;
	}

	public void setTimeLastOpen(String timeLastOpen) {
		this.timeLastOpen = timeLastOpen;
	}
	
}
