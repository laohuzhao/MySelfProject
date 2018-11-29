package com.rsa.rsademo.RSATEST.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    /**
     * 如果不使用注解@WebFilter，就这样配置
     * */
    @Bean
    public FilterRegistrationBean registFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CharactorFilter());
        registration.addUrlPatterns("/*");//过滤所有接口
        registration.addInitParameter("exclusions","/login");//排除登录接口
        registration.setName("CharactorFilter");
        registration.setOrder(1);
        return registration;
    }
   /* @Bean统一管理过滤器，可以配置多个过滤器，过滤器的作用不同
    public FilterRegistrationBean registFilter1() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CharactorFilter());
        registration.addUrlPatterns("/*");//过滤所有接口
        registration.addInitParameter("exclusions","/login");//排除登录接口
        registration.setName("CharactorFilter");
        registration.setOrder(1);
        return registration;
    }*/

}
