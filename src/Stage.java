import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

public class Stage {

	public final int elementAreaHeight = 400;
	public final int elementAreaWidth;

	public final JFrame window;
	public final int width, height;
	private final Element[][] elements;

	int mx, my;
	int elementSize;
	public Bakuhatsu baku;
	int ballcount=0;

	public Stage(JFrame window, int width, int height) {
		baku = new Bakuhatsu();
		this.window = window;
		this.width = width;
		this.height = height;
		this.elements = new Element[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				elements[i][j] = new Element(this, i, j);
			}
		}

		elementAreaWidth = elementAreaHeight * width / height;
		elementSize = elementAreaHeight / height;
	}

	void mainLoop(Graphics2D g) {
		step();
		draw(g);
	}

	void step() {
		//エレメント更新
		
		System.out.println(ballcount);
		if(ballcount>=60){
			ballcount = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height-1; j++) {
					elements[i][j] = elements[i][j+1];
				}
			}
		}else{
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					elements[i][j].step();
				}
			}
		}
	}
	int bombtimer=60;
	void draw(Graphics2D g) {
		bombtimer--;
		//エレメント描画
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				elements[i][j].draw(g);
			}
		}

		//マウス座標表示
		g.setColor(Color.RED);
		g.fillOval(mx-5, my-5, 10, 10);

		//灰色フィルター
		g.setColor(new Color(100, 100, 100,100));
		g.fillRect(window.getWidth()/2 - elementAreaWidth / 2, window.getHeight() - elementAreaHeight + elementSize * (height-1) - 20, elementAreaWidth, elementSize);
		if(bombtimer <= 1){
			bombtimer = 60;
		}
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,bombtimer/60f));
		baku.draw(g);
	}

	void mousePressed(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}

	void mouseDragged(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}

//	Element selectElement(int mx, int my) {
//		int x = (int)((mx - (elementAreaWidth - window.getWidth()) / 2) / elementSize);
//		int y = 0;
//		return
//	}

}
