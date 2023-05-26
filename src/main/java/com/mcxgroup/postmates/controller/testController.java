package com.mcxgroup.postmates.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.ReentrantLock;

@RestController
public class testController {
    @GetMapping("/t")
    public String ret(){
        return "hello";
    }

    private ReentrantLock lock = new ReentrantLock(true);
}
