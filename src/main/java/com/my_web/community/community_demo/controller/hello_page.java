package com.my_web.community.community_demo.controller;

import com.my_web.community.community_demo.DAO.no_dao_test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//
//****************** 返回数据类型如不是String，似乎都是JSON************************//
//

@Controller
@RequestMapping("/alpha")
public class hello_page {
    @RequestMapping("/hello_page")
    @ResponseBody
    public String hello(){
        String s = "Hello Spring Boot.";
        return s;
    }

    @RequestMapping("/hello_page_json")
    @ResponseBody
    public String hello_json(){
        return "Hello Spring Boot.";
    }

    @Autowired
    private no_dao_test no_dao_test_find;
    @RequestMapping("/date")
    @ResponseBody
    public String getda(){
        return no_dao_test_find.find();
    }


    //---------------------**********************-----------------------------------//
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public int students_request1(@RequestParam(name = "id", required = false, defaultValue = "10") int id){
        System.out.println(id);
        return id;
    }

    @RequestMapping(path = "/students/{id}", method = RequestMethod.GET)
    @ResponseBody
    public int students_request2(@PathVariable(name="id", required = false) int id){
        System.out.println(id);
        return id;
    }

    @RequestMapping(path = "/students/post", method = RequestMethod.POST)
    @ResponseBody
    public String students_request3(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 两种html返回方式
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView get_teacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "Oathmeaning");
        mav.addObject("age", "25");
        mav.setViewName("/te_demo/view");
        return mav;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String get_school(Model model) {
        model.addAttribute("name", "上海大学");
        model.addAttribute("age", "100");
        return "/te_demo/view";
    }

    //返回Json 异步操作
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> get_emp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "Oathmeaning");
        emp.put("salary", "5");
        emp.put("age", "25");
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> get_emps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "Oathmeaning");
        emp.put("salary", "5");
        emp.put("age", "25");
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "Oath");
        emp.put("salary", "1");
        emp.put("age", "5");
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "meaning");
        emp.put("salary", "2");
        emp.put("age", "15");
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "Oathmean");
        emp.put("salary", "30");
        emp.put("age", "50");
        list.add(emp);

        return list;
    }
}
