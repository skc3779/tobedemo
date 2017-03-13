package kr.kangchun.demo11.app01;

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
public class Demo1Application {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CompletableFuture<Integer> f = CompletableFuture.completedFuture(1);
        System.out.println(f.get());

        CompletableFuture<Integer> f2 = new CompletableFuture<>();
        f2.complete(2);
        System.out.println(f2.get());

//        CompletableFuture<Integer> f3 = new CompletableFuture<>();
//        f3.completeExceptionally(new RuntimeException());
//        System.out.println(f3.get());

        CompletableFuture.runAsync(() -> log.info("runAsync"));

        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);

    }
}
