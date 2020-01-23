package com.example.lr1.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("lr1")
    public String lr1(){
        return "index";
    }
}
