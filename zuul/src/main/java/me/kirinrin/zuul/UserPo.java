package me.kirinrin.zuul;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname User
 * @Description 用于测试缓存类对像的
 * @Date 2020/3/12 2:11 下午
 * @Created by Kirinrin
 */
@Data
@AllArgsConstructor
public class UserPo implements Serializable {
    private String name;
    private String password;
    private String email;
}
