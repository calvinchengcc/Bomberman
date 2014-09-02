import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import javax.imageio.ImageIO;

public class BombermanGame extends Panel implements KeyListener {

	private static final long serialVersionUID = 4378130292215014642L;
	
	//double buffer
	private BufferedImage imageBuffer;
	private Graphics2D graphicsBuffer;
	private ScheduledExecutorService t;

	//constants
	public static final byte BLOCK_SIZE = 50;

	private ArrayList<Bomber> bomber = new ArrayList<Bomber>(4);
	private ArrayList<Brick> softBrick = new ArrayList<Brick>();
	private ArrayList<Brick> hardBrick = new ArrayList<Brick>();
	private ArrayList<Bomb> bombs = new ArrayList<Bomb>();
	private ArrayList<Explosion> explosion = new ArrayList<Explosion>();
	private ArrayList<PowerUp> powerUp = new ArrayList<PowerUp>();

	//image declarations
	private static Image[] bomberImage = new Image[4];
	private static Image hardBrickImage;
	private static Image softBrickImage;
	private static Image bombImage;
	private static Image explosionImage;
	private static Image powerUp_MoreBombs;
	private static Image powerUp_MoreRange;
	private static Image powerUp_PassBomb;
	private static Image powerUp_AddLife;
	private static Image powerUp_MysteryBox;

	private double blockWidth = BLOCK_SIZE;
	private double blockHeight = BLOCK_SIZE;
	private ArrayList<Double> hardBlockX = new ArrayList<Double>(22);
	private ArrayList<Double> hardBlockY = new ArrayList<Double>(11);
	private ArrayList<Double> softBlockX = new ArrayList<Double>(23);
	private ArrayList<Double> softBlockY = new ArrayList<Double>(12);
	
	public BombermanGame() {
		//get images and dimensions
		bomberImage[0] = getImage("Bomber1_Yellow.png");
		bomberImage[1] = getImage("Bomber2_Green.png");
		bomberImage[2] = getImage("Bomber3_Blue.png");
		bomberImage[3] = getImage("Bomber4_Red.png");

		hardBrickImage = getImage("Hard Brick.png");
		softBrickImage = getImage("Soft Brick.png");
		bombImage = getImage("Bomb.png");
		explosionImage = getImage("Explosion.png");

		powerUp_MoreBombs = getImage("PowerUp_MoreBombs.png");
		powerUp_MoreRange = getImage("PowerUp_MoreRange.png");
		powerUp_PassBomb = getImage("PowerUp_PassBomb.png");
		powerUp_AddLife = getImage("PowerUp_AddLife.png");
		powerUp_MysteryBox = getImage("PowerUp_MysteryBox.png");
		
		setPreferredSize(new Dimension(1201, 651));
		setSize(1201, 651);
	}
	
	public void init() {
		
		//double buffering
		imageBuffer = (BufferedImage)(createImage(getWidth(), getHeight()));
		graphicsBuffer = (Graphics2D)(imageBuffer.getGraphics());
		
		setUpPlayers();
		setUpHardBricks();
		setUpSoftBricks();

		addKeyListener(this);
		setFocusable(true);

		Runnable game = new TimerTask(){
			public void run() {

				Graphics g = getGraphics();

				//call objects to move
				for(Bomber b : bomber){
					b.move();
				}
				
				checkCollisions();

				//if bomber life is 0, take him out of the game
				for(Bomber b : bomber){
					if(b.getLives()<=0){
						b.setPos(getWidth(), getHeight());
						b.setSize(0, 0);
						b.setDir(0, 0);
					}
				}

				//explosion collision detection, randomly put powerup
				for(Explosion ex : explosion){

					for(int i=0; i<softBrick.size(); i++){
						if(softBrick.get(i).intersects(ex.getRectangle2D())){
							ex.power = true;
							softBrick.remove(i);
						}
					}

					for(int i=0; i<powerUp.size(); i++){
						if(powerUp.get(i).intersects(ex.getRectangle2D())){
							powerUp.remove(i);
						}
					}
				}

				//increase timer, check the bomb's timer, and explode if the number is high enough
				for(int i=0; i<bombs.size(); i++){
					
					bombs.get(i).timer++;
					
					if(bombs.get(i).timer > 25){

						//add an explosion where bomb was
						explosion.add(new Explosion(bombs.get(i).getX(), bombs.get(i).getY(), blockWidth, blockHeight, explosionImage));

						//explode left
						left:
							for(int j=1; j<=bombs.get(i).getRange(); j++){

								Rectangle2D.Double r = new Rectangle2D.Double(bombs.get(i).getX()-j*blockWidth, bombs.get(i).getY(), blockWidth, blockHeight);

								for(Brick b : hardBrick){
									if(r.intersects(b.getRectangle2D())){
										break left;
									}
								}
								
//								r = new Rectangle2D.Double(bombs.get(i).getX(), bombs.get(i).getY()-(j+1)*blockHeight, blockWidth, blockHeight);
//								
//								for(Brick b : softBrick){
//									if(r.intersects(b.getRectangle2D())){
//										addable = false;
//										break left;
//									}
//								}
								
								explosion.add(new Explosion(r.getX(), r.getY(), blockWidth, blockHeight, explosionImage));
							}

						//explode right
						right:
							for(int j=1; j<=bombs.get(i).getRange(); j++){

								Rectangle2D.Double r = new Rectangle2D.Double(bombs.get(i).getX()+j*blockWidth, bombs.get(i).getY(), blockWidth, blockHeight);

								for(Brick b : hardBrick){
									if(r.intersects(b.getRectangle2D())){
										break right;
									}
								}
								
//								r = new Rectangle2D.Double(bombs.get(i).getX(), bombs.get(i).getY()-(j+1)*blockHeight, blockWidth, blockHeight);
//								
//								for(Brick b : softBrick){
//									if(r.intersects(b.getRectangle2D())){
//										addable = false;
//										break right;
//									}
//								}
								
								explosion.add(new Explosion(r.getX(), r.getY(), blockWidth, blockHeight, explosionImage));
							}

						//explode up
						up:
							for(int j=1; j<=bombs.get(i).getRange(); j++){

								Rectangle2D.Double r = new Rectangle2D.Double(bombs.get(i).getX(), bombs.get(i).getY()-j*blockHeight, blockWidth, blockHeight);

								for(Brick b : hardBrick){
									if(r.intersects(b.getRectangle2D())){
										break up;
									}
								}
								
//								r = new Rectangle2D.Double(bombs.get(i).getX(), bombs.get(i).getY()-(j+1)*blockHeight, blockWidth, blockHeight);
//								
//								for(Brick b : softBrick){
//									if(r.intersects(b.getRectangle2D())){
//										addable = false;
//										break up;
//									}
//								}
								
								explosion.add(new Explosion(r.getX(), r.getY(), blockWidth, blockHeight, explosionImage));
							}

						//explode down
						down:
							for(int j=1; j<=bombs.get(i).getRange(); j++){

								Rectangle2D.Double r = new Rectangle2D.Double(bombs.get(i).getX(), bombs.get(i).getY()+j*blockHeight, blockWidth, blockHeight);

								for(Brick b : hardBrick){
									if(r.intersects(b.getRectangle2D())){
										break down;
									}
								}
								
//								r = new Rectangle2D.Double(bombs.get(i).getX(), bombs.get(i).getY()-(j+1)*blockHeight, blockWidth, blockHeight);
//								
//								for(Brick b : softBrick){
//									if(r.intersects(b.getRectangle2D())){
//										addable = false;
//										break down;
//									}
//								}
								
								explosion.add(new Explosion(r.getX(), r.getY(), blockWidth, blockHeight, explosionImage));
							}

						bombs.get(i).explode();
						bombs.remove(i);
					}
				}

				//increase timers, check the explosions' timers, and disappear if the number is high enough, and put a power up if true
				for(int i=0; i<explosion.size(); i++){
					
					explosion.get(i).timer++;
					
					if(explosion.get(i).timer > 5){
						
						if(explosion.get(i).power){
							
							int random = (int)(Math.random()*20);
							
							if(random==0){
								
								powerUp.add(new PowerUp(explosion.get(i), random, powerUp_MoreBombs));
								
							}else if(random==1){
								
								powerUp.add(new PowerUp(explosion.get(i), random, powerUp_MoreRange));
								
							}else if(random==2){
								
								powerUp.add(new PowerUp(explosion.get(i), random, powerUp_PassBomb));
								
							}else if(random==3){
								
								powerUp.add(new PowerUp(explosion.get(i), random, powerUp_AddLife));
								
							}else if(random==4){
								
								powerUp.add(new PowerUp(explosion.get(i), random, powerUp_MysteryBox));
								
							}
						}
						
						explosion.remove(i);
					}
				}
				
				//check whether a bomber has powered up
				for(Bomber b : bomber){
					
					for(int i=0; i<powerUp.size(); i++){
						
						if(b.intersects(powerUp.get(i).getRectangle2D())){
							
							if(powerUp.get(i).getPowerID()==4){
								
								powerUp.get(i).setPowerID((int)(Math.random()*5));
								
							}
							
							if(powerUp.get(i).getPowerID()==0){
								
								b.increaseMaxBombs(1);
								
							}else if(powerUp.get(i).getPowerID()==1){
								
								b.increaseBombRange(1);
								
							}else if(powerUp.get(i).getPowerID()==2){
								
								b.passBomb = true;
								
							}else if(powerUp.get(i).getPowerID()==3){
								
								b.setLives(b.getLives()+1);
								
							}
							
							powerUp.remove(i);
						}
					}
				}

				paint(g);
			}
		};

		t = Executors.newScheduledThreadPool(1);
		t.scheduleAtFixedRate(game, 0, 75, TimeUnit.MILLISECONDS);
	}

	public void keyPressed(KeyEvent e){

		int keyCode = e.getKeyCode();

		/* Bomber 1 keys
		 * ARROW KEYS / ENTER
		 */

		//movement
		if(keyCode == KeyEvent.VK_A){
			bomber.get(0).setDir(-1, 0);
		}else if(keyCode == KeyEvent.VK_D){
			bomber.get(0).setDir(1, 0);
		}

		if(keyCode == KeyEvent.VK_W){
			bomber.get(0).setDir(0, -1);
		}else if(keyCode == KeyEvent.VK_S){
			bomber.get(0).setDir(0, 1);
		}

		//bomb drop
		if(keyCode == KeyEvent.VK_SPACE){
			if(bomber.get(0).canDropBomb()){
				bombs.add(new Bomb(bomber.get(0), bombImage));
			}
		}


		/* Bomber 2 keys
		 * WASD / SPACE
		 */

		//movement
		if(keyCode == KeyEvent.VK_LEFT){
			bomber.get(1).setDir(-1, 0);
		}else if(keyCode == KeyEvent.VK_RIGHT){
			bomber.get(1).setDir(1, 0);
		}

		if(keyCode == KeyEvent.VK_UP){
			bomber.get(1).setDir(0, -1);
		}else if(keyCode == KeyEvent.VK_DOWN){
			bomber.get(1).setDir(0, 1);
		}

		//bomb drop
		if(keyCode == KeyEvent.VK_ENTER){
			if(bomber.get(1).canDropBomb()){
				bombs.add(new Bomb(bomber.get(1), bombImage));
			}
		}


		/* Bomber 3 keys
		 * IJKL / ;
		 */

		//movement
		if(keyCode == KeyEvent.VK_J){
			bomber.get(2).setDir(-1, 0);
		}else if(keyCode == KeyEvent.VK_L){
			bomber.get(2).setDir(1, 0);
		}

		if(keyCode == KeyEvent.VK_I){
			bomber.get(2).setDir(0, -1);
		}else if(keyCode == KeyEvent.VK_K){
			bomber.get(2).setDir(0, 1);
		}

		//bomb drop
		if(keyCode == KeyEvent.VK_SEMICOLON){
			if(bomber.get(2).canDropBomb()){
				bombs.add(new Bomb(bomber.get(2), bombImage));
			}
		}


		/* Bomber 4 keys	*
		 * 8456 / +
		 */

		//movement
		if(keyCode == KeyEvent.VK_NUMPAD4){
			bomber.get(3).setDir(-1, 0);
		}else if(keyCode == KeyEvent.VK_NUMPAD6){
			bomber.get(3).setDir(1, 0);
		}

		if(keyCode == KeyEvent.VK_NUMPAD8){
			bomber.get(3).setDir(0, -1);
		}else if(keyCode == KeyEvent.VK_NUMPAD5){
			bomber.get(3).setDir(0, 1);
		}

		//bomb drop
		if(keyCode == KeyEvent.VK_ADD){
			if(bomber.get(3).canDropBomb()){
				bombs.add(new Bomb(bomber.get(3), bombImage));
			}
		}
	}

	public void keyReleased(KeyEvent e){

		int keyCode = e.getKeyCode();

		/* Bomber 1 stop motion */
		if(keyCode == KeyEvent.VK_A){
			bomber.get(0).setDir(0, 0);
		}else if(keyCode == KeyEvent.VK_D){
			bomber.get(0).setDir(0, 0);
		}

		if(keyCode == KeyEvent.VK_W){
			bomber.get(0).setDir(0, 0);
		}else if(keyCode == KeyEvent.VK_S){
			bomber.get(0).setDir(0, 0);
		}


		/* Bomber 2 stop motion */
		if(keyCode == KeyEvent.VK_LEFT){
			bomber.get(1).setDir(0, 0);
		}else if(keyCode == KeyEvent.VK_RIGHT){
			bomber.get(1).setDir(0, 0);
		}

		if(keyCode == KeyEvent.VK_UP){
			bomber.get(1).setDir(0, 0);
		}else if(keyCode == KeyEvent.VK_DOWN){
			bomber.get(1).setDir(0, 0);
		}

		/* Bomber 3 stop motion */
		if(keyCode == KeyEvent.VK_J){
			bomber.get(2).setDir(0, 0);
		}else if(keyCode == KeyEvent.VK_L){
			bomber.get(2).setDir(0, 0);
		}

		if(keyCode == KeyEvent.VK_I){
			bomber.get(2).setDir(0, 0);
		}else if(keyCode == KeyEvent.VK_K){
			bomber.get(2).setDir(0, 0);
		}

		/* Bomber 4 stop motion */
		if(keyCode == KeyEvent.VK_NUMPAD4){
			bomber.get(3).setDir(0, 0);
		}else if(keyCode == KeyEvent.VK_NUMPAD6){
			bomber.get(3).setDir(0, 0);
		}

		if(keyCode == KeyEvent.VK_NUMPAD8){
			bomber.get(3).setDir(0, 0);
		}else if(keyCode == KeyEvent.VK_NUMPAD5){
			bomber.get(3).setDir(0, 0);
		}
	}

	public void keyTyped(KeyEvent e){}

	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		
		if (graphicsBuffer == null) {
			return;
		}

		//background, turn on anti-aliasing
		graphicsBuffer.setColor(Color.lightGray);
		graphicsBuffer.fillRect(0, 0, getWidth(), getHeight());
		graphicsBuffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		HashSet<BaseEntity> objectsToDraw = new HashSet<BaseEntity>();
		objectsToDraw.addAll(explosion);
		objectsToDraw.addAll(hardBrick);
		objectsToDraw.addAll(softBrick);
		objectsToDraw.addAll(bomber);
		objectsToDraw.addAll(bombs);
		objectsToDraw.addAll(powerUp);
		
		for (BaseEntity drawObject : objectsToDraw) {
			drawObject.paint(graphicsBuffer, this);
		}

		//draw bomber lives
		graphicsBuffer.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		printPlayerInfo(0, Color.YELLOW, g2);
		printPlayerInfo(1, Color.RED, g2);
		printPlayerInfo(2, Color.CYAN, g2);
		printPlayerInfo(3, Color.GREEN, g2);

		//draw everything from buffer
		g2.drawImage(imageBuffer, 0,0, getWidth(), getHeight(), this);
	}
	
	private Image getImage(String name) {
		try {
			return ImageIO.read(getClass().getClassLoader().getResource(name));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void setUpPlayers() {
		//bomber initializations
		bomber.add(new Bomber(blockWidth, blockHeight, blockWidth, blockHeight, bomberImage[0]));											//top left corner
		bomber.add(new Bomber(getWidth()-blockWidth-blockWidth-1, getHeight()-2*blockHeight-1, blockWidth, blockHeight, bomberImage[1]));	//bottom right corner
		bomber.add(new Bomber(blockWidth, getHeight()-2*blockHeight-1, blockWidth, blockHeight, bomberImage[2]));							//bottom left corner
		bomber.add(new Bomber(getWidth()-blockWidth-blockWidth-1, blockHeight, blockWidth, blockHeight, bomberImage[3]));					//top right corner

	}
	
	private void setUpHardBricks() {
		for(double i=2; i<22; i++){
			hardBlockX.add(i);
		}
		for(double i=2; i<11; i++){
			hardBlockY.add(i);
		}
		
		//hard brick for walls and map
		for(int i=0; i<getWidth()/blockWidth-1; i++){
			hardBrick.add(new Brick(blockWidth*i, 0, blockWidth, blockHeight, hardBrickImage));							//top wall
			hardBrick.add(new Brick(blockWidth*i, getHeight()-blockHeight-1, blockWidth, blockHeight, hardBrickImage));	//bottom wall
		}
		for(int i=1; i<getHeight()/blockHeight-2; i++){
			hardBrick.add(new Brick(0, blockHeight*i, blockWidth, blockHeight, hardBrickImage));						//left wall
			hardBrick.add(new Brick(getWidth()-blockWidth-1, blockHeight*i, blockWidth, blockHeight, hardBrickImage));	//right wall
		}

		for(int i=0; i<50; i++){

			double x = hardBlockX.get((int)(Math.random()*hardBlockX.size()))*blockWidth;
			double y = hardBlockY.get((int)(Math.random()*hardBlockY.size()))*blockHeight;

			hardBrick.add(new Brick(x, y, blockWidth, blockHeight, hardBrickImage));
		}
	}
	
	private void setUpSoftBricks() {
		for(double i=1; i<23; i++){
			softBlockX.add(i);
		}
		for(double i=1; i<12; i++){
			softBlockY.add(i);
		}

		//soft bricks randomly in map
		for(int i=0; i<125; i++){

			boolean empty = true;
			double x = softBlockX.get((int)(Math.random()*softBlockX.size()))*blockWidth;
			double y = softBlockY.get((int)(Math.random()*softBlockY.size()))*blockWidth;

			Rectangle2D.Double r = new Rectangle2D.Double(x, y, blockWidth, blockHeight);

			//check to see if any of the existing soft bricks are already there
			for(Brick b : softBrick){
				if(b.intersects(r)){
					empty = false;
					break;
				}
			}

			//check to see if a hard brick is already there
			for(Brick b : hardBrick){
				if(b.intersects(r)){
					empty = false;
					break;
				}
			}

			//check to see if the soft brick will be in an area which might make it impossible for the player to move
			Rectangle2D.Double[] playerArea = new Rectangle2D.Double[4];
			playerArea[0] = new Rectangle2D.Double(blockWidth, blockHeight, 2*blockWidth, 2*blockHeight);
			playerArea[1] = new Rectangle2D.Double(blockWidth, getHeight()-3*blockHeight, 2*blockWidth, 2*blockHeight);
			playerArea[2] = new Rectangle2D.Double(getWidth()-3*blockWidth, blockHeight, 2*blockWidth, 2*blockHeight);
			playerArea[3] = new Rectangle2D.Double(getWidth()-3*blockWidth, getHeight()-3*blockHeight, 2*blockWidth, 2*blockHeight);

			for(Rectangle2D.Double p : playerArea){
				if(p.intersects(r)){
					empty = false;
					break;
				}
			}

			if(empty){
				softBrick.add(new Brick(x, y, blockWidth, blockHeight, softBrickImage));
			}else if(i > 0){
				i--;
			}
		}
	}
	
	private void checkCollisions() {

		//bomber collision detection with a wall/bomb/explosion
		for(Bomber b : bomber){

			//hard walls
			for(Brick brick : hardBrick){
				if(b.intersects(brick.getRectangle2D())){
					b.setPos(b.getPrevX(), b.getPrevY());
					break;
				}
			}

			//soft walls
			for(Brick brick : softBrick){
				if(b.intersects(brick.getRectangle2D())){
					b.setPos(b.getPrevX(), b.getPrevY());
					break;
				}
			}

			//bombs
			for(Bomb bomb : bombs){
				if(b.intersects(bomb.getRectangle2D()) && !b.passBomb) {
					b.setPos(b.getPrevX(), b.getPrevY());
					break;
				}
			}

			//explosions (lose a life, start from corner);
			for(Explosion ex : explosion){
				if(b.intersects(ex.getRectangle2D())){
					b.setPos(b.getOrigX(), b.getOrigY());
					b.setLives(b.getLives()-1);
					break;
				}
			}
		}
	}
	
	private void printPlayerInfo(int playerNum, Color color, Graphics2D g2) {
		graphicsBuffer.setColor(color);
		String toPrint = "Player " + (playerNum + 1) + " lives left: ";
		
		switch(playerNum) {
		case 0:
			toPrint += bomber.get(playerNum).getLives() + "  |  ";
			toPrint += "WASD to move, 'Space' to drop bomb";
			break;
		case 1:
			toPrint += bomber.get(3).getLives() + "  |  ";
			toPrint += "Numpad 8456 to move, '+' to drop bomb";
			break;
		case 2:
			toPrint += bomber.get(playerNum).getLives() + "  |  ";
			toPrint += "IJKL to move, ';' to drop bomb";
			break;
		case 3:
			toPrint += bomber.get(1).getLives() + "  |  ";
			toPrint += "Arrow keys to move, 'Enter' to drop bomb";
			break;
		}
		
		int stringWidth = (int)g2.getFontMetrics().stringWidth(toPrint);
		int stringHeight = (int)g2.getFontMetrics().getStringBounds(toPrint, g2).getHeight();
		
		switch(playerNum) {
		case 0:
			graphicsBuffer.drawString(toPrint, 20, 15 + stringHeight);
			break;
		case 1:
			graphicsBuffer.drawString(toPrint, getWidth() - stringWidth - 220, 15 + stringHeight);
			break;
		case 2:
			graphicsBuffer.drawString(toPrint, 20, getHeight() - 20);
			break;
		case 3:
			graphicsBuffer.drawString(toPrint, getWidth() - stringWidth - 230, getHeight() - 20);
			break;
		}
	}
	
	public Image getIconImage() {
		return bombImage;
	}
}