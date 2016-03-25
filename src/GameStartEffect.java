import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class GameStartEffect {

	BufferedImage image;
	double y;
	double vy;
	int width, height;
	JFrame window;
	int phase = 0;

	public GameStartEffect(JFrame window) {
		this.window = window;
		y = -100;
	}

	void init(Graphics2D graphics) {
		Font font = Utils.createFont("helsinki.ttf").deriveFont(Font.PLAIN, 100);
		graphics.setFont(font);
		FontMetrics fm = graphics.getFontMetrics();
		String text = "START!";
		image = new BufferedImage(width = fm.stringWidth(text), height = fm.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D)image.createGraphics();
		g.setColor(Color.RED);
		g.setFont(font);
		g.drawString(text, 0, 90);
	}

	void step() {
		vy += .5;
		y += vy;
		
		if (phase == 0) {
			if (y > 300) {
				phase++;
				vy *= -0.5;
			}
		}
	}

	void draw(Graphics2D g) {
		if (image == null) init(g);
		g.drawImage(image, window.getWidth() / 2 - width / 2, (int)y, width, height, null);
	}
}
