import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Vector;

import javax.swing.JFrame;

public class Stage {

	public final int elementAreaHeight = 480;
	public final int elementAreaWidth;

	public final JFrame window;
	public final int width, height;
	private final Element[][] elements;
	
	private enum State{
		WAITING,
		ERASING,
		FALLING,
		LIFTING,
		AFTER_EFFECT
	}
	private State state;

	boolean clicked = false;
	int mx, my;
	int elementSize;
	Font englishFont, japaneseFont;
	static final int LIFT_REM_TURN = 5;
	int liftRemainTurn = 5;
	long score = 1145141919810L;
	public Bakuhatsu baku;
	int ballcount=0;

	public Stage(JFrame window, int width, int height) {
		baku = new Bakuhatsu();
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
		score = 364364;

		englishFont = Utils.createFont("helsinki.ttf").deriveFont(Font.PLAIN, 35);
		japaneseFont = Utils.createFont("yasashisa_bold.ttf").deriveFont(Font.PLAIN, 27);
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
					int cnt = 0;
					for(int i=0;i<width;++i){
						int s = height-1;
						for(int j=height-1;j>=0;--j){
							if(!elements[i][j].killed){
								elements[i][s] = elements[i][j];
								--s;
							}else if(!(elements[i][j] instanceof NullElement)){
								++cnt;
							}
						}
						for(int j=s;j>=0;--j){
							elements[i][j] = new NullElement(this,i,j);
						}
					}
					score += 364*cnt;
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
				--liftRemainTurn;
				if(liftRemainTurn <= 0){
					for(int i=0;i<width;++i)for(int j=0;j<height;++j)elements[i][j].lift();
				}
				state = State.LIFTING;
			}
			break;
		case LIFTING:
			for(int i=0;i<width;++i)for(int j=0;j<height;++j)flag &= !elements[i][j].isLifting;
			if(flag){
				if(liftRemainTurn <= 0){
					int cnt = 0;
					for(int i=0;i<width;++i)if(!(elements[i][0] instanceof NullElement)){
						elements[i][0].erase();
						topelements[i] = elements[i][0];
						elements[i][0] = new NullElement(this,i,0);
						++cnt;
					}
					for(int i=0;i<width;++i){
						for(int j=0;j<height-1;++j){
							elements[i][j] = elements[i][j+1];
						}
						elements[i][height-1] = new Element(this,i,height-1);
					}
					// cnt だけ 自分に ダメージ 入れといて
					liftRemainTurn = LIFT_REM_TURN;
				}
				state = State.AFTER_EFFECT;
			}
			break;
		case AFTER_EFFECT:
			state = State.WAITING;
			break;
		}
		clicked = false;
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

		g.setColor(new Color(247, 155, 55));
		g.fillRoundRect(22,
				window.getHeight() - elementAreaHeight + 295,
				175, 46, 20, 20);
		g.fillRoundRect(22,
				window.getHeight() - elementAreaHeight + 350,
				175, 46, 20, 20);
		g.fillRoundRect(603,
				window.getHeight() - elementAreaHeight + 295,
				175, 46, 20, 20);
		g.fillRoundRect(603,
				window.getHeight() - elementAreaHeight + 350,
				175, 46, 20, 20);
		g.fillRoundRect(603,
				window.getHeight() - elementAreaHeight + 410,
				175, 46, 20, 20);
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
				window.getHeight() - elementAreaHeight + 295,
				175, 46, 20, 20);
		g.drawRoundRect(22,
				window.getHeight() - elementAreaHeight + 350,
				175, 46, 20, 20);
		g.drawRoundRect(603,
				window.getHeight() - elementAreaHeight + 295,
				175, 46, 20, 20);
		g.drawRoundRect(603,
				window.getHeight() - elementAreaHeight + 350,
				175, 46, 20, 20);
		g.drawRoundRect(603,
				window.getHeight() - elementAreaHeight + 410,
				175, 46, 20, 20);
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
		g.drawString("ポーズ",
				window.getWidth() / 2 + elementAreaWidth / 2 + 70,
				window.getHeight() - 40
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
		g.fillOval(mx-5, my-5, 10, 10);

		//灰色フィルター
		g.setColor(new Color(100, 100, 100, 100));
		g.fillRect(window.getWidth()/2 - elementAreaWidth / 2, window.getHeight() - elementAreaHeight + elementSize * (height-1) - 10, elementAreaWidth, elementSize);
		if(bombtimer <= 1){
			bombtimer = 60;
		}
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,bombtimer/60f));
		baku.draw(g);
	}

	void mousePressed(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
		clicked = true;
	}

	void mouseDragged(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
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
