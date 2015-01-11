package tripleGame;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum BlockType {
	ROAD("res/road.gif","ROAD",0), 
	GRASS("res/grass.gif","BUSH",10),
	BUSH("res/bush.gif","TREE",50), 
	TREE("res/tree.gif","HUT",100), 
	HUT("res/hut.gif","HUT",250), 
	ROBOT("res/road.gif","ROAD",0),
	BEAR("res/bear.gif","CHURCH", 100),
	CHURCH("res/church.gif", "CHURCH", 250);

	//Fields that each blockType has
	private String filename;
	private String next;
	private int score;
	
	//Constants
	private static final List<BlockType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();
	public static final Random RANDOM = new Random();

	//Constructor
	private BlockType(String sprite, String next, int c){
		this.filename = sprite;
		this.next = next;
		this.score = c;
	}

	//Next type when upgraded
	public BlockType getNext(){
		return BlockType.valueOf(next);
	}

	//Randomly select an enum
	public static BlockType randomLetter()  {
		BlockType t= VALUES.get(RANDOM.nextInt(SIZE));
		
		//Don't randomly select Road, Church, etc.
		while(t == BlockType.ROAD || t == BlockType.CHURCH){
			t = VALUES.get(RANDOM.nextInt(SIZE));
		}
		
		return t;
	}
	
	//get the sprite location
	public String getSprite() {
		return filename;
	}
	
	public int getScore(){
		return score;
	}
	
}
