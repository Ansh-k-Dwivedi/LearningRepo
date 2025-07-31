package com.example.Coffee_Machine;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class CoffeeLifecycle {

    @PostConstruct
    public void init(){
        System.out.println("Coffee Machine has been initialized");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("Shutting down / Destroying coffee machine");
    }
}
