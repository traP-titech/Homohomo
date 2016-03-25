import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import java.text.DecimalFormat;

public class Stage {

	public final int elementAreaHeight = 480;
	public final int elementAreaWidth;

	public final JFrame window;
	public final int width, height;
	private final Element[][] elements;

	int mx, my;
	int elementSize;
	Font englishFont, englishFontLarge, japaneseFont, japaneseFontSmall, japaneseFontLarge;
	DecimalFormat scoreFormat;
	long score = 931L;

	public Stage(JFrame window, int width, int height) {
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

		englishFont = Utils.createFont("helsinki.ttf").deriveFont(Font.PLAIN, 35);
		englishFontLarge = Utils.createFont("helsinki.ttf").deriveFont(Font.PLAIN, 54);
		japaneseFont = Utils.createFont("yasashisa_bold.ttf").deriveFont(Font.PLAIN, 27);
		japaneseFontSmall = Utils.createFont("yasashisa_bold.ttf").deriveFont(Font.PLAIN, 21);
		japaneseFontLarge = Utils.createFont("yasashisa_bold.ttf").deriveFont(Font.PLAIN, 32);
		
		scoreFormat = new DecimalFormat("00000000");
	}

	void step() {
		//エレメント更新
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				elements[i][j].step();
			}
		}
	}

	void draw(Graphics2D g) {

		//UI描画
		g.setColor(new Color(100, 100, 100, 100));
		g.fillRoundRect(
				window.getWidth() / 2 - elementAreaWidth / 2 - 10,
				window.getHeight() - elementAreaHeight - 30,
				elementAreaWidth + 20,
				elementAreaHeight + 20,
				10,
				10
				);	// メイン
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1.5f));
		g.drawLine(
				window.getWidth() / 2 - elementAreaWidth / 2,
				window.getHeight() - elementSize - 23,
				window.getWidth() / 2 + elementAreaWidth / 2,
				window.getHeight() - elementSize - 23
				);	// メインのNEXTとのセパレータ
		g.setColor(new Color(252, 181, 96));
		g.fillRoundRect(
				window.getWidth() / 2 - elementAreaWidth / 2 - 10,
				45,
				elementAreaWidth + 20,
				40,
				20,
				20
				);	// 上のバー
		g.fillRoundRect(15,
				window.getHeight() - elementAreaHeight + 50,
				190, 350, 20, 20);	// 左ボックス

		g.fillRoundRect(595,
				window.getHeight() - elementAreaHeight + 50,
				190, 350, 20, 20);	// 右ボックス

		g.setColor(new Color(247, 155, 55));
		g.fillRoundRect(22,
				window.getHeight() - elementAreaHeight + 290,
				175, 46, 20, 20);	// 左ボックス特殊上
		g.fillRoundRect(22,
				window.getHeight() - elementAreaHeight + 345,
				175, 46, 20, 20);	// 左ボックス特殊下
		g.fillRoundRect(603,
				window.getHeight() - elementAreaHeight + 290,
				175, 46, 20, 20);	// 右ボックス特殊上
		g.fillRoundRect(603,
				window.getHeight() - elementAreaHeight + 345,
				175, 46, 20, 20);	// 右ボックス特殊下
		g.fillRoundRect(603,
				window.getHeight() - elementAreaHeight + 410,
				175, 46, 20, 20);	// 右下ポーズボタン
		g.setColor(new Color(250, 110, 30));
		g.drawRoundRect(
				window.getWidth() / 2 - elementAreaWidth / 2 - 10,
				45,
				elementAreaWidth + 20,
				40,
				20,
				20
				);
		g.drawRoundRect(15,
				window.getHeight() - elementAreaHeight + 50,
				190, 350, 20, 20);

		g.drawRoundRect(595,
				window.getHeight() - elementAreaHeight + 50,
				190, 350, 20, 20);

		g.drawRoundRect(22,
				window.getHeight() - elementAreaHeight + 290,
				175, 46, 20, 20);
		g.drawRoundRect(22,
				window.getHeight() - elementAreaHeight + 345,
				175, 46, 20, 20);
		g.drawRoundRect(603,
				window.getHeight() - elementAreaHeight + 290,
				175, 46, 20, 20);
		g.drawRoundRect(603,
				window.getHeight() - elementAreaHeight + 345,
				175, 46, 20, 20);
		g.drawRoundRect(603,
				window.getHeight() - elementAreaHeight + 410,
				175, 46, 20, 20);
		g.setFont(englishFont);
		g.setColor(Color.BLACK);
		g.drawString("NEXT",
				window.getWidth() / 2 - elementAreaWidth / 2 - 110,
				window.getHeight() - elementSize + 15);
		g.drawString(scoreFormat.format(score),
				window.getWidth() / 2 + elementAreaWidth / 2 + 30,
				window.getHeight() - elementAreaHeight + 10);
		g.setFont(englishFontLarge);
		g.drawString("10"/*残りターン数*/,
				40,
				window.getHeight() - elementAreaHeight + 20);
		g.setFont(japaneseFontSmall);
		g.drawString("すこあ",
				window.getWidth() / 2 + elementAreaWidth / 2 + 30,
				window.getHeight() - elementAreaHeight - 30
				);
		g.drawString("のこり",
				40,
				window.getHeight() - elementAreaHeight - 30
				);
		g.drawString("ターン",
				100,
				window.getHeight() - elementAreaHeight + 20
				);
		g.setFont(japaneseFont);
		g.drawString("ポーズ",
				window.getWidth() / 2 + elementAreaWidth / 2 + 70,
				window.getHeight() - 40
				);

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
		g.setColor(new Color(100, 100, 100, 100));
		g.fillRect(window.getWidth()/2 - elementAreaWidth / 2, window.getHeight() - elementAreaHeight + elementSize * (height-1) - 10, elementAreaWidth, elementSize);
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
