import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public class Button {

	public int x, y, w, h, aw, ah;
	private static Color fillColor = new Color(247, 155, 55);
	private static Color drawColor = new Color(250, 110, 30);

	private boolean mouseOver;
	private int count = 0;
	private int expand = 0;
	private final boolean clickable;
	private final String text;
	private final Font font;
	private ButtonListener listener;

	Button(int x, int y, int w, int h, int aw, int ah, boolean clickable, String text, Font font) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.aw = aw;
		this.ah = ah;
		this.clickable = clickable;
		this.text = text;
		this.font = font;
	}

	void setListener(ButtonListener listener) {
		this.listener = listener;
	}

	void draw(Graphics2D g) {
		g.setColor(fillColor);
		g.fillRoundRect(x - expand, y - expand, w + 2 * expand, h + 2 * expand, aw, ah);
		g.setColor(drawColor);
		g.drawRoundRect(x - expand, y - expand, w + 2 * expand, h + 2 * expand, aw, ah);

		g.setColor(Color.BLACK);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, x + w / 2 - fm.stringWidth(text) / 2, y + h / 2 + font.getSize()/2);
	}

	void step() {
		if (mouseOver) {
			count++;
			if (count > 10) count = 10;
		} else {
			count--;
			if (count < 0) count = 0;
		}
		expand = count * 2 / 3;
	}

	void mouseMoved(MouseEvent e) {
		mouseOver = contains(e.getX(), e.getY()) && clickable;
	}

	void mousePressed(MouseEvent e) {
		if (mouseOver == false) return;
		if (e.getButton() != MouseEvent.BUTTON1) return;
		if (listener == null) return;
		listener.onPressed();
	}

	boolean contains(int px, int py) {
		return x <= px && px <= x + w
				&& y <= py && py <= y + h;
	}
}

interface ButtonListener {
	void onPressed();
}
