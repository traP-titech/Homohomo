import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class PauseEffect {

	Color color = new Color(100, 100, 100, 200);
	int x, y, w, h;
	int count = 0;
	int phase = 5;
	int alpha;
	Button button;
	Font font;

	PauseEffect(Button button) {
		x = button.x;
		y = button.y;
		w = button.w;
		h = button.h;
		this.button = button;
		this.font = Utils.createFont("helsinki.ttf").deriveFont(Font.PLAIN, 100);
	}

	void step() {
		alpha = 0;
		switch (phase) {
		case 0:{
			final float MAX_COUNT = 20.0F;
			float t = count / MAX_COUNT;
			t = t * t * (3 - 2 * t);
			h = (int)(button.h * (1-t) + button.h * 5 * t);
			y = button.y + button.h / 2 - h / 2;
			w = (int)(button.w * (1-t) + button.w / 5 * t);
			x = button.x + button.w / 2 - w / 2;
			count++;
			if (count == MAX_COUNT) {
				count = 0;
				phase++;
			}
		} break;
		case 1: {
			final float MAX_COUNT = 20;
			float t = count / MAX_COUNT;
			t = t * t * (3 - 2 * t);
			h = (int)(button.h * 5 * (1-t) + 600 * t);
			y = (int)((button.y - button.h * 2) * (1-t));
			w = (int)(button.w / 5 * (1-t) + 800 * t);
			x = (int)((button.x + button.w * 2 / 5) * (1-t));
			count++;
			if (count == MAX_COUNT) {
				count = 0;
				phase++;
			}
		} break;
		case 2: {
			count++;
			alpha = (int)((Math.sin(count * 0.05) + 1) * 0xff / 2);
		} break;
		case 3: {
			final float MAX_COUNT = 20;
			float t = count / MAX_COUNT;
			t = t * t * (3 - 2 * t);
			t = 1 - t;
			h = (int)(button.h * 5 * (1-t) + 600 * t);
			y = (int)((button.y - button.h * 2) * (1-t));
			w = (int)(button.w / 5 * (1-t) + 800 * t);
			x = (int)((button.x + button.w * 2 / 5) * (1-t));
			count++;
			if (count == MAX_COUNT) {
				count = 0;
				phase++;
			}
		} break;
		case 4: {
			final float MAX_COUNT = 20.0F;
			float t = count / MAX_COUNT;
			t = t * t * (3 - 2 * t);
			t = 1 - t;
			h = (int)(button.h * (1-t) + button.h * 5 * t);
			y = button.y + button.h / 2 - h / 2;
			w = (int)(button.w * (1-t) + button.w / 5 * t);
			x = button.x + button.w / 2 - w / 2;
			count++;
			if (count == MAX_COUNT) {
				count = 0;
				phase++;
			}
		} break;
		}
	}

	void draw(Graphics2D g) {
		if (phase == 5) return;
		g.setColor(color);
		g.fillRect(x, y, w, h);
		g.setColor(new Color(0xff, 0xff, 0xff, alpha));
		g.setFont(font);
		String text = "Pause...";
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, 400 - fm.stringWidth(text) / 2, 300);
	}
	
	void start() {
		phase = 0;
		count = 0;
	}
	
	void end() {
		phase = 3;
		count = 0;
	}
}
