import java.awt.Image;

public class Explosion extends BaseEntity {

	public int timer = 0;
	public boolean power = false;

	public Explosion(double xpos, double ypos, double width, double height, Image img){

		super(xpos, ypos, width, height, img);
	}
}