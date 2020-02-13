package me.kirinrin.author.entity;

import lombok.Data;

@Data
public class SysUser {
    private Integer userId;
    private String username;
    private String encodePassword;
    private Integer age;
}
