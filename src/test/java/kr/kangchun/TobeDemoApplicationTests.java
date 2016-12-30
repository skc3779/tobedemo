package kr.kangchun;

import kr.kangchun.TobeDemoApplication.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TobeDemoApplicationTests {

	@Test
	public void contextLoads() {
		RestTemplate rest = new RestTemplate();
		// java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast
		// List<User> users = rest.getForObject("http://localhost:8080", List.class);

		List<User> users = rest.exchange("http://localhost:8080", HttpMethod.GET,
				null, new ParameterizedTypeReference<List<User>>(){}).getBody();
		users.forEach(System.out::println);
	}

}
