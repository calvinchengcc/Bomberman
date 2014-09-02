import java.awt.*;
import java.awt.event.*;

public class GameLauncher {
	
	public static void main(String[] args) {
		
		BombermanGame game = new BombermanGame();
	    Frame myFrame = new Frame("Bomberman"); // create frame with title
	    myFrame.add(game);
	    
	    myFrame.setLocation(100, 100);
	    myFrame.setResizable(false);
	    myFrame.pack();
	    myFrame.setIconImage(game.getIconImage());
	    myFrame.setVisible(true); // usual step to make frame visible
	    
	    myFrame.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent we) {
	    		System.exit(0);
	    	}
	    });
	    
	    game.requestFocus();
	    game.init();
	}
}
