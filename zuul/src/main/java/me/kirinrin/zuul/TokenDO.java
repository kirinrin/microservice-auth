package me.kirinrin.zuul;

import lombok.Data;

import java.io.Serializable;

/**
 * @Classname TokenPo
 * @Description 用于缓正在rdids的token类
 * @Date 2020/3/12 2:36 下午
 * @author by Kirinrin
 */
@Data
public class TokenDO implements Serializable {
    public String token;
    public String user;
}
