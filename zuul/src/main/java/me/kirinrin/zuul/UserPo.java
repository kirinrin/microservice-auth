package me.kirinrin.zuul;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname User
 * @Description 用于封装从redis中读取的token 用户对像
 * @Date 2020/3/12 2:11 下午
 * @author by Kirinrin
 */
@Data
@AllArgsConstructor
public class UserPo implements Serializable {
    private String key;
    private String token;
    private String userName;
    private String displayName;
    private String[] actions;
    private String tenantId;
}
