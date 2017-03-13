package kr.kangchun.demo08;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class Demo08Application {

	public static void main(String[] args) {
		SpringApplication.run(Demo08Application.class, args);
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


	@RestController
	public static class ReactiveController {
		@RequestMapping(name="/reactive1", method= RequestMethod.GET)
		public Publisher<String> reactive1(@RequestParam("name") String name) {
			return new Publisher<String>() {
				@Override
				public void subscribe(Subscriber<? super String> s) {
					s.onSubscribe(new Subscription() {
						@Override
						public void request(long n) {
							s.onNext("Hello " + name);
							s.onComplete();
						}

						@Override
						public void cancel() {

						}
					});
				}
			};
		}

		@RequestMapping(name="/reactive2", method= RequestMethod.GET)
		public String reactive2(@RequestParam("name") String name) {
			return name;
		}
	}

}
