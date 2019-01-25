package com.sdk4.biz.aote.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.sdk4.biz.aote.api.sys.Version;
import com.sdk4.biz.aote.api.user.Login;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiFactory implements ApplicationContextAware {

    private static boolean inited = false;
    private static ApplicationContext applicationContext;
    public static ConcurrentMap<String, ApiService> _api = new ConcurrentHashMap<String, ApiService>();
    private static List<String> NOT_REQUIRED_LOGIN = new ArrayList<String>();

    static {
        NOT_REQUIRED_LOGIN.add(Version.METHOD);
        NOT_REQUIRED_LOGIN.add(Login.METHOD);
    }
    
    public static ApiService getApi(String name) {
        return _api.get(name);
    }
    
    public static boolean requiredLogin(String method) {
        return !NOT_REQUIRED_LOGIN.contains(method);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized (ApiFactory.class) {
            if (!inited) {
                // 为什么要执行两次 ???
                ApiFactory.applicationContext = applicationContext;
                String[] apiBeanNames = applicationContext.getBeanNamesForType(ApiService.class);

                System.out.println("============= Load API Service =============");

                for (String name : apiBeanNames) {
                    ApiService service = getBean(name);
                    _api.put(service.method(), service);
                    
                    System.out.println(service.method() + "," + service.getClass().getName());

                    log.info("load api service {}: {}", service.method(), service.getClass().getName());
                }
                inited = true;
            }
        }

    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

}
