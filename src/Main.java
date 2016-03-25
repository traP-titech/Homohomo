import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class Main implements MouseListener, MouseMotionListener, KeyListener {
	public static void main(String[] args) {
		new Main();
	}

	private final JFrame window;
	private final BufferStrategy buffer;
	private Stage stage;

	public Main() {
		window = new JFrame("ホモホモ");
		window.setSize(800, 600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.createBufferStrategy(2);
		window.addKeyListener(this);
		window.addMouseListener(this);
		window.addMouseMotionListener(this);
		buffer = window.getBufferStrategy();
		stage = new Stage(window, 10, 11);

		new Timer().schedule(new TimerTask(){

			@Override
			public void run() {
				if (buffer.contentsLost()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				} else {
					Graphics2D g = (Graphics2D)buffer.getDrawGraphics();
					Insets in = window.getInsets();
//					g.translate(in.left, in.top);
					mainLoop(g);
					g.dispose();
					buffer.show();
				}
			}

		}, 0, 17);
	}

	void mainLoop(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, window.getWidth(), window.getHeight());
		stage.mainLoop(g);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		stage.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		stage.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
