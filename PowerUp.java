import java.awt.Image;

class PowerUp extends BaseEntity {

	private int powerID = 0;

	public PowerUp(Explosion ex, int powerID, Image img){

		super(ex.getX(), ex.getY(), ex.getWidth(), ex.getHeight(), img);
		
		this.powerID = powerID;
	}
	
	public void setPowerID(int powerID){
		
		this.powerID = powerID;
	}
	
	public int getPowerID() { return powerID; }
}