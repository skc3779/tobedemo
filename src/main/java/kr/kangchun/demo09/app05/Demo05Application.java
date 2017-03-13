package kr.kangchun.demo09.app05;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Tobe09 비동기 방식을 사용
 * 논 블로깅 IO 방식을 이용하여 개선.
 * 대표적인 라이브러리 Netty 사용.
 * 로직을 추가하고 콜백으로 돌려주기 위해 DeferredResult를 사용함.
 * 로직을 이제는 2개의 REST API를 이용해 체인방식으로 호출해보자.
 */
@SpringBootApplication
public class Demo05Application {

    @RestController
    public static class MyController {
        AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));
        private String REMOTE_URL = "http://localhost:8081/demo09?req={req}";
        private String REMOTE2_URL = "http://localhost:8082/service2?req={req}";

        /**
         * ListenableFuture는 비동기적으로 리턴값을 돌려준다.
         * @param idx
         * @return
         */
        @RequestMapping(name="/rest", method= RequestMethod.GET)
        public DeferredResult<String> rest(int idx) {

            DeferredResult<String> dr = new DeferredResult<>();
            ListenableFuture<ResponseEntity<String>> f1 =
                    rt.getForEntity(REMOTE_URL, String.class, "hello " + idx);

            f1.addCallback(s-> {
                ListenableFuture<ResponseEntity<String>> f2 =
                        rt.getForEntity(REMOTE2_URL, String.class, s.getBody());
                f2.addCallback(s2->{
                    dr.setResult(s2.getBody() + "/work");
                }, e2->{
                    dr.setErrorResult(e2.getMessage());
                });
            }, e-> {
                dr.setErrorResult(e.getMessage());
            });

            return dr;
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(Demo05Application.class, args);
    }
}
