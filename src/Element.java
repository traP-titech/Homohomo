import java.awt.Color;
import java.awt.Graphics2D;

public class Element {

	private enum Type {RED, GREEN, YELLOW};

	private final int x, y;
	private final Type type;
	private final Stage stage;

	public Element(Stage stage, int x, int y) {
		this.stage = stage;
		this.x = x;
		this.y = y;
		this.type = Utils.randomSelect(Type.values());
	}

	void step() {

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