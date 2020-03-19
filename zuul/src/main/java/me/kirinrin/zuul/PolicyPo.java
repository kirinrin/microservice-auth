package me.kirinrin.zuul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname PolicyPo
 * @Description 封装权限策略组数据
 * @Date 2020/3/12 7:54 下午
 * @Created by Kirinrin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyPo {
    private String action;
    private String effect;
    private String resource;
}
