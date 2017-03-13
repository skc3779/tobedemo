package kr.kangchun.demo07.reactive;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tobee-tv lecture 7
 * reactive Streams - Schdulers
 */
@Slf4j
public class SchedulerEx {
    public static void main(String[] args) {
        Publisher<Integer> pub = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        log.debug("request()");
                        s.onNext(1);
                        s.onNext(2);
                        s.onNext(3);
                        s.onNext(4);
                        s.onNext(5);
                        s.onComplete();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
        // pub

        // sub
        Subscriber<Integer> sub = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                // 전체값을 보내..
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);

            }

            @Override
            public void onNext(Integer i) {
                log.debug("onNext:{}",i);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError:{}",t);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };


        Publisher<Integer> subOnPub = subOp -> {

            // single thread만 보내줌 나머지는 Queue 에 저장.
            ExecutorService es = Executors.newSingleThreadExecutor(
                new CustomizableThreadFactory() {
                    @Override
                    public String getThreadNamePrefix() {
                        return "subOn-";
                    }
                }
            );

            es.execute(()->{
                log.debug("subOnPub execute");
                pub.subscribe(sub);
            });
        };

        Publisher<Integer> pubOnPub = subOp -> {

            // 중계 Subscriber 생성.
            pub.subscribe(new Subscriber<Integer>() {
            //subOnPub.subscribe(new Subscriber<Integer>() {
                ExecutorService es = Executors.newSingleThreadExecutor(
                    new CustomizableThreadFactory(){
                        @Override
                        public String getThreadNamePrefix() {
                            return "pubOn-";
                        }
                    });

                @Override
                public void onSubscribe(Subscription s) {
                    log.debug("pubOnPub onSubscribe");
                    subOp.onSubscribe(s);
                }

                @Override
                public void onNext(Integer i) {
                    es.execute(()->subOp.onNext(i));

                }

                @Override
                public void onError(Throwable t) {
                    es.execute(()->subOp.onError(t));
                    es.shutdown();
                }

                @Override
                public void onComplete() {
                    es.execute(subOp::onComplete);
                    es.shutdown();
                }
            });
        };


        // 1. none operator
        // pub.subscribe(sub);

        // append to operator
        // 2. publisher 가 느리고 consumer(s) 빠른 시나리오.
        // subOnPub.subscribe(sub);

        // 3. publisher 가 빠르고 consumer(s) 느린 시나리오.
        pubOnPub.subscribe(sub);

        // 2 가지를 모두 적용. (정상작동 안함 ?)
        // 4. subOnPub -> pubOnPub
        // pubOnPub.subscribe(sub);

        log.debug("exit");
    }
}
