package com.riteny.sample;

import com.riteny.config.anno.SimpleAuthArgumentTypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@SpringBootApplication
public class SampleApplication extends WebMvcConfigurerAdapter {

    @Autowired
    private SimpleAuthArgumentTypeResolver simpleAuthArgumentTypeResolver;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(simpleAuthArgumentTypeResolver);
    }
}
