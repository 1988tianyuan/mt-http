import java.util.regex.Pattern;

import org.junit.Test;

public class MainTest {

	@Test
	public void test1() {
		System.out.println(Pattern.matches(".*\\$.*\\.class", "/target/classes/com/liugeng/mthttp/server/handler/HttpDispatcherHandler$SessionRemoveTask.class"));
	}
}
