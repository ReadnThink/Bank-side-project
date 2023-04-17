package com.side.workout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Slf4j
@EnableJpaAuditing
@SpringBootApplication
public class WorkoutApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WorkoutApplication.class, args);
//        String[] iocNames = context.getBeanDefinitionNames();
//        for (String name : iocNames){
//            log.debug("디버그 : iocNames = {}", name);
//        }
    }

}
