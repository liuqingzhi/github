package com.yesmynet.database.query.controller;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestController
{
    @RequestMapping(value = "/index.do", method = RequestMethod.GET)
    public String search(@RequestParam(required=false) String keytype,@RequestParam(required=false) String keyword,Model model)
    {
        Date now=new Date();
        
        model.addAttribute("nowTime", now);

        return "testIndex";
    }
}
