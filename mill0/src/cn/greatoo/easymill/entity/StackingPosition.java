package cn.greatoo.easymill.entity;

public class StackingPosition {

	private int amount;
	private Coordinates coordinates;
	
	public StackingPosition(int amount, Coordinates coordinates) {
		this.amount = amount;
		this.coordinates = coordinates;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public Coordinates getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	
}
