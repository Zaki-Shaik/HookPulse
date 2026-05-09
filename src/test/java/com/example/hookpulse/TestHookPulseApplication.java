package com.example.hookpulse;

import org.springframework.boot.SpringApplication;

public class TestHookPulseApplication {

    public static void main(String[] args) {
        SpringApplication.from(HookPulseApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
