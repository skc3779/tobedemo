package kr.kangchun.demo09.app02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * Tobe09 비동기 방식을 사용
 * 단 자원이 비효율적으로 사용됨.
 * 백그라운드에서 100개의 스레드를 만듬.
 */
@SpringBootApplication
public class Demo02Application {

    @RestController
    public static class MyController {
        AsyncRestTemplate rt = new AsyncRestTemplate();
        private String REMOTE_URL = "http://localhost:8081/demo09?req={req}";

        /**
         * ListenableFuture는 비동기적으로 리턴값을 돌려준다.
         * @param idx
         * @return
         */
        @RequestMapping(name="/rest", method= RequestMethod.GET)
        public ListenableFuture<ResponseEntity<String>> rest(int idx) {
            return rt.getForEntity(REMOTE_URL, String.class, "hello " + idx);
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(Demo02Application.class, args);
    }
}
