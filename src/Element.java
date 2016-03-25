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

	public enum Type {RED, GREEN, YELLOW, BLUE};

	public int x, y;
	public int gx, gy;
	public Type type;
	private final Stage stage;
	private static final BufferedImage[] images;
	
	static {
		images = new BufferedImage[6];
		try {
			images[0] = ImageIO.read(Main.class.getResource("bomb_red.png"));
			images[1] = ImageIO.read(Main.class.getResource("bomb_green.png"));
			images[2] = ImageIO.read(Main.class.getResource("bomb_yellow.png"));
			images[3] = ImageIO.read(Main.class.getResource("bomb_blue.png"));
			images[4] = ImageIO.read(Main.class.getResource("bomb_white.png"));
			images[5] = ImageIO.read(Main.class.getResource("bomb_rock.png"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public Element(Stage stage, int x, int y) {
		this.stage = stage;
		this.x = x;
		this.y = y;
		this.gx = getX(x);
		this.gy = getY(y);
		this.type = Utils.randomSelect(Type.values());
	}
	void step() {
		if(isErasing){
			++erasecnt;
			if(erasecnt >= 10){
				isErasing = false;
			}
		}else if(isFalling){
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
		if(endcheck = false){
			
		}
	}

	void draw(Graphics2D g) {

		BufferedImage image = null;
		switch (type) {
		case RED:
			image = images[0];
			break;
		case GREEN:
			image = images[1];
			break;
		case YELLOW:
			image = images[2];
			break;
		case BLUE:
			image = images[3];
			break;
		}
		if(specolor == 1){
			image = images[5];
		}else if(specolor == 2){
			image = images[4];
		}

		if(!killed) g.drawImage(image, gx, gy, stage.elementSize, stage.elementSize, null);
	}
	
	// 消す
	int target_y = -1;
	int vy = 0;
	int erasecnt;
	public boolean isErasing = false;
	public boolean killed = false;
	public void erase(){
		erasecnt = 0;
		isErasing = true;
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
			return Type.YELLOW;
		case GREEN:
			return Type.BLUE;
		case YELLOW:
			return Type.GREEN;
		case BLUE:
			return Type.RED;
		default:
			return null;
		}
	}

	int getX(int x) { //左上座標
		return stage.window.getWidth() / 2 - stage.elementAreaWidth / 2 + stage.elementSize * x;
	}
	
	int getY(int y) {
		if (y == stage.height-1) return stage.window.getHeight() - stage.elementAreaHeight + stage.elementSize * y - 10;
		return stage.window.getHeight() - stage.elementAreaHeight + stage.elementSize * y - 20;
	}
}