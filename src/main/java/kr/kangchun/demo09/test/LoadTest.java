package kr.kangchun.demo09.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by skc37 on 2017-03-11.
 */
@Slf4j
public class LoadTest {
    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();

        String url = "http://localhost:8080/test?idx={idx}";

        //동시에 스레드를 실행
        CyclicBarrier barrier = new CyclicBarrier(101);

        for(int i=0; i<100; i++) {
            es.submit(()->{
                int idx = counter.addAndGet(1);

                barrier.await();
                log.info("Thread {}", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                String result = rt.getForObject(url, String.class, idx);

                sw.stop();
                log.info("Elapsed: {}, {} / {}", idx, sw.getTotalTimeSeconds(), result);

                return null;
            });
        }

        barrier.await();
        StopWatch main = new StopWatch();
        main.start();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total: {}", main.getTotalTimeSeconds());
    }
}
