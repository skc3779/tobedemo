package kr.kangchun.demo07.type;

import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by skc37 on 2016-12-30.
 */
public class SpringTypeReferenceMain {
    public static void main(String[] args) {
        //String 3.2 이상에서 사용가능함.
        ParameterizedTypeReference type = new ParameterizedTypeReference<List<Map<Set<Integer>, String>>>() {};
        System.out.println(type.getType());
    }
}
