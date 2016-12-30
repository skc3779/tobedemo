package kr.kangchun;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class TobeDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TobeDemoApplication.class, args);
	}

	@RestController
	public static class MyController {
		@RequestMapping("/")
		public List<User> users() {
			return Arrays.asList(new User("a"), new User("b"), new User("c"));
		}
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@ToString
	public static class User {
		String name;
	}
}
