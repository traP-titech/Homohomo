import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

class Bakuhatsu{
	BufferedImage img;
	Bakuhatsu(){
		try {
			img = ImageIO.read(Main.class.getResource("bomb.png"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	void draw(Graphics2D g){
		g.drawImage(img,100,100,null);
	}
	void step(){
		
	}
}
public class Element {
	
	public boolean endcheck=true;
	public boolean erase = false;
	public int specolor = 0;

	private enum Type {RED, GREEN, YELLOW,BLUE};

	private final int x, y;
	private final Type type;
	private final Stage stage;

	public Element(Stage stage, int x, int y) {
		this.stage = stage;
		this.x = x;
		this.y = y;
		this.type = Utils.randomSelect(Type.values());
	}
	void erase(){
		erase = true;
	}
	void step() {
		if(endcheck = false){
			
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
		case BLUE:
			color = Color.BLUE;
			break;
		}
		if(specolor == 1){
			color = Color.BLACK;
		}else if(specolor == 2){
			color = Color.WHITE;
		}

		g.setColor(color);
		g.fillOval(getX(), getY(), stage.elementSize, stage.elementSize);
	}

	int getX() { //左上座標
		return stage.window.getWidth() / 2 - stage.elementAreaWidth / 2 + stage.elementSize * x;
	}
	
	int getY() {
		if (y == stage.height-1) return stage.window.getHeight() - stage.elementAreaHeight + stage.elementSize * y - 20;
		return stage.window.getHeight() - stage.elementAreaHeight + stage.elementSize * y - 30;
	}
}