package cn.greatoo.easymill.entity;

import java.io.Serializable;

public class Gripper{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6313896030333391085L;

	public enum Type {
		TWOPOINT, VACUUM
	}
	
	private int id;
	private Type type;
	private String name;
	private float height;
	private boolean fixedHeight;	
	private String imageUrl;

	public Gripper(final String name, final Type type, final float height, final String imageUrl) {
		this.name = name;
		this.type = type;
		this.height = height;
		this.imageUrl = imageUrl;
		this.fixedHeight = false;

	}
	
	public Gripper() {
		
	}
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(final float height) {
		this.height = height;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(final String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isFixedHeight() {
		return fixedHeight;
	}

	public void setFixedHeight(final boolean fixedHeight) {
		this.fixedHeight = fixedHeight;
	}

}
