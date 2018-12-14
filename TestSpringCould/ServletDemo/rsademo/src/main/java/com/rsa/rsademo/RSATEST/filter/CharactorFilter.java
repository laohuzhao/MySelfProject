package com.rsa.rsademo.RSATEST.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebFilter(urlPatterns = "/*", filterName = "logFilter2")//配置过滤地址
public class CharactorFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("初始化过滤器");
    }

    @Override
    public void doFilter(ServletRequest Request, ServletResponse Response, FilterChain filterChain) throws IOException, ServletException {
        Request.setCharacterEncoding("UTF-8");
        Response.setCharacterEncoding("UTF-8");
        System.out.println("过滤开始");
        HttpServletRequest r=(HttpServletRequest)Request;

       String headerStr= r.getHeader("header");
       if ("token".equals(headerStr)){
           System.out.println("请求头"+headerStr);
           filterChain.doFilter(Request,Response);
       }else {
           HttpServletResponse s=(HttpServletResponse) Response;
           PrintWriter out = s.getWriter();
           out.write("404");
           out.flush();
       }

    }

    @Override
    public void destroy() {
        System.out.println("过滤销毁");
    }
}