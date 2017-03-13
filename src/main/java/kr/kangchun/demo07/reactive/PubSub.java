package kr.kangchun.demo07.reactive;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by skc37 on 2017-01-15.
 */
public class PubSub {

    /**
     *
     * 1. 확장된 옵져버 패턴 =>
     * Observable 패턴의 문제점 해결.
     * 1) Complete 개념이 없다.
     * 2) Error 발생에 대한 처리가 패턴에 있지 않다.
     * http://www.reactive-streams.org
     * http://www.reactivex.io
     */

    /**
     * http://www.reactive-streams.org
     * 소스 : https://github.com/reactive-streams/reactive-streams-jvm/
     * 스펙 : https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.0/README.md#specification
     *
     */

    public static void main(String[] args) {
        Iterable<Integer> iter = Arrays.asList(1,2,3,4,5);

        Publisher p = new Publisher<Integer>() {

            @Override
            public void subscribe(Subscriber s) {

                // Subscription 구독이라는 정보를 가진 오브젝트 (중개자 열할)
                s.onSubscribe(new Subscription() {
                    Iterator<Integer> it = iter.iterator();

                    @Override
                    public void request(long n) {
                        try {
                            while (n-- > 0) {
                                if(it.hasNext()) {
                                    s.onNext(it.next());
                                } else {
                                    s.onComplete();
                                    break;
                                }
                            }
                        } catch (RuntimeException e) {
                            s.onError(e);
                        }

                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        };

        Subscriber<Integer> s = new Subscriber<Integer>() {

            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) { // 필수
                System.out.println("onSubscribe");
                this.subscription = s;
                this.subscription.request(2l);
            }

            int bufferSize = 2;

            @Override
            public void onNext(Integer item) { // Optional 무제한
                System.out.println("onNext " + item);
                if(--bufferSize <= 0)
                    this.subscription.request(1l);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError");

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");

            }
        };

        // 출판자에게 구독자가 구독처리
        p.subscribe(s);
    }

}
