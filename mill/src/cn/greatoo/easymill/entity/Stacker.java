package cn.greatoo.easymill.entity;


public class Stacker {
	private int id;

	private String userFrameName;
	private int horizontalHoleAmount;	
	private int verticalHoleAmount;
	private float holeDiameter;
	private float studDiameter;
	private float horizontalHoleDistance;
	private float verticalHoleDistance;

	private float horizontalPadding;
	private float verticalPaddingTop;
	
	private float verticalPaddingBottom;
	private float interferenceDistance;
	private float overflowPercentage;

	private float horizontalR;
	private float tiltedR;
	
	private float maxOverflow;
	private float maxUnderflow;
	private float minOverlap;

	private float studHeight_Stacker;
	
	private Smooth smoothto;
	private Smooth smoothfrom;
	
	private float orientation;
	private int layers;
	private int amount;
	private float studHeight_Workpiece;
	
	public Stacker(final int horizontalHoleAmount, final int verticalHoleAmount, final float holeDiameter, final float studDiameter, final float horizontalHoleDistance, final float verticalHoleDistance, final float horizontalPadding, 
			final float verticalPaddingTop, final float verticalPaddingBottom, final float interferenceDistance, final float overflowPercentage,
				final float horizontalR, final float tiltedR, final float maxOverflow, final float maxUnderflow, final float minOverlap, final float studHeight_Stacker,
					final float smoothToX, final float smoothToY, final float smoothToZ, final float smoothFromX, final float smoothFromY, 
					final float smoothFromZ, float orientation, int layers,int amount,float studHeight_Workpiece) {
		this.userFrameName =userFrameName;
		this.horizontalPadding = horizontalPadding;
		this.verticalPaddingBottom = verticalPaddingBottom;
		this.verticalPaddingTop = verticalPaddingTop;
		this.horizontalHoleAmount = horizontalHoleAmount;
		this.verticalHoleAmount = verticalHoleAmount;
		this.holeDiameter = holeDiameter;
		this.studDiameter = studDiameter;
		this.horizontalHoleDistance = horizontalHoleDistance;
		this.verticalHoleDistance = verticalHoleDistance;
		this.interferenceDistance = interferenceDistance;
		this.overflowPercentage = overflowPercentage;
		this.maxOverflow = maxOverflow;
		this.maxUnderflow = maxUnderflow;
		this.minOverlap = minOverlap;
		this.tiltedR = tiltedR;
		this.horizontalR = horizontalR;
		this.studHeight_Stacker = studHeight_Stacker;
		this.orientation = orientation;
		this.layers = layers;
		this.amount = amount;
		this.studHeight_Workpiece = studHeight_Workpiece;
	}	

	public Stacker() {
		// TODO Auto-generated constructor stub
	}

	public Stacker(int horizontalHoleAmount, int verticalHoleAmount, float holeDiameter, float studDiameter,
			float horizontalPadding, float verticalPaddingTop, float verticalPaddingBottom,
			float horizontalHoleDistance, float verticalHoleDistance, float interferenceDistance,
			float overflowPercentage, float horizontalR, float tiltedR, float maxOverflow, float maxUnderflow,
			float minOverlap, float orientation, int layers, int amount, float studHeight_Workpiece) {
		this.horizontalPadding = horizontalPadding;
		this.verticalPaddingBottom = verticalPaddingBottom;
		this.verticalPaddingTop = verticalPaddingTop;
		this.horizontalHoleAmount = horizontalHoleAmount;
		this.verticalHoleAmount = verticalHoleAmount;
		this.holeDiameter = holeDiameter;
		this.studDiameter = studDiameter;
		this.horizontalHoleDistance = horizontalHoleDistance;
		this.verticalHoleDistance = verticalHoleDistance;
		this.interferenceDistance = interferenceDistance;
		this.overflowPercentage = overflowPercentage;
		this.maxOverflow = maxOverflow;
		this.maxUnderflow = maxUnderflow;
		this.minOverlap = minOverlap;
		this.tiltedR = tiltedR;
		this.horizontalR = horizontalR;
		this.orientation = orientation;
		this.layers = layers;
		this.amount = amount;
		this.studHeight_Workpiece = studHeight_Workpiece;
	}

	public String getUserFrameName() {
		return userFrameName;
	}


	public void setUserFrameName(String userFrameName) {
		this.userFrameName = userFrameName;
	}


	public int getHorizontalHoleAmount() {
		return horizontalHoleAmount;
	}


	public void setHorizontalHoleAmount(int horizontalHoleAmount) {
		this.horizontalHoleAmount = horizontalHoleAmount;
	}


	public int getVerticalHoleAmount() {
		return verticalHoleAmount;
	}


	public void setVerticalHoleAmount(int verticalHoleAmount) {
		this.verticalHoleAmount = verticalHoleAmount;
	}


	public float getHoleDiameter() {
		return holeDiameter;
	}


	public void setHoleDiameter(float holeDiameter) {
		this.holeDiameter = holeDiameter;
	}


	public float getStudDiameter() {
		return studDiameter;
	}


	public void setStudDiameter(float studDiameter) {
		this.studDiameter = studDiameter;
	}


	public float getHorizontalHoleDistance() {
		return horizontalHoleDistance;
	}


	public void setHorizontalHoleDistance(float horizontalHoleDistance) {
		this.horizontalHoleDistance = horizontalHoleDistance;
	}


	public float getVerticalHoleDistance() {
		return verticalHoleDistance;
	}


	public void setVerticalHoleDistance(float verticalHoleDistance) {
		this.verticalHoleDistance = verticalHoleDistance;
	}


	public float getHorizontalPadding() {
		return horizontalPadding;
	}


	public void setHorizontalPadding(float horizontalPadding) {
		this.horizontalPadding = horizontalPadding;
	}


	public float getVerticalPaddingTop() {
		return verticalPaddingTop;
	}


	public void setVerticalPaddingTop(float verticalPaddingTop) {
		this.verticalPaddingTop = verticalPaddingTop;
	}


	public float getVerticalPaddingBottom() {
		return verticalPaddingBottom;
	}


	public void setVerticalPaddingBottom(float verticalPaddingBottom) {
		this.verticalPaddingBottom = verticalPaddingBottom;
	}


	public float getInterferenceDistance() {
		return interferenceDistance;
	}


	public void setInterferenceDistance(float interferenceDistance) {
		this.interferenceDistance = interferenceDistance;
	}


	public float getOverflowPercentage() {
		return overflowPercentage;
	}


	public void setOverflowPercentage(float overflowPercentage) {
		this.overflowPercentage = overflowPercentage;
	}


	public float getHorizontalR() {
		return horizontalR;
	}


	public void setHorizontalR(float horizontalR) {
		this.horizontalR = horizontalR;
	}


	public float getTiltedR() {
		return tiltedR;
	}


	public void setTiltedR(float tiltedR) {
		this.tiltedR = tiltedR;
	}


	public float getMaxOverflow() {
		return maxOverflow;
	}


	public void setMaxOverflow(float maxOverflow) {
		this.maxOverflow = maxOverflow;
	}


	public float getMaxUnderflow() {
		return maxUnderflow;
	}


	public void setMaxUnderflow(float maxUnderflow) {
		this.maxUnderflow = maxUnderflow;
	}


	public float getMinOverlap() {
		return minOverlap;
	}


	public void setMinOverlap(float minOverlap) {
		this.minOverlap = minOverlap;
	}


	public float getStudHeight_Stacker() {
		return studHeight_Stacker;
	}


	public void setStudHeight_Stacker(float studHeight_Stacker) {
		this.studHeight_Stacker = studHeight_Stacker;
	}


	public Smooth getSmoothto() {
		return smoothto;
	}


	public void setSmoothto(Smooth smoothto) {
		this.smoothto = smoothto;
	}


	public Smooth getSmoothfrom() {
		return smoothfrom;
	}


	public void setSmoothfrom(Smooth smoothfrom) {
		this.smoothfrom = smoothfrom;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
