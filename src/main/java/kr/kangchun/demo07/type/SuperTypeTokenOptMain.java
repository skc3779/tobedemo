package kr.kangchun.demo07.type;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skc37 on 2016-12-30.
 * Optimize
 */
@Slf4j
public class SuperTypeTokenOptMain {

    static class TypesafeMap {
        Map<Type, Object> map = new HashMap<>();

        <T> void put(TypeReference<T> tr, T value) {
            map.put(tr.type, value);
        }

        <T> T get(TypeReference<T> tr) {
            if(tr.type instanceof Class<?>) {
                return ((Class<T>)tr.type).cast(map.get(tr.type));
            } else {
                // TypeReference<List<Ineger>>
                return  ((Class<T>)((ParameterizedType)tr.type).getRawType()).cast(map.get(tr.type));
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
    }

    public static void main(String[] args) {

        TypesafeMap m = new TypesafeMap();
        TypeReference<Integer> trInt = new TypeReference<Integer>(){};
        TypeReference<String> trString = new TypeReference<String>(){};
        TypeReference<List<Integer>> trListInteger =  new TypeReference<List<Integer>>(){};
        TypeReference<List<String>> trListString =  new TypeReference<List<String>>(){};
        TypeReference<List<List<String>>> trListStringInList =  new TypeReference<List<List<String>>>(){};
        TypeReference<Map<String, String>> trMap =  new TypeReference<Map<String, String>>(){};

        m.put(trInt, 1);
        m.put(trString, "String");
        m.put(trListInteger, Arrays.asList(1,2,3));
        m.put(trListString, Arrays.asList("A","B","C"));
        m.put(trListStringInList, Arrays.asList(Arrays.asList("a"),
                Arrays.asList("b","c"), Arrays.asList("e", "f", "g")));
        Map<String, String> map = new HashMap<>();
        map.put("key1","value1");
        map.put("key2","value2");
        m.put(trMap, map);

        log.info("Integer:{}", m.get(trInt));
        log.info("String:{}", m.get(trString));
        log.info("List:{}", m.get(trListInteger));
        log.info("List:{}", m.get(trListString));
        log.info("List:{}", m.get(trListStringInList));
        log.info("List:{}", m.get(trMap));

        // Resolvable, Spring 4.0 higher
        // 활용법
        // ResolvableType rt = ResolvableType.forClass(TypeReference.class);
        ResolvableType rt = ResolvableType.forInstance(new TypeReference<List<String>>(){});
        System.out.println("-------------------------------------");
        System.out.println("Type Info");
        // 타입정보
        System.out.println(rt.getSuperType().getGeneric(0).getType());
        // 2번째 타입정보
        System.out.println(rt.getSuperType().getGeneric(0).getNested(2).getType());
        System.out.println(rt.getSuperType().hasUnresolvableGenerics());

    }
}
