import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;


public class Utils {

	static <T> T randomSelect(T[] array) {
		return array[(int)((Math.random() * array.length))];
	}

	static Font createFont(String filename){
		Font font = null;
		InputStream is = null;
		try {
			is = Main.class.getResourceAsStream(filename);
			font = Font.createFont(Font.TRUETYPE_FONT, is);
			is.close();
		}catch(IOException e){
			e.printStackTrace();
		}catch(FontFormatException e){
			e.printStackTrace();
		}
		return font;
	}
}
