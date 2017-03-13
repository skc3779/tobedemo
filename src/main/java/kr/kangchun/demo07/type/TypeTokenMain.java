package kr.kangchun.demo07.type;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skc37 on 2016-12-30.
 */
@Slf4j
public class TypeTokenMain {

    static class TypesafeMap {
        Map<Class<?>, Object> map = new HashMap<>();

        <T> void put(Class<T> clazz, T value) {
            map.put(clazz, value);
        }

        <T> T get(Class<T> clazz) {
            return clazz.cast(map.get(clazz));
        }
    }

    static class Sup<T> {
        T value;
    }

    public static void main(String[] args) {

        TypesafeMap m = new TypesafeMap();
        m.put(Integer.class, 1);
        m.put(String.class, "String");
        m.put(List.class, Arrays.asList(1,2,3));
        m.put(List.class, Arrays.asList("1","2","3"));

        log.info("String:{}", m.get(String.class));
        log.info("Integer:{}", m.get(Integer.class));

    }
}
