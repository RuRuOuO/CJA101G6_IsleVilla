package com.islevilla;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {

        @RequestMapping("/test")
        public String test() {
            System.out.println("Hi!");
            return "Hello World";
        }

}
