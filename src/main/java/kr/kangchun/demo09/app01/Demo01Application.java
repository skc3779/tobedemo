package kr.kangchun.demo09.app01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Tobe09 강좌 비효율적인 서비스
 */
@SpringBootApplication
public class Demo01Application {

    @RestController
    public static class MyController {
        RestTemplate rt = new RestTemplate();
        private String REMOTE_URL = "http://localhost:8081/demo09?req={req}";
        @RequestMapping(name="/rest", method= RequestMethod.GET)
        public String rest(int idx) {
            String res = rt.getForObject(REMOTE_URL, String.class, "hello " + idx);
            return res;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(Demo01Application.class, args);
    }
}
