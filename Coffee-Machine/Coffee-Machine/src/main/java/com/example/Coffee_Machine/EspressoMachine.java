package com.example.Coffee_Machine;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
@Qualifier("espressoMachine")
public class EspressoMachine implements CoffeeMachine {

    public EspressoMachine(){
        System.out.println("Espresso Constructor called");
    }
    @Override
    public void makeCoffee() {
        System.out.println("Espresso Prepared");
    }

    @PostConstruct
    public void init(){
        System.out.println("Espresso initialized");
    }


}
