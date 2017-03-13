package kr.kangchun.demo11.app04;

import io.netty.channel.nio.NioEventLoopGroup;
import kr.kangchun.demo09.app06.Demo06Application;
import lombok.extern.slf4j.Slf4j;
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

import java.util.concurrent.*;

/**
 * Tobe11 CompletableFuture
 * 비동기 작업은
 * CompletableFuture 를 사용해서 작성하자.
 */
@Slf4j
@SpringBootApplication
public class Demo4Application {


    @RestController
    public static class MyController {

        AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));
        private String REMOTE_URL = "http://localhost:8081/demo09?req={req}";
        private String REMOTE2_URL = "http://localhost:8082/service2?req={req}";

        @Autowired
        MyService myService;

        /**
         * ListenableFuture는 비동기적으로 리턴값을 돌려준다.
         * @param idx
         * @return
         */
        @RequestMapping(name="/rest", method= RequestMethod.GET)
        public DeferredResult<String> rest(int idx) {

            DeferredResult<String> dr = new DeferredResult<>();

            toCF(rt.getForEntity(REMOTE_URL, String.class, "hello " + idx))
                    .thenCompose(s->toCF(rt.getForEntity(REMOTE2_URL, String.class, s.getBody())))
                    .thenCompose(s2->toCF(myService.work(s2.getBody())))
                    .thenAccept(s3->dr.setResult(s3))
                    .exceptionally(e-> {
                        dr.setErrorResult(e.getMessage());
                        return (Void)null;
                    });


/*
            ListenableFuture<ResponseEntity<String>> f1 =
                    rt.getForEntity(REMOTE_URL, String.class, "hello " + idx);

            f1.addCallback(s-> {
                ListenableFuture<ResponseEntity<String>> f2 =
                        rt.getForEntity(REMOTE2_URL, String.class, s.getBody());
                f2.addCallback(s2->{
                    ListenableFuture<String> f3 = myService.work(s2.getBody());
                    f3.addCallback(s3->{
                        dr.setResult(s3);
                    }, e3-> {
                        dr.setErrorResult(e3.getMessage());
                    });

                }, e2-> {
                    dr.setErrorResult(e2.getMessage());
                });
            }, e-> {
                dr.setErrorResult(e.getMessage());
            });
*/

            return dr;
        }

        <T> CompletableFuture<T> toCF(ListenableFuture<T> lf) {
            CompletableFuture<T> cf = new CompletableFuture<T>();
            lf.addCallback(s-> cf.complete(s), e->cf.completeExceptionally(e));
            return cf;
        }

    }

    /**
     * MyService는 이제 비동기로 작업시킬 필요가 없음.
     * Demo5Application 참고
     */
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
        SpringApplication.run(Demo4Application.class, args);
    }
}
