package kr.kangchun.type;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skc37 on 2016-12-30.
 */
@Slf4j
public class SuperTypeTokenMain {

    static class TypesafeMap {
        Map<TypeReference<?>, Object> map = new HashMap<>();

        <T> void put(TypeReference<T> clazz, T value) {
            map.put(clazz, value);
        }

        <T> T get(TypeReference<T> tr) {
            if(tr.type instanceof Class<?>) {
                return ((Class<T>)tr.type).cast(map.get(tr));
            } else {
                // TypeReference<List<Ineger>>
                return  ((Class<T>)((ParameterizedType)tr.type).getRawType()).cast(map.get(tr));
            }
        }
    }

    static class TypeReference<T> {
        Type type;

        public TypeReference() {
            Type stype = getClass().getGenericSuperclass();
            if(stype instanceof ParameterizedType) {
                this.type = ((ParameterizedType)stype).getActualTypeArguments()[0];
            }
            else throw new RuntimeException();
        }

        // hashCode And Equals

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeReference<?> that = (TypeReference<?>) o;

            return type.equals(that.type);
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }
    }

    public static void main(String[] args) {

        TypesafeMap m = new TypesafeMap();
        TypeReference<Integer> trInt = new TypeReference<Integer>(){};
        TypeReference<String> trString = new TypeReference<String>(){};
        TypeReference<List<Integer>> trList =  new TypeReference<List<Integer>>(){};
        TypeReference<List<String>> trListString =  new TypeReference<List<String>>(){};
        m.put(trInt, 1);
        m.put(trString, "String");
        m.put(trList, Arrays.asList(1,2,3));
        m.put(trListString, Arrays.asList("A","B","C"));
        log.info("Integer:{}", m.get(trInt));
        log.info("String:{}", m.get(trString));
        log.info("List:{}", m.get(trList));
        log.info("List:{}", m.get(trListString));

    }
}
