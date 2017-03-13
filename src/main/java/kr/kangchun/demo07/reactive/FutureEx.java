package kr.kangchun.demo07.reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by skc37 on 2017-02-04.
 */
@Slf4j
public class FutureEx {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ExecutorService es = Executors.newCachedThreadPool();

/*        es.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                }catch(InterruptedException e) {}
            }
        });*/

        Future<String> f = es.submit(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        });

        System.out.println(f.get()); // Blocking

        log.info("Exit");

    }
}
