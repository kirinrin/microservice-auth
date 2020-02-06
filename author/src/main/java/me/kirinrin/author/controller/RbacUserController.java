package me.kirinrin.author.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理RbacUser的入口
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class RbacUserController {

    /**
     * Test string.
     *
     * @return the string
     */
    @GetMapping("/test")
    public String test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User principal = (User) authentication.getPrincipal();

        String username = principal.getUsername();

        log.info("current username: 【 {} 】", username);
        return "success";
    }
}
