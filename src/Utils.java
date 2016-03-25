
public class Utils {

	static <T> T randomSelect(T[] array) {
		return array[(int)(Math.random() * array.length)];
	}
}
