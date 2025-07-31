package com.example.SpringTest.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoOutput {

    @GetMapping("/output")
   public String getOutput() {
        return "Hello, this is a demo output!";
    }
}
