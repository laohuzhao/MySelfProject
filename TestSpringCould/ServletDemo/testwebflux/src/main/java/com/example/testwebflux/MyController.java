package com.example.testwebflux;


import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@EnableScheduling
public class MyController {



    @GetMapping("/")
    public String index() {
        return "index";
    }
}
