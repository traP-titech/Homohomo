import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public abstract class Scene {

	abstract Scene mainLoop(Graphics2D g);

	void mousePressed(MouseEvent e){}
	void mouseDragged(MouseEvent e){}
	void mouseMoved(MouseEvent e){}
}
