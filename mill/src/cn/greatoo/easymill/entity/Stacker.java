package cn.greatoo.easymill.entity;

public class Stacker {

	private int hPaddingSum;
	private int vPaddingSum;
	private float horizontalHoleDistance;
	private float verticalHoleDistance;
	private float studDiameter;
	private float horizontalPadding;
	private float verticalPaddingBottom;
	
	private float orientation;
	private int layers;
	private int amount;
	private float studHeight;
	
	public Stacker(int hPaddingSum, int vPaddingSum, float horizontalHoleDistance,float verticalHoleDistance,
			float studDiameter,float horizontalPadding,float verticalPaddingBottom,
			float orientation,int layers,int amount,float studHeight) {
		this.hPaddingSum = hPaddingSum;
		this.vPaddingSum = vPaddingSum;
		this.horizontalHoleDistance = horizontalHoleDistance;
		this.verticalHoleDistance = verticalHoleDistance;
		this.studDiameter = studDiameter;
		this.horizontalPadding = horizontalPadding;
		this.verticalPaddingBottom = verticalPaddingBottom;
		this.orientation = orientation;
		this.layers = layers;
		this.amount = amount;
		this.studHeight = studHeight;
	}

	
	public Stacker() {
		// TODO Auto-generated constructor stub
	}


	public int gethPaddingSum() {
		return hPaddingSum;
	}


	public void sethPaddingSum(int hPaddingSum) {
		this.hPaddingSum = hPaddingSum;
	}


	public int getvPaddingSum() {
		return vPaddingSum;
	}


	public void setvPaddingSum(int vPaddingSum) {
		this.vPaddingSum = vPaddingSum;
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

	public float getStudDiameter() {
		return studDiameter;
	}

	public void setStudDiameter(float studDiameter) {
		this.studDiameter = studDiameter;
	}

	public float getHorizontalPadding() {
		return horizontalPadding;
	}

	public void setHorizontalPadding(float horizontalPadding) {
		this.horizontalPadding = horizontalPadding;
	}

	public float getVerticalPaddingBottom() {
		return verticalPaddingBottom;
	}

	public void setVerticalPaddingBottom(float verticalPaddingBottom) {
		this.verticalPaddingBottom = verticalPaddingBottom;
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


	public float getStudHeight() {
		return studHeight;
	}


	public void setStudHeight(float studHeight) {
		this.studHeight = studHeight;
	}
	
}
