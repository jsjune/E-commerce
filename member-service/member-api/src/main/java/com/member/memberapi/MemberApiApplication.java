package com.member.memberapi;

import com.member.membercore.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AppConfig.class)
public class MemberApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberApiApplication.class, args);
    }
}
