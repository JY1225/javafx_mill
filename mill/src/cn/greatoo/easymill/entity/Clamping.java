package cn.greatoo.easymill.entity;

import java.beans.Transient;
import java.util.HashSet;
import java.util.Set;


import cn.greatoo.easymill.entity.Coordinates;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="Clamping")

public class Clamping implements Cloneable {
	
	public enum ClampingType {
		LENGTH, WIDTH
	}
	
	public static enum Type {
		CENTRUM {
			@Override
			public String toString() {
				return "Centrum";
			}
		}, 
		FIXED_XP {
			@Override
			public String toString() {
				return "Fix X +";
			}
		}, 
		FIXED_XM {
			@Override
			public String toString() {
				return "Fix X -";
			}
		}, 
		FIXED_YP {
			@Override
			public String toString() {
				return "Fix Y +";
			}
		}, 
		FIXED_YM {
			@Override
			public String toString() {
				return "Fix Y -";
			}
		}, 
		NONE
	} 
	
	@Id
	@Column(name="Clamping_ID", nullable=false, updatable=false, unique=true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(name = "Clamping_NAME",length=32)
	private String name;

	@Column(name = "relativePosition",length=32)

	private Coordinates relativePosition;
	
	private Coordinates smoothToPoint;
	private Coordinates smoothFromPoint;
	private float height;
	private float defaultHeight;
	private String imageURL;
	private Type type;	
	private String processName;
	private ClampingType clampingType;

	// Process ID that is currently located in the clamping - default value = -1
	// In case of dualLoad, we can have 'two' workpieces in 'one' clamping
	private Set<Integer> prcIdUsingClamping;
	// Related clampings that are currently active for use - unique per processFlow
	private Set<Clamping> relatedClampings;
	// Default
	private int nbOfPossibleWPToStore = 1;
	
	public Clamping(final Type type, ClampingType clampingType, final String name, final float defaultHeight, final Coordinates relativePosition, final Coordinates smoothToPoint,
			final Coordinates smoothFromPoint, final String imageURL) {
		this.name = name;
		this.height = defaultHeight;
		this.defaultHeight = defaultHeight;
		this.relativePosition = relativePosition;
		this.smoothToPoint = smoothToPoint;
		this.smoothFromPoint = smoothFromPoint;
		this.imageURL = imageURL;
		this.prcIdUsingClamping = new HashSet<Integer>();
		this.relatedClampings = new HashSet<Clamping>();
		this.type = type;
		this.clampingType = clampingType;
	}

	public Clamping(final Type type, ClampingType clampingType,final String name, String  processName, Program.Step step, final float defaultHeight, 
			final Coordinates relativePosition, final Coordinates smoothPoint, final String imageURL) {
		this(type, clampingType, name, defaultHeight, relativePosition, smoothPoint, smoothPoint, imageURL);
	}
	
	public Clamping() {
		// TODO Auto-generated constructor stub
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

	public ClampingType getClampingType() {
		return clampingType;
	}

	public void setClampingType(ClampingType clampingType) {
		this.clampingType = clampingType;
	}

	public void addRelatedClamping(final Clamping clamping) {
		relatedClampings.add(clamping);
	}
	
	public void removeRelatedClamping(final Clamping clamping) {
		Clamping clToRemove = null;
		for (Clamping relClamping: relatedClampings) {
			if (clamping.equals(relClamping)) {
				clToRemove = relClamping;
				break;
			}
		}
		if (clToRemove != null) {
			relatedClampings.remove(clToRemove);
		}
	}
	
	public Set<Clamping> getRelatedClampings() {
		return relatedClampings;
	}
	
	public void setRelatedClampings(final Set<Clamping> tobeRelatedClampings) {
		this.relatedClampings = tobeRelatedClampings;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public Coordinates getRelativePosition() {
		return relativePosition;
	}

	public void setRelativePosition(final Coordinates relativePosition) {
		this.relativePosition = relativePosition;
	}

	public String getImageUrl() {
		return imageURL;
	}

	public void setImageUrl(final String imageURL) {
		this.imageURL = imageURL;
	}

	public Coordinates getSmoothToPoint() {
		return smoothToPoint;
	}

	public void setSmoothToPoint(final Coordinates smoothToPoint) {
		this.smoothToPoint = smoothToPoint;
	}

	public Coordinates getSmoothFromPoint() {
		return smoothFromPoint;
	}

	public void setSmoothFromPoint(final Coordinates smoothFromPoint) {
		this.smoothFromPoint = smoothFromPoint;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(final float height) {
		this.height = height;
	}
	
	public float getDefaultHeight() {
		return defaultHeight;
	}

	public void setDefaultHeight(final float defaultHeight) {
		this.defaultHeight = defaultHeight;
	}
	
	public void resetHeightToDefault() {
		this.height = defaultHeight;
	}
	
	public int getNbPossibleWPToStore() {
		return this.nbOfPossibleWPToStore;
	}
	
	public void setNbPossibleWPToStore(final int nbWPToStore) {
		this.nbOfPossibleWPToStore = nbWPToStore;
	}
	
	public synchronized Set<Integer> getProcessIdUsingClamping() {
		return this.prcIdUsingClamping;
	}
	
	public synchronized void addProcessIdUsingClamping(int id) {
		this.prcIdUsingClamping.add(id);
	}
	
	public synchronized boolean isInUse(int processId) {
		if(prcIdUsingClamping.contains(processId)) {
			return true;
		}
		return (prcIdUsingClamping.size() >= nbOfPossibleWPToStore);
	}
	
	@Override
	public Clamping clone() throws CloneNotSupportedException {
		Clamping clonedClamping = new Clamping(this.type, this.clampingType,this.name, this.defaultHeight, this.relativePosition, this.smoothToPoint, this.smoothFromPoint, this.imageURL);
		clonedClamping.setId(this.id);
		return clonedClamping;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Clamping)) {
			throw new IllegalArgumentException("Cannot compare " + obj.getClass() + " with " + this.getClass());
		}
		Clamping clamping = (Clamping) obj;
		if (clamping.getId() == this.getId() && clamping.getName().equals(this.getName())) {
			return true;
		}
 		return false;
	}

	@Override
	public int hashCode() {
		return this.getId() * this.getName().hashCode() * getRelatedClampings().hashCode();
	}
	
	@Override
	public String toString() {
		return "Clamping " + getName();
	}

}
