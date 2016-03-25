import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class TitleScene extends Scene {

	private BufferedImage image;
	private final JFrame window;
	private int count = 0;

	public TitleScene(JFrame window) {
		this.window = window;
		try {
			image = ImageIO.read(TitleScene.class.getResource("title.png"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	@Override
	Scene mainLoop(Graphics2D g) {
		Insets in = window.getInsets();
		g.drawImage(image, in.left, in.top, window.getWidth() - (in.left + in.right), window.getHeight() - (in.top + in.bottom), null);

		final int maxCount = 100;
		if (count > 1) {
			count++;
			if (count == maxCount) return new GameScene(window);
			Color color = new Color(0,0,0, 0xff * Math.min(count, 80) / 80);
			g.setColor(color);
			g.fillRect(in.left, in.top, window.getWidth() - (in.left + in.right), window.getHeight() - (in.top + in.bottom));
		}

		return this;
	}

	@Override
	void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) count++;
	}
}
