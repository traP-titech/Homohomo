import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Vector;

import javax.swing.JFrame;

public class Stage {

	public final int elementAreaHeight = 400;
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

	int mx, my;
	int elementSize;

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
		
		state = State.WAITING;
	}

	void mainLoop(Graphics2D g) {
		step();
		draw(g);
	}
	
	Vector<Integer>[] eraselist;
	int puzzleBFS(int x, int y){
		int[][] visited = new int[width][height];
		for(int i=0;i<width;++i)for(int j=0;j<height;++j)visited[i][j]=-1;
		Queue<int[]> q = new ArrayDeque<int[]>();
		visited[x][y] = 0;
		q.add(new int[]{x,y});
		int[] dx = new int[]{1,0,-1,0};
		int[] dy = new int[]{0,1,0,-1};
		int res = 0;
		while(q.peek() != null){
			int[] p = q.poll();
			int val = visited[p[0]][p[1]];
			for(int i=0;i<4;++i){
				int nx = p[0] + dx[i];
				int ny = p[1] + dy[i];
				if(/*ここに連鎖条件*/false){
					visited[nx][ny] = val+1;
					res = val+1;
					q.add(new int[]{nx,ny});
				}
			}
		}
		return res;
	}

	void step() {
		switch (state) {
		case WAITING:
			int id = selectElement(mx,my);
			if(id!=-1){
				int x = id/height;
				int y = id%height;
				// erase calculation
				int num = puzzleBFS(x, y);
				// phase proceed
			}
			break;
		case ERASING:
			//
			break;
		case FALLING:
			//
			break;
		case LIFTING:
			//
			break;
		case AFTER_EFFECT:
			//
			break;
		}
		//エレメント更新
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				elements[i][j].step();
			}
		}
	}

	void draw(Graphics2D g) {

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
		g.fillRect(window.getWidth()/2 - elementAreaWidth / 2, window.getHeight() - elementAreaHeight + elementSize * (height-1) - 20, elementAreaWidth, elementSize);
	}

	void mousePressed(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}

	void mouseDragged(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}

	// mx,myを与えると、それが含まれるElement(x,y)についてx*height+yを返す
	// 該当なしの場合は-1を返す
	int selectElement(int mx, int my) {
		int x = (int)((mx - (window.getWidth() - elementAreaWidth) / 2) / elementSize);
		int y = (int)((my - (window.getHeight() - elementAreaHeight) + 30) / elementSize);
		if(0<=x && x<width && 0<=y && y<height){
			Element e = elements[x][y];
			float dx = (float)(e.getX()+elementSize/2f - mx);
			float dy = (float)(e.getY()+elementSize/2f - my); 
			if(dx*dx + dy*dy <= elementSize*elementSize){
				// pushed
				return x*height+y;
			}
		}
		return -1;
	}

}
