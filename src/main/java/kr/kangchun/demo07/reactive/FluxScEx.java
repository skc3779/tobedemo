package kr.kangchun.demo07.reactive;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Created by skc37 on 2017-01-30.
 * lecture 8
 */
@Slf4j
public class FluxScEx {
    public static void main(String[] args) {

        Flux.range(1,10)
                .publishOn(Schedulers.newSingle("pub"))
                .log()
                .subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(System.out::println);
        log.debug("exit");

    }
}
