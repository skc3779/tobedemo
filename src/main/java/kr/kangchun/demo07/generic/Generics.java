package kr.kangchun.demo07.generic;

import java.util.*;

/**
 * Created by skc37 on 2017-01-01.
 */
public class Generics {

    static <T extends Comparable<T>> Long countGreatThan(T[] arr, T elem) {
        return Arrays.stream(arr).filter(s -> s.compareTo(elem) > 0).count();
    }

    private static <T extends Comparable<? super T>> T max(List<? extends T> list) {
        return list.stream().reduce((a,b)->a.compareTo(b) > 0 ? a : b).get();
    }

    private static <T> void reverse(List<T> list) {
        List<T> temp = new ArrayList<>(list);
        for(int i=0; i<list.size();i++) {
            list.set(i, temp.get(list.size()-i-1));
        }
    }


    /**
     * Generics에 와일드카드 활용방법
     * @param args
     */
    public static void main(String[] args) {
        String[] arr = new String[] {"a", "b", "c", "d"};
        System.out.println("Generics extends type ------------------------------");
        System.out.println("count:" + countGreatThan(arr, "b"));

        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7);

        System.out.println("wildcard extend type -------------------------------");
        System.out.println("max : " + max(list));
        System.out.println("max : " + Collections.max(list));

        System.out.println("reverse -------------------------------");
        reverse(list);
        System.out.println("reverse : " + Arrays.toString(list.toArray()));
    }


}
