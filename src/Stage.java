import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Stage {

	public final int elementAreaHeight = 480;
	public final int elementAreaWidth;

	public final JFrame window;
	public final int width, height;
	private final Element[][] elements;
	
	private static BufferedImage bury;
	static{
		try{
			bury = ImageIO.read(Main.class.getResource("bury.png"));			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private enum State{
		WAITING,
		ERASING,
		FALLING,
		LIFTING,
		AFTER_EFFECT
	}
	private State state;

	boolean pauseFlag = false;
	boolean clicked = false;
	int mx, my;
	int elementSize;
	Font englishFont, japaneseFont;
	
	static final int LIFT_REM_TURN = 5;
	int liftRemainTurn;
	
	static final int PLAYER_HP_MAX = 100;
	int playerHP;
	
	static final int ENEMY_HP_MAX = 100;
	int enemyHP;
	
	long score = 1145141919810L;

	Button[] buttons = new Button[5];
	private PauseEffect pauseEffect;
	int ballcount=0;

	public Stage(JFrame window, int width, int height) {
		this.window = window;
		this.width = width;
		this.height = height;
		this.elements = new Element[width][height];
		this.topelements = new Element[width];

		elementAreaWidth = elementAreaHeight * width / height;
		elementSize = elementAreaHeight / height;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				elements[i][j] = new Element(this, i, j);
			}
		}
		state = State.WAITING;
		clicked = false;

		liftRemainTurn = LIFT_REM_TURN;
		playerHP = PLAYER_HP_MAX;
		enemyHP = ENEMY_HP_MAX;
		score = 0;

		englishFont = Utils.createFont("helsinki.ttf").deriveFont(Font.PLAIN, 35);
		japaneseFont = Utils.createFont("yasashisa_bold.ttf").deriveFont(Font.PLAIN, 27);

		buttons[0] = new Button(22,
				window.getHeight() - elementAreaHeight + 295,
				175, 46, 20, 20, true, "A", japaneseFont);

		buttons[1] = new Button(22,
				window.getHeight() - elementAreaHeight + 350,
				175, 46, 20, 20, true, "B", japaneseFont);

		buttons[2] = new Button(603,
				window.getHeight() - elementAreaHeight + 295,
				175, 46, 20, 20, false, "C", japaneseFont);

		buttons[3] = new Button(603,
				window.getHeight() - elementAreaHeight + 350,
				175, 46, 20, 20, false, "D", japaneseFont);

		buttons[4] = new Button(603,
				window.getHeight() - elementAreaHeight + 410,
				175, 46, 20, 20, true, "ポーズ", japaneseFont);

		buttons[4].setListener(new ButtonListener() {

			@Override
			public void onPressed() {
				pauseFlag = true;
				pauseEffect.start();
			}
		});

		pauseEffect = new PauseEffect(buttons[4]);
	}

	Vector<Vector<Integer>> eraselist;
	int puzzleBFS(int x, int y){
		int[][] visited = new int[width][height-1];
		for(int i=0;i<width;++i)for(int j=0;j<height-1;++j)visited[i][j]=-1;
		Queue<int[]> q = new ArrayDeque<int[]>();
		visited[x][y] = 0;
		q.add(new int[]{x,y});
		int[] dx = new int[]{1,0,-1,0};
		int[] dy = new int[]{0,1,0,-1};
		int res = 0;
		while(q.peek() != null){
			int[] p = q.poll();
			int val = visited[p[0]][p[1]];
			Element.Type nextType = elements[p[0]][p[1]].getNextColor();
			for(int i=0;i<4;++i){
				int nx = p[0] + dx[i];
				int ny = p[1] + dy[i];
				if(0<=nx && nx<width && 0<=ny && ny<height-1 &&
					visited[nx][ny] == -1 && elements[nx][ny].type == nextType){
					visited[nx][ny] = val+1;
					res = val+1;
					q.add(new int[]{nx,ny});
				}
			}
		}
		eraselist = new Vector<Vector<Integer>>();
		eraselist.setSize(res+1);
		for(int i=0;i<=res;++i){
			eraselist.set(i, new Vector<Integer>());
		}
		for(int i=0;i<width;++i)for(int j=0;j<height-1;++j){
			int val = visited[i][j];
			if(val!=-1){
				eraselist.get(val).addElement(i*height+j);
			}
		}
		return res;
	}

	int erasemax;
	int eraseiter;

	Element[] topelements;
	
	int erasecnt;
	int flowcnt;
	
	int progress;
	
	int moto_playerHP;
	int moto_enemyHP;
	long moto_score;

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
		// フェーズ分け
		boolean flag = true;
		switch (state) {
		case WAITING:
			if(clicked){
				int id = selectElement(mx,my);
				if(id!=-1){
					int x = id/height;
					int y = id%height;
					// erase calculation
					erasemax = puzzleBFS(x, y)+1;
					eraseiter = -1;
					// phase proceed
					state = State.ERASING;
				}
			}
			break;
		case ERASING:
			// 消去が終わってるかチェック
			for(int i=0;i<width;++i)for(int j=0;j<height-1;++j)flag &= !elements[i][j].isErasing;
			if(flag){
				eraseiter++;
				if(eraseiter == erasemax){
					erasecnt = 0;
					for(int i=0;i<width;++i){
						int s = height-1;
						for(int j=height-1;j>=0;--j){
							if(!elements[i][j].killed){
								elements[i][s] = elements[i][j];
								--s;
							}else if(!(elements[i][j] instanceof NullElement)){
								++erasecnt;
							}
						}
						for(int j=s;j>=0;--j){
							elements[i][j] = new NullElement(this,i,j);
						}
					}
					for(int i=0;i<width;++i)for(int j=0;j<height-1;++j)elements[i][j].fall(j);
					state = State.FALLING;
				}else{
					int sz = eraselist.get(eraseiter).size();
					for(int i=0;i<sz;++i){
						int id = eraselist.get(eraseiter).get(i);
						int x = id/height;
						int y = id%height;
//						System.out.println("("+x+","+y+")");
						elements[x][y].erase();
					}
				}
			}
			break;
		case FALLING:
			// 落下が終わってるかチェック
			for(int i=0;i<width;++i)for(int j=0;j<height-1;++j)flag &= !elements[i][j].isFalling;
			if(flag){
				if(liftRemainTurn <= 1){
					for(int i=0;i<width;++i)for(int j=0;j<height;++j)elements[i][j].lift();
				}
				state = State.LIFTING;
			}
			break;
		case LIFTING:
			for(int i=0;i<width;++i)for(int j=0;j<height;++j)flag &= !elements[i][j].isLifting;
			if(flag){
				flowcnt = 0;
				if(liftRemainTurn <= 1){
					for(int i=0;i<width;++i)if(!(elements[i][0] instanceof NullElement)){
						elements[i][0].erase();
						topelements[i] = elements[i][0];
						elements[i][0] = new NullElement(this,i,0);
						++flowcnt;
					}
					for(int i=0;i<width;++i){
						for(int j=0;j<height-1;++j){
							elements[i][j] = elements[i][j+1];
						}
						elements[i][height-1] = new Element(this,i,height-1);
					}
				}
				--liftRemainTurn;
				if(liftRemainTurn<=0)liftRemainTurn = LIFT_REM_TURN;
				state = State.AFTER_EFFECT;
				progress = 0;
				moto_playerHP = playerHP;
				moto_enemyHP = enemyHP;
				moto_score = score;
			}
			break;
		case AFTER_EFFECT:
			// score += erasecnt * 100
			// playerHP -= flowcnt * 2
			// enemyHP -= erasecnt
			++progress;
			if(progress >= 30){
				playerHP = moto_playerHP - flowcnt * 2;
				enemyHP = moto_enemyHP - erasecnt;
				moto_score = score + erasecnt * 100;
				if(playerHP<0)playerHP = 0;
				if(enemyHP<0)enemyHP = 0;
				state = State.WAITING;
			}else{
				playerHP = moto_playerHP - flowcnt * 2 * progress / 30;
				enemyHP = moto_enemyHP - erasecnt * progress / 30;
				if(playerHP<0)playerHP = 0;
				if(enemyHP<0)enemyHP = 0;
				score = moto_score + erasecnt * 100 * progress / 30;
			}
			break;
		}
		clicked = false;

		for (Button b : buttons) b.step();
		pauseEffect.step();
	}
	int bombtimer=60;
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
				);
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1.5f));
		g.drawLine(
				window.getWidth() / 2 - elementAreaWidth / 2,
				window.getHeight() - elementSize - 23,
				window.getWidth() / 2 + elementAreaWidth / 2,
				window.getHeight() - elementSize - 23
				);
		//エレメント描画
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				elements[i][j].draw(g);
			}
		}
		g.setColor(new Color(252, 181, 96));
		g.fillRoundRect(
				window.getWidth() / 2 - elementAreaWidth / 2 - 10,
				45,
				elementAreaWidth + 20,
				40,
				20,
				20
				);
		g.fillRoundRect(15,
				window.getHeight() - elementAreaHeight + 50,
				190, 350, 20, 20);

		g.fillRoundRect(595,
				window.getHeight() - elementAreaHeight + 50,
				190, 350, 20, 20);

		int playerH = 350*(100-playerHP)/100;
		int playerY = window.getHeight() - elementAreaHeight + 50 + 350 - playerH;
		g.drawImage(bury, 15, playerY, 190, playerH, null);
		int enemyH = 350*(100-enemyHP)/100;
		int enemyY = window.getHeight() - elementAreaHeight + 50 + 350 - enemyH;
		g.drawImage(bury, 595, enemyY, 190, enemyH, null);

//		g.setColor(new Color(247, 155, 55));
//		g.fillRoundRect(22,
//				window.getHeight() - elementAreaHeight + 295,
//				175, 46, 20, 20);
//		g.fillRoundRect(22,
//				window.getHeight() - elementAreaHeight + 350,
//				175, 46, 20, 20);
//		g.fillRoundRect(603,
//				window.getHeight() - elementAreaHeight + 295,
//				175, 46, 20, 20);
//		g.fillRoundRect(603,
//				window.getHeight() - elementAreaHeight + 350,
//				175, 46, 20, 20);
//		g.fillRoundRect(603,
//				window.getHeight() - elementAreaHeight + 410,
//				175, 46, 20, 20);
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
		for (Button b : buttons) b.draw(g);

		g.setFont(englishFont);
		g.setColor(Color.BLACK);
		g.drawString("NEXT",
				window.getWidth() / 2 - elementAreaWidth / 2 - 100,
				window.getHeight() - elementSize + 15);
		g.drawString(String.valueOf(score),
				window.getWidth() / 2 + elementAreaWidth / 2 + 30,
				window.getHeight() - elementAreaHeight + 10);
		g.setFont(japaneseFont);
		g.drawString("すこあ",
				window.getWidth() / 2 + elementAreaWidth / 2 + 30,
				window.getHeight() - elementAreaHeight - 30
				);
		g.drawString("のこり",
				50,
				window.getHeight() - elementAreaHeight - 30
				);
		g.drawString(liftRemainTurn + "ターン",
				50,
				window.getHeight() - elementAreaHeight
				);

		
		bombtimer--;
		//エレメント描画
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				elements[i][j].draw(g);
			}
		}

		//マウス座標表示
		g.setColor(Color.RED);
//		g.fillOval(mx-5, my-5, 10, 10);

		//灰色フィルター
		g.setColor(new Color(100, 100, 100, 100));
		g.fillRect(window.getWidth()/2 - elementAreaWidth / 2, window.getHeight() - elementAreaHeight + elementSize * (height-1) - 10, elementAreaWidth, elementSize);

		pauseEffect.draw(g);
	}

	void mousePressed(MouseEvent e) {
		if (pauseFlag) {
			pauseFlag = false;
			pauseEffect.end();
			return;
		}
		this.mx = e.getX();
		this.my = e.getY();
		clicked = true;

		for (Button b : buttons) b.mousePressed(e);
	}

	void mouseDragged(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}

	void mouseMoved(MouseEvent e) {
		for (Button b : buttons) {
			b.mouseMoved(e);
		}
	}

	// mx,myを与えると、それが含まれるElement(x,y)についてx*height+yを返す
	// 該当なしの場合は-1を返す
	int selectElement(int mx, int my) {
		int x = (int)((mx - (window.getWidth() - elementAreaWidth) / 2) / elementSize);
		int y = (int)((my - (window.getHeight() - elementAreaHeight) + 10) / elementSize);
		if(0<=x && x<width && 0<=y && y<height-1){
			Element e = elements[x][y];
			if(!(e instanceof NullElement)){
				float dx = (float)(e.getX(x)+elementSize/2f - mx);
				float dy = (float)(e.getY(y)+elementSize/2f - my);
				if(dx*dx + dy*dy <= elementSize*elementSize){
					// pushed
					return x*height+y;
				}
			}
		}
		return -1;
	}

}
