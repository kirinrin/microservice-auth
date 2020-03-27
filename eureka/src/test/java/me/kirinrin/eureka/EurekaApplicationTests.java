package me.kirinrin.eureka;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

class EurekaApplicationTests {

    @Test
    void contextLoads() {
        String[] str = new String[] { "yang", "hao" };
        List list = Arrays.asList(str);
        list.add("haoB");
    }

    @Test
    void IntegerTest(){
        Integer a = 3;
        Integer b = 3;
        Integer x = 273;
        Integer y = 273;
        Assertions.assertEquals(a,b);
        Assertions.assertTrue(a == b );
        Assertions.assertTrue( x != y);
    }

    @Test
    void arrayTest(){
        String[] array = {"a", "b", "c"};
        List list = Arrays.asList(array);
        array[0] = "y";

        System.out.println(list.get(0));
    }
}
