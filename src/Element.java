import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Element {

	private enum Type {RED, GREEN, YELLOW, BLUE};

	private final int x, y;
	private final Type type;
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
		this.type = Utils.randomSelect(Type.values());
	}

	void step() {

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

		g.drawImage(image, getX(), getY(), stage.elementSize, stage.elementSize, null);
	}

	int getX() { //左上座標
		return stage.window.getWidth() / 2 - stage.elementAreaWidth / 2 + stage.elementSize * x;
	}

	int getY() {
		if (y == stage.height-1) return stage.window.getHeight() - stage.elementAreaHeight + stage.elementSize * y - 10;
		return stage.window.getHeight() - stage.elementAreaHeight + stage.elementSize * y - 20;
	}
}