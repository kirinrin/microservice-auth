package me.kirinrin.author.controller;

import lombok.extern.slf4j.Slf4j;
import me.kirinrin.author.entity.Rest;
import me.kirinrin.author.entity.RestBody;
import me.kirinrin.author.entity.SysUser;
import me.kirinrin.author.service.SysUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/login")
public class LoginController {
    @Resource
    private SysUserService sysUserService;

    /**
     * 登录失败返回 401 以及提示信息.
     *
     * @return the rest
     */
    @PostMapping("/failure")
    public Rest loginFailure() {

        return RestBody.failure(HttpStatus.UNAUTHORIZED.value(), "登录失败了，老哥");
    }

    /**
     * 登录成功后拿到个人信息.
     *
     * @return the rest
     */
    @PostMapping("/success")
    public Rest loginSuccess() {
        // 登录成功后用户的认证信息 UserDetails会存在 安全上下文寄存器 SecurityContextHolder 中
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        SysUser sysUser = sysUserService.queryByUsername(username);
        // 脱敏
        sysUser.setEncodePassword("[PROTECT]");
        return RestBody.okData(sysUser,"登录成功");
    }
}