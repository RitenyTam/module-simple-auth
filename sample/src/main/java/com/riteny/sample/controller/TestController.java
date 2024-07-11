package com.riteny.sample.controller;

import com.riteny.config.anno.SimpleAuthResolverAnno;
import com.riteny.simpleauth.SimpleAuthService;
import com.riteny.simpleauth.entity.SimpleAuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private SimpleAuthService simpleAuthService;

    @GetMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) throws LoginException {
        return simpleAuthService.login(username, password);
    }

    @GetMapping("/login2")
    public String login2(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("time") Long timestamp) throws LoginException {
        return simpleAuthService.login(username, password, timestamp);
    }

    @GetMapping("/save")
    public void save(@RequestParam("username") String username, @RequestParam("password") String password) {
        simpleAuthService.register(username, password);
    }

    @GetMapping("/token")
    public SimpleAuthUser testGet(@SimpleAuthResolverAnno SimpleAuthUser simpleAuthUser) {
        return simpleAuthUser;
    }
}
