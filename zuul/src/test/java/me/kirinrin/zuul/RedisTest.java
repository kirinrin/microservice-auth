package me.kirinrin.zuul;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

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

    @Test
    public void test() throws Exception {
        stringRedisTemplate.opsForValue().set("aaa", "111");
        assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
    }

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

    private UserPo genTestUser(){
        return new UserPo("user-name","psaa", "aa@126.com");
    }

    /**
     * 测试读写一个类
     * 测试给类设置过期时间
     * @throws Exception
     */
    @Test
    public void testObj() throws Exception {
        UserPo user = genTestUser();
        ValueOperations<String, UserPo> operations=redisTemplate.opsForValue();
        operations.set(user.getEmail(), user,1, TimeUnit.SECONDS);
        assertEquals(user.getPassword(), operations.get(user.getEmail()).getPassword());
        Thread.sleep(1000);
        //redisTemplate.delete("com.neo.f");
        boolean exists=redisTemplate.hasKey(user.getEmail());

        assertTrue(! exists);
    }

    @Autowired
    TokenAuthorityService service;


}
