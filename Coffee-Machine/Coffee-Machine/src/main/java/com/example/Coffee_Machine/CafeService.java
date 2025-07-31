package com.example.Coffee_Machine;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
public class CafeService {
    private CoffeeMachine espressoMachine;
    private CoffeeMachine latteMachine;

    @Autowired
    public CafeService(
            @Qualifier("espressoMachine") CoffeeMachine espressoMachine,
            @Qualifier("latteMachine") CoffeeMachine latteMachine){
        this.espressoMachine = espressoMachine;
        this.latteMachine = latteMachine;
    }

    public void makeEspresso(){
        espressoMachine.makeCoffee();

    }

    public void makeLatte(){
        latteMachine.makeCoffee();

    }


}
