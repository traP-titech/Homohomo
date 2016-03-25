import java.awt.Color;
import java.awt.Graphics2D;

public class Element {

	public enum Type {RED, GREEN, YELLOW};

	public int x, y;
	public int gx, gy;
	public Type type;
	private final Stage stage;

	public Element(Stage stage, int x, int y) {
		this.stage = stage;
		this.x = x;
		this.y = y;
		this.gx = getX(x);
		this.gy = getY(y);
		this.type = Utils.randomSelect(Type.values());
	}

	void step() {
		if(isFalling){
			gy += vy;
			if(gy >= target_y){
				gy = target_y;
				isFalling = false;
			}
		}else if(isLifting){
			gy += vy;
			if(gy <= target_y){
				gy = target_y;
				isLifting = false;
			}
		}
	}

	void draw(Graphics2D g) {

		Color color = null;
		switch (type) {
		case RED:
			color = Color.RED;
			break;
		case GREEN:
			color = Color.GREEN;
			break;
		case YELLOW:
			color = Color.YELLOW;
			break;
		}
		
		if(killed) color = Color.WHITE;
		g.setColor(color);
		g.fillOval(gx, gy, stage.elementSize, stage.elementSize);
	}
	
	// 消す
	int target_y = -1;
	int vy = 0;
	public boolean isErasing = false;
	public boolean killed = false;
	public void erase(){
		killed = true;
	}
	
	// 落とす
	public boolean isFalling = false;
	public void fall(int y){
		this.y = y;
		target_y = getY(y);
		vy = 3;
		isFalling = true;
	}
	
	// 上げる
	public boolean isLifting = false;
	public void lift(){
		target_y = getY(y-1);
		vy = -3;
		isLifting = true;
	}
	
	public Type getNextColor(){
		switch (type) {
		case RED:
			return Type.GREEN;
		case GREEN:
			return Type.YELLOW;
		case YELLOW:
			return Type.RED;
		default:
			return null;
		}
	}

	int getX(int x) { //左上座標
		return stage.window.getWidth() / 2 - stage.elementAreaWidth / 2 + stage.elementSize * x;
	}
	
	int getY(int y) {
		if (y == stage.height-1) return stage.window.getHeight() - stage.elementAreaHeight + stage.elementSize * y - 20;
		return stage.window.getHeight() - stage.elementAreaHeight + stage.elementSize * y - 30;
	}
}