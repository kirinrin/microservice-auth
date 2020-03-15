package me.kirinrin.zuul;

import lombok.Data;

/**
 * @Classname PolicyPo
 * @Description 封装权限策略组数据
 * @Date 2020/3/12 7:54 下午
 * @Created by Kirinrin
 */
@Data
public class PolicyPo {
    private String action;
    private String effect;
    private String resource;
}
