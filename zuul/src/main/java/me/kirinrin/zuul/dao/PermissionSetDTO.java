package me.kirinrin.zuul.dao;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Classname PermissionSetDTO
 * @Description 封装存储在redis中的数据, 以及经过正则变换的正则
 * @Date 2020/3/16 2:29 下午
 * @author by Kirinrin
 */
@Data
public class PermissionSetDTO {
    private String uri;
    private String key;
    private String uriRe;
    private Set<String> valueIds;

}
