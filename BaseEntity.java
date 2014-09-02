import java.awt.*;
import java.awt.geom.*;

public abstract class BaseEntity {

	private double xpos;
	private double ypos;
	private double width;
	private double height;

	private int xdir;
	private int ydir;

	private Rectangle2D.Double rect;
	private Image img;

	private double prevX;
	private double prevY;
	private double prevWidth;
	private double prevHeight;

	/* constructor */
	public BaseEntity(double xpos, double ypos, double width, double height, Image img){

		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;

		xdir = 0;
		ydir = 0;

		rect = new Rectangle2D.Double(xpos, ypos, width, height);
		this.img = img;
	}

	/* get methods */
	public double getX() {	return xpos; }
	public double getY() {	return ypos; }
	public double getWidth() {	return width; }
	public double getHeight() { return height; }

	public int getXDir() { return xdir; }
	public int getYDir() { return ydir; }

	public Rectangle2D.Double getRectangle2D() { return rect; }
	public Image getImage() { return img; }

	public double getPrevX() { return prevX; }
	public double getPrevY() { return prevY; }
	public double getPrevWidth() { return prevWidth; }
	public double getPrevHeight() { return prevHeight; }

	/* set methods */
	public void setPos(double xpos, double ypos) {
		this.xpos = xpos;
		this.ypos = ypos;
	}

	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public void setDir(int xdir, int ydir) {
		this.xdir = xdir;
		this.ydir = ydir;
	}

	public void setImage(Image img) {
		this.img = img;
	}

	//move method: set to move the entire distance of itself (e.g. xpos += width, ypos += height)
	public void move() {

		prevX = xpos;
		prevY = ypos;
		prevWidth = width;
		prevHeight = height;

		setPos(xpos + xdir*width, ypos + ydir*height);
		rect.setRect(xpos, ypos, width, height);
	}

	/* overloaded intersects methods */
	public boolean intersects(Rectangle2D rectangle){
		return rect.intersects(rectangle);
	}

	public boolean intersects(double xpos, double ypos, double width, double height){
		return rect.intersects(xpos, ypos, width, height);
	}

	//paint method: paints image only (rectangle is only used for collision detection)
	public void paint(Graphics2D g, Component a){
		g.drawImage(img, (int)(xpos), (int)(ypos), (int)(width), (int)(height), a);
	}
}