package tripleGame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class TripleUserPanel extends JPanel {
	
	//create a controller
	private Triple controller;
	
	//create the GUI buttons, sliders and labels
	private JButton startStop = new JButton("START");
	private JButton reset = new JButton("RESET");
	private JLabel score = new JLabel("Score: 0000");
	
	
	//set up a bunch of colours for making things look nice
	public static Color redRidingHood = new Color(209,72,54);
	public Color darkGray = new Color(50, 50, 50);
	public Color lightGray = new Color(250,250,250);
	public Color gray = new Color(200,200,200);

	/**
	 * Constructor for the user panel. Adds all components to the panel and adds listeners and styling
	 * @param attachedTo
	 */
	public TripleUserPanel(Triple attachedTo) {

		controller = attachedTo;
		setOpaque(true);
		setBackground(Color.WHITE);
		//Size of the left hand user panel
		setPreferredSize(new Dimension(100,400));
		
		//Call the start and stop methods
		startStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller.running() == true){
					//controller.stopSimulation();
				}else{
					//controller.startSimulation();
				}
				
			}
		});
		
		//calls the reset method
		reset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(controller.running()==false){
					controller.resetGrid();
				}
			}
		});
		
				
				
		//Start-stop button style including dimensions and colours
		startStop.setBackground(redRidingHood);
		startStop.setForeground(Color.WHITE);
		startStop.setPreferredSize(new Dimension(80, 35));
		startStop.setFocusable(true);
		
		//Reset button style including dimensions and colours
		reset.setBackground(darkGray);
		reset.setPreferredSize(new Dimension(80,35));
		reset.setForeground(Color.WHITE);
		reset.setFocusable(false);
		
	

		//Add all of the elements to the panel in correct order
		add(startStop);
		add(reset);
		add(score);
		
	}
	
	public void updateScore(int s){
		String format = String.format("%%0%dd", 4);
		String result = String.format(format, s);
		score.setText("Score: "+result);
	}
	
	
	/**
	 * Called when simulation started to change most elements to non editable. Changes start-stop text
	 * and gridsize in case user has not pressed enter on gridsize (to avoid confusion)
	 */
	public void simulationStarted(){
		startStop.setText("STOP");
		reset.setEnabled(false);
	}
	
	/**
	 * Called when simulation is stopped to change most elements to editable. Changes start-stop text
	 * 
	 */
	public void simulationStopped(){
		startStop.setText("START");
		reset.setEnabled(true);
	}


}
