package com.autol.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@RefreshScope
@RequestMapping
@MapperScan("com.autol.demo")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Value("${spring.datasource.url}")
    private String fromValue;

    @Autowired
    private UserDao userDao;
    /**
     * 返回配置文件中的值
     */
    @GetMapping("/from")
    @ResponseBody
    public String returnFormValue(){
        return fromValue;
    }
    @GetMapping("/from1")
    @ResponseBody
    public String returnFormValue1(int id){
        return userDao.getUserName(id).getName();
    }

}
