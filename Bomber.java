import java.awt.Image;
import java.util.ArrayList;

public class Bomber extends BaseEntity {

	private final int origX;
	private final int origY;

	private int maxBombs = 1;
	private int bombsDeployed = 0;
	private int bombRange = 1;
	private int lives = 3;
	
	public boolean passBomb = false;

	ArrayList<Bomb> bomb = new ArrayList<Bomb>();

	public Bomber(double xpos, double ypos, double width, double height, Image img){

		super(xpos, ypos, width, height, img);

		origX = (int)(xpos);
		origY = (int)(ypos);
	}

	public boolean canDropBomb(){
		return (bombsDeployed < maxBombs) ? true : false;
	}

	public void increaseMaxBombs(int increment){
		maxBombs += increment;
	}

	public void increaseBombsDeployed(int increment){
		bombsDeployed += increment;
	}

	public void increaseBombRange(int increment){
		bombRange += increment;
	}

	public void setBombsDeployed(int bombs){
		bombsDeployed = bombs;
	}

	public void setLives(int lives){
		this.lives = lives;
	}

	public int getOrigX() { return origX; }
	public int getOrigY() { return origY; }
	public int getMaxBombs() { return maxBombs; }
	public int getBombsDeployed() { return bombsDeployed; }
	public int getBombRange() { return bombRange; }
	public int getLives() { return lives; }
}