package kr.kangchun.demo11.app02;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Tobe11 CompletableFuture
 *
 */
@Slf4j
public class Demo2Application {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

/*
        CompletableFuture
                .runAsync(()->log.info("runAsync"))
                .thenRun(()->log.info("thenRun1"))
                .thenRun(()->log.info("thenRun2"));
*/

        CompletableFuture
                .supplyAsync(() -> {
                    log.info("runAsync");
                    if(1==1) throw new RuntimeException();
                    return 1;
                })
                .thenApply(s -> {
                    log.info("thenApply {}", s);
                    return s + 1;
                })
                .thenCompose(s2-> {
                    log.info("thenCompose {}", s2);
                    return CompletableFuture.completedFuture(s2+1);
                })
                .exceptionally(e-> -10)
                .thenAccept(s2 -> log.info("thenAccept {}", s2));


        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);

    }
}
