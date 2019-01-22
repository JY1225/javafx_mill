package cn.greatoo.easymill.entity;

import org.apache.logging.log4j.CloseableThreadContext.Instance;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.cnc.MCodeAdapter;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;

public class UserFrame {

	private int id; 
	private String name;
	private int number;
	private float zSafeDistance;
	private Coordinates location;
	public static UserFrame Instace = null;
	
	public UserFrame( final String name,final int number, final float zSafeDistance, final Coordinates location) {
		this.number = number;
		this.name = name;
		this.zSafeDistance = zSafeDistance;
		this.location = location;
	}

	public UserFrame() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(final int number) {
		this.number = number;
	}

	public float getzSafeDistance() {
		return zSafeDistance;
	}

	public void setzSafeDistance(final float zSafeDistance) {
		this.zSafeDistance = zSafeDistance;
	}

	public Coordinates getLocation() {
		return location;
	}

	public void setLocation(final Coordinates location) {
		this.location = location;
	}
	
	public static UserFrame getInstance(final String name, final int number, final float zSafeDistance, final Coordinates location) {
		if (Instace == null && location != null) {
			Instace = new UserFrame(name, number, zSafeDistance, location);
		}
		return Instace;
	}

}
