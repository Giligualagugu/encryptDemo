package com.kele.enc.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoController {


    @PostMapping("/demo-enc")
    public Object testfilter(@RequestBody Object o) {

        System.out.println("controller 收到:" + o);

        Map<String, Object> map = new HashMap<>();
        map.put("jiaoiao", "ddd");

        return map;
    }
}
