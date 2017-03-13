package kr.kangchun.demo10.app01;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Tobe09 비동기 방식을 사용
 * 논 블로깅 IO 방식을 이용하여 개선.
 * 대표적인 라이브러리 Netty 사용.
 * 1.로직을 추가하고 콜백으로 돌려주기 위해 DeferredResult를 사용함.
 * 2.로직을 이제는 2개의 REST API를 이용해 체인방식으로 호출해보자.
 * 3.3번째는 로직에 Async 서비스를 추가한다.
 * 4.비동기(콜백) 헬을 제거해 보기
 */
@SpringBootApplication
public class Demo01Application {


    @RestController
    public static class MyController {

        AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));
        private String REMOTE_URL = "http://localhost:8081/demo09?req={req}";
        private String REMOTE2_URL = "http://localhost:8082/service2?req={req}";

        @Autowired MyService myService;

        /**
         * ListenableFuture는 비동기적으로 리턴값을 돌려준다.
         * @param idx
         * @return
         */
        @RequestMapping(name="/rest", method= RequestMethod.GET)
        public DeferredResult<String> rest(int idx) {

            DeferredResult<String> dr = new DeferredResult<>();

            Completion.from(rt.getForEntity(REMOTE_URL, String.class, "hello " + idx))
                    .andAccept(s->dr.setResult(s.getBody()));

//            ListenableFuture<ResponseEntity<String>> f1 =
//                    rt.getForEntity(REMOTE_URL, String.class, "hello " + idx);
//
//            f1.addCallback(s-> {
//                ListenableFuture<ResponseEntity<String>> f2 =
//                        rt.getForEntity(REMOTE2_URL, String.class, s.getBody());
//                f2.addCallback(s2->{
//                    ListenableFuture<String> f3 = myService.work(s2.getBody());
//                    f3.addCallback(s3->{
//                        dr.setResult(s3);
//                    }, e3-> {
//                        dr.setErrorResult(e3.getMessage());
//                    });
//
//                }, e2-> {
//                    dr.setErrorResult(e2.getMessage());
//                });
//            }, e-> {
//                dr.setErrorResult(e.getMessage());
//            });

            return dr;
        }

    }

    public static class Completion {

        Consumer<ResponseEntity<String>> con;

        Completion next;

        public Completion() {}

        public Completion(Consumer<ResponseEntity<String>> con) {
            this.con = con;
        }

        public void andAccept(Consumer<ResponseEntity<String>> con) {
            Completion c = new Completion(con);
            this.next = c;
        }

        public static Completion from(ListenableFuture<ResponseEntity<String>> lf) {
            Completion c = new Completion();

            lf.addCallback(s->{
                c.complete(s);
            }, e->{
                c.error(e);
            });
            return c;
        }

        private void complete(ResponseEntity<String> s) {
            if(next != null) next.run(s);
        }

        private void run(ResponseEntity<String> s) {
            if(con != null) con.accept(s);
        }

        private void error(Throwable e) {

        }

    }

    @Service
    public static class MyService {
        @Async
        public ListenableFuture<String> work(String req) {
            return new AsyncResult<>(req + "/asyncwork3");
        }
    }

    @Bean
    public ThreadPoolTaskExecutor myThreadPool() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(1);
        te.setMaxPoolSize(10);
        te.initialize();
        return te;
    }

    public static void main(String[] args) {
        SpringApplication.run(Demo01Application.class, args);
    }
}
