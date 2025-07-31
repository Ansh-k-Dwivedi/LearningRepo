package com.example.Coffee_Machine;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Lazy
@Component
@Scope("prototype")
public class LatteMachine implements CoffeeMachine{


    public LatteMachine (){
        System.out.println("Latte Constructor called");
    }
    @Override
    public void makeCoffee() {
        System.out.println("Latte Prepared");
    }

    @PostConstruct
    public void init(){
        System.out.println("Lattemachine Innitialized");

    }


}
