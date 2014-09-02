import java.awt.Image;

public class Bomb extends BaseEntity {

	private Bomber b;
	public int timer = 0;

	public Bomb(Bomber b, Image img){

		super(b.getX(), b.getY(), b.getWidth(), b.getHeight(), img);

		b.increaseBombsDeployed(1);
		b.getBombRange();

		this.b = b;
	}

	public int getRange() { return b.getBombRange(); }

	public void explode(){
		b.increaseBombsDeployed(-1);
	}
}