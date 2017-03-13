package kr.kangchun.demo10.app05;

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
 * 5.2개의 REST API 비동기 헬을 제거해 보기
 * 6.Completion refactoring
 * 7.Error 처리 중복 문제 해결 (RemoteApplication throw new RuntimeException(); 발생
 * 8.MyService 적용, Generic을 이용한 타입변환
 */
@SpringBootApplication
public class Demo05Application {


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
                    .andApply(s->rt.getForEntity(REMOTE2_URL, String.class, s.getBody()))
                    .andApply(s->myService.work(s.getBody()))
                    .andError(e->dr.setErrorResult(e.toString()))
                    .andAccept(s->dr.setResult(s));

            return dr;
        }

    }

    public static class ErrorCompletion<T> extends Completion<T, T> {

        Consumer<Throwable> econ;
        public ErrorCompletion(Consumer<Throwable> econ) {
            this.econ = econ;
        }

        @Override
        void run(T value) {
            if(next != null) next.run(value);
        }

        @Override
        void error(Throwable e) {
            econ.accept(e);
        }
    }

    public static class AcceptCompletion<S> extends Completion<S, Void> {

        Consumer<S> con;
        public AcceptCompletion(Consumer<S> con) {
            this.con = con;
        }

        @Override
        void run(S value) {
            con.accept(value);
        }

    }

    public static class ApplyCompletion<S, T> extends Completion<S, T> {

        Function<S, ListenableFuture<T>> fn;
        public ApplyCompletion(Function<S, ListenableFuture<T>> fn) {
            this.fn = fn;
        }

        @Override
        void run(S value) {
            ListenableFuture<T> lf = fn.apply(value);
            lf.addCallback(s->{
                complete(s);
            }, e->{
                error(e);
            });
        }
    }

    public static class Completion<S, T> {
        Completion next;

        public Completion() {}

        public <V> Completion<T,V> andApply(Function<T, ListenableFuture<V>> fn) {
            Completion<T,V> c = new ApplyCompletion<>(fn);
            this.next = c;
            return c;
        }

        public void andAccept(Consumer<T> con) {
            Completion<T, Void> c = new AcceptCompletion<>(con);
            this.next = c;
        }

        public Completion<T, T> andError(Consumer<Throwable> econ) {
            Completion<T, T> c = new ErrorCompletion<>(econ);
            this.next = c;
            return c;
        }

        public static <S, T> Completion<S, T> from(ListenableFuture<T> lf) {
            Completion<S, T> c = new Completion<>();
            lf.addCallback(s->{
                c.complete(s);
            }, e->{
                c.error(e);
            });
            return c;
        }

        void complete(T s) {
            if(next != null) next.run(s);
        }

        void run(S value) {
        }

        void error(Throwable e) {
            if(next != null) next.error(e);
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
        SpringApplication.run(Demo05Application.class, args);
    }
}
