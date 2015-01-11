package tripleGame;

import java.awt.Graphics;
import java.awt.Image;

public class Block {
	
	public static final int WIDTH = 67;

	private BlockType type = BlockType.ROAD;
	private double x,y;
	private double vx,vy;
	private Sprite sprite;
	
	public Block(double x, double y, BlockType type){
		this.x = x;
		this.y = y;
		this.vx = 0;
		this.vy = 0;
		this.type = type;
		sprite = SpriteStore.get().getSprite(type.getSprite());
	}
	
	public void draw(Graphics g){
		sprite.draw(g, (int)x, (int)y);
	}
	
	public BlockType getType(){
		return type;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setType(BlockType type) {
		this.type = type;
		sprite = SpriteStore.get().getSprite(type.getSprite());
	}
	
	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void setVelocity(double x, double y){
		this.vx = x;
		this.vy = y;
	}
	
	public Image getSprite(){
		return sprite.getImage();
	}
	
	public void updatePos(){
		this.x += this.vx;
		this.y += this.vy;
	}
}
