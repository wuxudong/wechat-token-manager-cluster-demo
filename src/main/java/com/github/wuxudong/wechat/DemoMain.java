package com.github.wuxudong.wechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.script.ScriptException;

@SpringBootApplication
@EnableScheduling
public class DemoMain {
    public static void main(String[] args) throws ScriptException {
        SpringApplication.run(DemoMain.class);
    }
}
