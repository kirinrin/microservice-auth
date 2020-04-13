package me.kirinrin.zuul;

import org.junit.jupiter.api.Test;

/**
 * @author by Kirinrin
 * @Classname AlibabaTest
 * @Description TODO
 * @Date 2020/4/6 10:52 上午
 */
public class AlibabaTest {
    @Test
    public void switchStringNullTest(){
        method(null);
    }

    public void method(String para){
        switch (para){
            case "sth":
                System.out.println("sth");
                break;
            case "null":
                System.out.println("null");
                break;
            default:
                System.out.println("default");
        }
    }
}
