package kr.kangchun.demo07.reactive;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by skc37 on 2017-01-15.
 */
public class Ob {

    // Iterable <---> Obserable (duality) 상대성
    // Pull
    public static void main(String[] args) {

        Iterable<Integer> iter = () ->
            new Iterator<Integer>() {
                int i=0;
                final static int MAX = 10;
                public boolean hasNext() {
                    return i < MAX;
                }
                public Integer next() {
                    return i++;
                }
            };

        System.out.println("1 ----------------------------------------");
        iter.forEach(System.out::println);
        for(Integer i: iter) {//for-each
            System.out.println(i);
        }

        System.out.println("2 ----------------------------------------");
        for(Iterator<Integer> it = iter.iterator(); it.hasNext();) {

            System.out.println(it.next());
        }

        System.out.println("3 ----------------------------------------");
        // 소스 -> 이벤트/데이터 -> 관찰자
        // Source -> Event/Data -> Observer

        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob);
        //io.run();
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io);

        System.out.println(Thread.currentThread().getName() + " EXIT");
        es.shutdown();

    }

    // 옵저버블
    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for(int i=1; i<10; i++) {
                setChanged();
                notifyObservers(i);   // push
                // int i = it.next(); // pull
            }
        }
    }


}

