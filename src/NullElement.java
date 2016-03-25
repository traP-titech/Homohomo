import java.awt.Graphics2D;

public class NullElement extends Element{

	public NullElement(Stage stage, int x, int y) {
		super(stage, x, y);
		type = null;
	}
	public void erase(){}
	public void fall(int y){}
	public void lift(){--y;gy=getY(y);}
	public void step(){}
	public void draw(Graphics2D g){}
}
