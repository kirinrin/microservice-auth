package me.kirinrin.zuul;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Classname RedisTest
 * @Description 测试Redis的基本操作和使用
 * @Date 2020/3/12 1:47 下午
 * @Created by Kirinrin
 */
@SpringBootTest
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 测试 K-V的键值对
     */
    @Test
    public void testKVString() {
        stringRedisTemplate.opsForValue().set("aab", "111");
        assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
    }

    @Test
    public void testHash(){

    }

    @Autowired
    TokenAuthorityService service;

    @Test
    void testRedabound(){
//        BoundSetOperations bound = redisTemplate.boundSetOps("access_token");
//        bound.members().forEach(v -> System.out.println("添加新值后查看所有的值:" + v));

        String token = "access_token:jiangkun:XwwFDQMOTF8XVFdZUh4FAg8MHAkFAFtEVQoCUhZbXFINC1JQ";
        Set<String> set =  stringRedisTemplate.keys("access_token:jiangkun:*");
        set.forEach(key -> System.out.println("key = " + key));
        System.out.println("----------------****----------- " + set.size());
    }

}
