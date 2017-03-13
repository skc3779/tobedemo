package kr.kangchun.demo07.generic;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by skc37 on 2017-01-13.
 */
public class IntersectionType {
    interface Hello {
        default void hello() {
            System.out.println("Hello");
        }
    }

    interface Hi {
        default void hi() {
            System.out.println("Hi");
        }
    }

    interface Printer {
        default void printer(String str) {
            System.out.println(str);
        }
    }

    private static <T extends Function & Hello & Hi>  void hello(T t) {
        t.hello();
        t.hi();
    }

    private static <T extends Function> void run(T t, Consumer<T> consumer) {
        consumer.accept(t);
    }

    interface DelegateTo<T> {
        T delegate();
    }

    private static <T extends DelegateTo<S>, S> void run2(T t, Consumer<T> consumer) {
        consumer.accept(t);
    }

    interface Hello2 extends DelegateTo<String> {
        default void hello2() {
            System.out.println("Hello2 " + delegate());
        }
    }

    interface UpperCase extends DelegateTo<String> {
        default void upperCase() {
            System.out.println("Hello2 " + delegate().toUpperCase());
        }
    }


    //---------------------------------------------------------------------
    // 사례적용
    //---------------------------------------------------------------------

    interface Pair<T> {
        T getFirst();
        T getSecond();
        void setFirst(T first);
        void setSecond(T second);
    }

    static class Name implements Pair<String> {

        String firstName;
        String lastName;

        Name(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public String getFirst() {
            return this.firstName;
        }

        @Override
        public String getSecond() {
            return this.lastName;
        }

        @Override
        public void setFirst(String first) {
            this.firstName = first;
        }

        @Override
        public void setSecond(String second) {
            this.lastName = second;
        }
    }

    interface ForwardingPair<T> extends DelegateTo<Pair<T>>, Pair<T> {
        default T getFirst() {return delegate().getFirst(); }
        default T getSecond() { return delegate().getSecond(); }
        default void setFirst(T first) { delegate().setFirst(first); }
        default void setSecond(T second) { delegate().setSecond(second);}
    }

    interface Convertable<T> extends DelegateTo<Pair<T>> {
        default void convert(Function<T,T> mapper) {
            Pair<T> pair = delegate();
            pair.setFirst(mapper.apply(pair.getFirst()));
            pair.setSecond(mapper.apply(pair.getSecond()));
        }
    }

    static <T> void print(Pair<T> pair) {
        System.out.println(pair.getFirst() + ", " + pair.getSecond());
    }

    interface Printable<T> extends DelegateTo<Pair<T>> {
        default void print() {
            System.out.println(delegate().getFirst() + ", " + delegate().getSecond());
        }
    }

    /**
     * 람다와 인터섹션 타입을 이용한 동적인 기능 확장방법
     * @param args
     */
    public static void main(String[] args) {

        hello((Function & Hello & Hi)s->s);

        System.out.println("----------------------------------------");
        run((Function & Hello & Hi & Printer)s->s, o->{
            o.hello();
            o.hi();
            o.printer("Printer");
        });

        System.out.println("----------------------------------------");

        run2((DelegateTo<String> & Hello2 & UpperCase)()->"Kangchun", o->{
            o.hello2();
            o.upperCase();
        });

        //-------------------------------------------------------------
        //사례

        System.out.println("----------------------------------------");
        Pair<String> name = new Name("Toby", "Lee");
        run2((ForwardingPair<String>)()->name, o-> {
           System.out.println(o.getFirst());
            System.out.println(o.getSecond());
        });

        System.out.println("----------------------------------------");
        run2((ForwardingPair<String> & Convertable<String>)()->name, o-> {
            print(o);
            o.convert(s->s.toUpperCase());
            print(o);
        });

        Pair<String> name2 = new Name("Toby", "Lee");
        System.out.println("----------------------------------------");
        run2((ForwardingPair<String> & Convertable<String> & Printable<String>)()->name2, o-> {
            o.print();
            o.convert(s->s.toUpperCase());
            o.print();
        });

    }
}
