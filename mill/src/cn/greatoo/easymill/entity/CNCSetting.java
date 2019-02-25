package cn.greatoo.easymill.entity;

public class CNCSetting {
	private int id;
	private ClampingType clampingType;
		
	public enum ClampingType {
		LENGTH(1), WIDTH(2);
		
		private int id;
		
		private ClampingType(int id) {
			this.id = id;
		}
		
		public int getId() {
			return this.id;
		}
		
		public int getIdTypeId() {
			return this.id;
		}
		public static ClampingType getTypeById(int id) throws IllegalStateException {
			for (ClampingType clampingType: ClampingType.values()) {
				if (clampingType.getId() == id) {
					return clampingType;
				}
			}
			throw new IllegalStateException("Unknown workpiece type: [" + id + "].");
		}

	}

	public CNCSetting(ClampingType clampingType) {
		this.clampingType = clampingType;
	}
	
	public CNCSetting() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public ClampingType getClampingType() {
		return clampingType;
	}

	public void setClampingType(ClampingType clampingType) {
		this.clampingType = clampingType;
	}
	
}
