package me.kirinrin.author.entity;

import lombok.Data;
/**
 * @author Kirinrin
 */
@Data
public class SysUser {
    private Integer userId;
    private String username;
    private String encodePassword;
    private String permissions;
}
