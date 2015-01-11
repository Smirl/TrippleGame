package tripleGame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * The class which is used to draw the state of the simulation. Gets state from InteractiveSIRS then paints every 50 milliseconds
 * @author s0941897
 *
 */
public class TripleViewer extends JComponent {

	//Create a controller
	private Triple controller;

	private Block[] animationBlocks;
	private int counter = 0;
	private int[] lastClicked = {0,0};

	private static final Sprite background = SpriteStore.get().getSprite(BlockType.ROAD.getSprite());

	//Painting timer which repaints
	public Timer autoUpdater = new Timer(20, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) { 
			controller.updatePositions();
			repaint(); 
		}		
	});

	/**
	 * Create new panel to paint main simulation
	 * @param attachedTo the controller
	 */
	public TripleViewer(Triple attachedTo) {
		controller = attachedTo;
		setOpaque(true);
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(402,402));
		autoUpdater.restart();
	}

	public void animate(Block[] toAnimate){
		animationBlocks = toAnimate;
	}

	public void updateCounter(int c){
		counter=c;
	}

	public void setLastClicked(int x, int y){
		lastClicked[0] = x*Block.WIDTH;
		lastClicked[1] = y*Block.WIDTH;
	}

	/**
	 * Overide the paintComponet method to draw grid
	 */
	protected void paintComponent(Graphics g) {

		// Clear the background if we are an opaque component
		if(isOpaque()) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		//get the current grid
		Block[][] currentGrid = controller.getCurrentGrid();

		g.setColor(Color.BLACK);

		//Set correct colour and then paint a rectange for each site
		for(int i=0;i<currentGrid.length;i++){
			for(int j=0;j<currentGrid.length;j++){
				background.draw(g, i*Block.WIDTH, j*Block.WIDTH);
				currentGrid[i][j].draw(g);
				g.drawRect(i*Block.WIDTH, j*Block.WIDTH, Block.WIDTH, Block.WIDTH);
			}
		}

		if(counter>=1) counter++;


		int s = controller.getScore();
		String format = String.format("%%0%dd", 4);
		String result = String.format(format, s);
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.drawString("Score "+result, 10, 20);

		String next = controller.getNext();
		g.drawString(next, 100, 20);

	}
	/**
	 * Called when simulation is started to starts painting the grid at 50 millisecond intervals
	 */
	public void simulationStarted() {
		// Ensure display is up-to-date
		repaint();
		// Send repaint messages at regular intervals using the autoUpdater Timer object
		autoUpdater.restart();
	}

	/**
	 * Called when simulation is stopped, Stops timer and paints final state.
	 */
	public void simulationFinished() {
		// Stop sending regular repaint messages
		autoUpdater.stop();
		// Ensure display is up-to-date
		repaint();

	}

}
