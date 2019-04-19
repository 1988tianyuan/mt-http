import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.executor.HttpExecutor;
import com.liugeng.mthttp.router.mapping.ResourceHttpExecutorMapping;

public class MainTest {

	@Test
	public void test1() {
		System.out.println(Pattern.matches(".*\\$.*\\.class", "/target/classes/com/liugeng/mthttp/server/handler/HttpDispatcherHandler$SessionRemoveTask.class"));
	}

	@Test
	public void test2() {
		ResourceHttpExecutorMapping mapping = new ResourceHttpExecutorMapping("");
		HttpExecutor httpExecutor = mapping.getExecutor(new HttpRequestEntity.Builder().path("/xxx.jpg").build());
		System.out.println(httpExecutor);
	}

	@Test
	public void test3() {
		String[] accepts = StringUtils.split(StringUtils.replace("text/html", " ", ""), ",");
		for (String accept : accepts) {
			System.out.println(accept);
		}
	}
}
