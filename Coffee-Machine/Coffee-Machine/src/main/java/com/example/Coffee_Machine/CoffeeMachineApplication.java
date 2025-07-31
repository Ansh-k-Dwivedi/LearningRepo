package com.example.Coffee_Machine;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication

public class CoffeeMachineApplication {

	public static void main(String[] args) {
		//SpringApplication.run(CoffeeMachineApplication.class, args);

		//ApplicationContext context = SpringApplication.run(CoffeeMachineApplication.class, args);
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		//CoffeeLifecycle  lc1 = context.getBean(CoffeeLifecycle.class);

		CafeService cafe = context.getBean(CafeService.class);
//		System.out.println(cafe.makeCoffee());
		cafe.makeEspresso();


		LatteMachine l1 = context.getBean(LatteMachine.class);
		EspressoMachine l2 = context.getBean(EspressoMachine.class);

		System.out.println("LatteMachine id: " + l1);
		System.out.println("EspressoMachine id: " + l2);


		//context.close();

	}

}

