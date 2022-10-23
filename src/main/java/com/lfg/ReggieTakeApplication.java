package com.lfg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@SpringBootApplication
@ServletComponentScan
public class ReggieTakeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeApplication.class, args);
    }

}
