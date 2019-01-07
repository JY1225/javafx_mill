package cn.greatoo.easymill.workpiece;

import cn.greatoo.easymill.ui.shape.IDrawableObject;
import cn.greatoo.easymill.ui.shape.RectanglePieceRepresentation;
import cn.greatoo.easymill.ui.shape.RoundPieceRepresentation;

public class WorkPiece {
	
	public WorkPiece(final WorkPiece wp) {
		this(wp.getType(), wp.getDimensions().clone(), wp.getMaterial(), wp.getWeight());
	}
	public enum Dimensions {
		LENGTH, WIDTH, HEIGHT, DIAMETER;
	}

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
	
	private Type type;
	private IWorkPieceDimensions dimensions;
	private WorkPieceShape shape;
	private Material material;
	private float weight;
	private IDrawableObject representation;
	
	public WorkPiece(final Type type, final IWorkPieceDimensions dimensions, final Material material, final float weight) {
		this.type = type;
		this.dimensions = dimensions;
		this.material = material;
		this.weight = weight;
		setShape();
	}
	
	public WorkPiece(final Type type, final WorkPieceShape shape, final Material material, final float weight) {
		this.type = type;
		transformPiece(shape);
		this.material = material;
		this.weight = weight;
		setShape();
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
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
		if (dimensions instanceof RoundDimensions) {
			this.shape = WorkPieceShape.CYLINDRICAL;
			this.representation = new RoundPieceRepresentation(this);
		} else if (dimensions instanceof RectangularDimensions) {
			this.shape = WorkPieceShape.CUBIC;
			this.representation = new RectanglePieceRepresentation(this);
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
			if (shape.equals(WorkPieceShape.CUBIC)) {
				this.dimensions = new RectangularDimensions();
				this.representation = new RectanglePieceRepresentation(this);
			} else {
				this.dimensions = new RoundDimensions();
				this.representation = new RoundPieceRepresentation(this);
			}
			this.shape = shape;
		}
	}
	
	public IDrawableObject getRepresentation() {
		return this.representation;
	}

	public IWorkPieceDimensions getDimensions() {
		return dimensions;
	}

	public void setDimensions(final IWorkPieceDimensions dimensions) {
		this.dimensions = dimensions;
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

	public void calculateWeight() {
		if (material.equals(Material.OTHER)) {
			throw new IllegalStateException("Can't calculate weight: unknown material type.");
		} else {
			setWeight(getDimensions().getVolume() * material.getDensity());
		}
	}
	
	public String toString() {
		return "WorkPiece: " + type + " - " + dimensions;
	}
}
