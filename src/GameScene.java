import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

public class GameScene extends Scene {

	private final Stage stage;
	private GameStartEffect startEffect;
	private int phase = 0;
	private int phaseCount = 0;
	private final JFrame window;

	GameScene(JFrame window) {
		stage = new Stage(window, 8, 11);
		startEffect = new GameStartEffect(window);
		this.window = window;
	}

	@Override
	Scene mainLoop(Graphics2D g) {
		phase = 5;
		stage.draw(g);
		switch (phase) {
		case 0: {
			final int MAX_COUNT = 20;
			phaseCount++;
			if (phaseCount == MAX_COUNT) {
				phaseCount = 0;
				phase++;
			}
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, window.getWidth(), window.getHeight());
		} break;
		case 1:{
			final int MAX_COUNT = 100;
			g.setColor(new Color(0,0,0, 0xff * (MAX_COUNT - phaseCount + 100) / (MAX_COUNT + 100)));
			g.fillRect(0, 0, window.getWidth(), window.getHeight());
			phaseCount++;
			if (phaseCount == MAX_COUNT) {
				phaseCount = 0;
				phase++;
				break;
			}
		} break;
		case 2: {
			final int MAX_COUNT = 20;
			phaseCount++;
			if (phaseCount == MAX_COUNT) {
				phaseCount = 0;
				phase++;
			}
			g.setColor(new Color(0,0,0, 0xff / 2));
			g.fillRect(0, 0, window.getWidth(), window.getHeight());
		} break;
		case 3: {
			final int MAX_COUNT = 100;
			phaseCount++;
			if (phaseCount == MAX_COUNT) {
				phaseCount = 0;
				phase++;
			}
			g.setColor(new Color(0,0,0, 0xff / 2));
			g.fillRect(0, 0, window.getWidth(), window.getHeight());
			startEffect.draw(g);
			startEffect.step();
		} break;
		case 4: {
			final int MAX_COUNT = 20;
			phaseCount++;
			if (phaseCount == MAX_COUNT) {
				phaseCount = 0;
				phase++;
			}
			g.setColor(new Color(0,0,0, 0xff / 2 - 0xff / 2 * phaseCount / MAX_COUNT));
			g.fillRect(0, 0, window.getWidth(), window.getHeight());
			startEffect.draw(g);
			startEffect.step();
		} break;
		case 5: {
			stage.step();
		} break;
		}
		return this;
	}

	@Override
	void mousePressed(MouseEvent e) {
		stage.mousePressed(e);
	}

	@Override
	void mouseDragged(MouseEvent e) {
		stage.mouseDragged(e);
	}
}
