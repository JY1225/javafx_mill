package cn.greatoo.easymill.entity;

public class WorkPiece {

	public enum Type {
		RAW(1), HALF_FINISHED(2), FINISHED(3);
		
		private int id;
		
		private Type(int id) {
			this.id = id;
		}
		
		public int getTypeId() {
			return this.id;
		}
		
		public static Type getTypeById(int id) throws IllegalStateException {
			for (Type workPieceType: Type.values()) {
				if (workPieceType.getTypeId() == id) {
					return workPieceType;
				}
			}
			throw new IllegalStateException("Unknown workpiece type: [" + id + "].");
		}
	}
	
	public enum Material {
		AL(1, 0.000002702f), CU(2, 0.00000896f), FE(3, 0.00000786f), OTHER(4, Float.NaN);

		private int id;
		private float density;

		private Material(int id, float density) {
			this.id = id;
			this.density = density;
		}

		public float getDensity() {
			return this.density;
		}

		public int getId() {
			return this.id;
		}

		public static Material getMaterialById(int id) throws IllegalStateException {
			for (Material workPieceMaterial: Material.values()) {
				if (workPieceMaterial.getId() == id) {
					return workPieceMaterial;
				}
			}
			throw new IllegalStateException("Unknown workpiece material: [" + id + "].");
		}
	}
	
	public enum WorkPieceShape {
		CUBIC(1), CYLINDRICAL(2);
		
		private int id;
		
		private WorkPieceShape(int id) {
			this.id = id;
		}
		
		public int getShapeId() {
			return this.id;
		}
		
		public static WorkPieceShape getShapeById(int id) throws IllegalStateException {
			for (WorkPieceShape workPieceShape: WorkPieceShape.values()) {
				if (workPieceShape.getShapeId() == id) {
					return workPieceShape;
				}
			}
			throw new IllegalStateException("Unknown workpiece shape: [" + id + "].");
		}
	}
	
	private int id;	
	private String processName;
	private Process.Step step;
	private Type type;
	private WorkPieceShape shape;
	private float height;
	private float length;
	private float width;
	private float diameter;
	private Material material;
	private float weight;

	
	public WorkPiece(String processName, Process.Step step, final Type type, float length, float width, float height, float diameter,
			final Material material, final float weight) {
		this.processName = processName;
		this.step = step;
		this.type = type;
		this.length = length;
		this.width = width;
		this.height = height;
		this.material = material;
		this.weight = weight;
		this.diameter = diameter;
		setShape();
	}
	
	public WorkPiece() {
	
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getProcessName() {
		return processName;
	}


	public void setProcessName(String processName) {
		this.processName = processName;
	}


	public Process.Step getStep() {
		return step;
	}


	public void setStep(Process.Step step) {
		this.step = step;
	}


	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}
	
	public WorkPieceShape getShape() {
		return shape;
	}
	
	private void setShape() {
		if (diameter == 0 || diameter < 0) {
			this.shape = WorkPieceShape.CYLINDRICAL;
		} else{
			this.shape = WorkPieceShape.CUBIC;			
		}
	}
	
	/**
	 * This method transforms the current workpiece to a given shape.
	 * 
	 * @param shape is the new shape of the workpiece
	 * @post new dimensions are added to the workpiece as well as a new graphical representation object
	 */
	public void transformPiece(WorkPieceShape shape) {
		if (this.shape == null || !this.shape.equals(shape)) {
			this.shape = shape;
		}
	}
	
	
	public Material getMaterial() {
		return material;
	}

	public void setMaterial(final Material material) {
		this.material = material;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(final float weight) {
		this.weight = weight;
	}

	public float getHeight() {
		return height;
	}


	public void setHeight(float height) {
		this.height = height;
	}


	public float getLength() {
		return length;
	}


	public void setLength(float length) {
		this.length = length;
	}


	public float getWidth() {
		return width;
	}


	public void setWidth(float width) {
		this.width = width;
	}


	public float getDiameter() {
		return diameter;
	}


	public void setDiameter(float diameter) {
		this.diameter = diameter;
	}


	public void setShape(WorkPieceShape shape) {
		this.shape = shape;
	}


	public void calculateWeight() {
		if (material.equals(Material.OTHER)) {
			throw new IllegalStateException("Can't calculate weight: unknown material type.");
		} else {
			if(diameter == 0 ) {
				setWeight((width*height*length) * material.getDensity());
			}else {
				setWeight(((diameter/2)*(diameter/2)* (float) Math.PI*height) * material.getDensity());
			}
			
		}
	}
	
}
