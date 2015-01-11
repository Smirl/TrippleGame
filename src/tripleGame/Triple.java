package tripleGame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * The controller for the SIRSModel Interactive simulation. This links it all together and deals with multi threading issues
 * 
 * @author s0941897
 *
 */
public class Triple extends JFrame implements MouseListener, MouseMotionListener, KeyListener {

	//The game grid
	private BlockType[][] grid;
	private Block[][] block;
	private ArrayList<Block> remove = new ArrayList<Block>();
	private BlockType next = BlockType.GRASS;

	//Create the viewing classes
	private TripleViewer viewer = new TripleViewer(this);
	//private TripleUserPanel user = new TripleUserPanel(this);

	//Start a new thread which the simulation is started on
	private Thread background = null;

	//The interation number
	private int score = 0; 

	//The Currently pressed mouse button
	private int mouseButton;


	/**
	 * The constructor which adds the JPanels to the frame and sets up an initial condition
	 * @throws HeadlessException
	 */
	public Triple() throws HeadlessException {
		// TODO Auto-generated constructor stub
		super("Interactive SIRS Model");
		setDefaultCloseOperation(EXIT_ON_CLOSE);


		getContentPane().add(viewer, BorderLayout.CENTER);
		pack();

		viewer.addMouseListener(this);
		viewer.addMouseMotionListener(this);
		this.addKeyListener(this);

		init(6,6);

		setVisible(true);

	}


	//Getters and setters 
	/**
	 * Gets the current state from the model to give to the viewer. Also updates the in-class private variables used for keeping track of paused 
	 * simulations
	 * @return the current grid
	 */
	public Block[][] getCurrentGrid(){

		synchronized (this) {

			return block;
		}

	}

	/**
	 * Get the length of one side of the grid
	 * @return gridsize
	 */
	public int[] getCurrentGridSize(){
		int[] out = new int[2];
		out[0] = grid.length;
		out[1] = grid[0].length;
		return out;
	}
	/**
	 * Get the step number
	 */
	public int getScore(){
		return score;
	}
	public String getNext(){
		return ""+next;
	}

	/**
	 * set the length of one side of the grid. In the SIRSModel this creates a new randomFillGrid. Repaint after to show the new grid
	 * @see SIRSModel.randomFillGrid()
	 * @param g the new gridsize
	 */
	public void setGridsize(int x, int y){
		init(x,y);
		repaint();
	}


	/**
	 * See if the simulation is running
	 * @return true if is running
	 */
	public boolean running(){
		if(background == null){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * Called by the reset button. Randomly fills grid then repaints.
	 */
	public void resetGrid(){
		int[] size = getCurrentGridSize();
		init(size[0],size[1]);
		score=0;
		repaint();
	}

	public void init(int x, int y){
		grid = new BlockType[x][y];
		block = new Block[x][y];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if(Math.random() < 0.8) {
					grid[i][j] = BlockType.ROAD;
					block[i][j] = new Block(i*Block.WIDTH, j*Block.WIDTH, BlockType.ROAD);
				}else{
					BlockType t = BlockType.randomLetter();
					while(t == BlockType.ROBOT) t = BlockType.randomLetter();
					grid[i][j] = t;
					block[i][j] = new Block(i*Block.WIDTH, j*Block.WIDTH, t);
				}
			}
		}
	}

	public void randomBlock(double prob){
		if(Math.random() < prob) {
			next =  BlockType.GRASS;
		}else{
			next = BlockType.randomLetter();
		}
	}

	public void animate(int x, int y, Block[] toAnimate){
		for (int i = 0; i < toAnimate.length; i++) {
			toAnimate[i].setVelocity((x-toAnimate[i].getX())/Math.abs(x-toAnimate[i].getX()), 
									 (y-toAnimate[i].getY())/Math.abs(y-toAnimate[i].getY()));
		}
	}
	
	public void updatePositions(){
		for (int i = 0; i < block.length; i++) {
			for (int j = 0; j < block[i].length; j++) {
				block[i][j].updatePos();
			}
		}
	}


	public void checkGrid(int i, int j) {

		remove.clear();

		doubleCheck(i, j, 5);
		if(remove.size() <=1) remove.clear();

		if(j>=1 && grid[i][j-1] == grid[i][j] && doubleCheck(i, j-1, 2)) {
			remove.add(block[i][j-1]);
		}
		if(j<=grid[i].length -2 && grid[i][j+1] == grid[i][j] && doubleCheck(i, j+1, 0)) {
			remove.add(block[i][j+1]);
		}
		if(i>= 1 && grid[i-1][j] == grid[i][j] && doubleCheck(i-1, j, 1)) {
			remove.add(block[i-1][j]);
		}
		if(i<=grid.length -2 && grid[i+1][j] == grid[i][j] && doubleCheck(i+1, j, 3)) {
			remove.add(block[i+1][j]);
		}

		System.out.println(remove.size()+" "+remove);

		if(remove.size() > 0) {
			Block[] toAnimate = new Block[remove.size()];
			for (int k = 0; k < remove.size(); k++) {
				toAnimate[k] = new Block(remove.get(k).getX(), remove.get(k).getY(), remove.get(k).getType());
				//grid[remove.get(k).getX()][remove.get(k).getY()] = BlockType.ROAD;
			}
			animate(i,j,toAnimate);
			grid[i][j] = grid[i][j].getNext();
			score += 10*remove.size()*grid[i][j].getScore();
		}


	}

	public boolean doubleCheck(int x, int y, int dir){
		int startingSize = remove.size();

		if(dir!= 0 && y>= 1 && grid[x][y-1] == grid[x][y]) remove.add(block[x][y-1]);
		if(dir!= 2 && y<=grid[x].length -2 && grid[x][y+1] == grid[x][y]) remove.add(block[x][y+1]);
		if(dir!= 3 && x>= 1 && grid[x-1][y] == grid[x][y]) remove.add(block[x-1][y]);
		if(dir!= 1 && x<=grid.length -2 && grid[x+1][y] == grid[x][y]) remove.add(block[x+1][y]);

		if(startingSize - remove.size() == 0) return false;
		else return true;
	}

	public void updateBears(){
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if(grid[i][j] == BlockType.BEAR){
					//Block[] toAnimate = new Block[1];
					//toAnimate[0] = grid[i][j];
					
					int d = BlockType.RANDOM.nextInt(3);

					switch (d) {
					case 0: 
						if(j>1 && grid[i][j-1] == BlockType.ROAD){
							grid[i][j] = BlockType.ROAD;
							grid[i][j-1]=BlockType.BEAR;
							//animate(i,j-1,toAnimate);
						}
						break;
					case 1: 
						if(i<=grid.length -2 && grid[i+1][j] == BlockType.ROAD){
							grid[i][j] = BlockType.ROAD;
							grid[i+1][j] = BlockType.BEAR;
							//animate(i+1,j,toAnimate);
						}
						break;
					case 2: 
						if(j<=grid[i].length -2  && grid[i][j+1] == BlockType.ROAD){
							grid[i][j] = BlockType.ROAD;
							grid[i][j+1] = BlockType.BEAR;
							//animate(i,j+1,toAnimate);
						}
						break;
					case 3: 
						if(i>1 && grid[i-1][j] == BlockType.ROAD){
							grid[i][j] = BlockType.ROAD;
							grid[i-1][j] = BlockType.BEAR;
							//animate(i-1,j,toAnimate);
						}
						break;
					default:break;
					}
					
				}
			}
		}
	}

	/**
	 * Starts the whole thing going.
	 * @param args
	 */
	public static void main(String[] args) {

		JFrame.setDefaultLookAndFeelDecorated(true);


		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Triple();

			}
		});

	}


	@Override
	public void mousePressed(MouseEvent e) {
		mouseButton=e.getButton();
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		Point temp = e.getPoint();


		int x = (int)temp.getX()*grid.length/viewer.getWidth();
		int y = (int)temp.getY()*grid.length/viewer.getHeight();
		
		System.out.println(grid[x][y]);

		if(grid[x][y] == BlockType.ROAD) {
			grid[x][y] = next;
			int prevScore = -1;
			while(score != prevScore){
				prevScore = score;
				checkGrid(x,y);

			}
		}else{
			if(next == BlockType.ROBOT) grid[x][y] = BlockType.ROAD;
		}

		randomBlock(0.8);
		//next=BlockType.BEAR;

		updateBears();

		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() ==82){
			resetGrid();
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	//unused interface methods
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

}
