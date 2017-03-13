package kr.kangchun.demo11.app03;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Tobe11 CompletableFuture
 *
 */
@Slf4j
public class Demo3Application {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService es = Executors.newFixedThreadPool(10);

        CompletableFuture
                .supplyAsync(() -> {
                    log.info("runAsync");
                    //if(1==1) throw new RuntimeException();
                    return 1;
                }, es)
                .thenCompose(s-> {
                    log.info("thenCompose {}", s);
                    return CompletableFuture.completedFuture(s+1);
                })
                .thenApplyAsync(s2->{
                    log.info("thenApplyAsync {}", s2);
                    return s2 + 1;
                }, es)
                .exceptionally(e-> -10)
                .thenAcceptAsync(s3 -> log.info("thenAcceptAsync {}", s3), es);


        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);

        es.awaitTermination(10, TimeUnit.SECONDS);
    }
}
