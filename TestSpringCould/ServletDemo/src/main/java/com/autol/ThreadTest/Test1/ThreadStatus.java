package com.autol.ThreadTest.Test1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThreadStatus {
    public static void main(String[] args) {
        List list=new ArrayList();
        HashMap<Integer,Integer> map=new HashMap();
        for(int i=0;i<10;i++){
            list.add(i);
            map.put(i,i);
        }
        list.forEach(a-> System.out.println(a));
        map.forEach((k,v)->{
            if (k==8){
                System.out.println(v);
            }else {
                System.out.println(k+v);
            }
        });
    }
}
