package com.example.xitong.redis;


import java.applet.Applet;
import java.util.Calendar;


public class test extends Applet {


    public static void main(String[] args) {
        Calendar c=Calendar.getInstance();
        int year=c.get(c.YEAR);
        int month=c.get(c.MONTH)+1;//月+1
        int day=c.get(c.DAY_OF_MONTH);
        int hour=c.get(c.HOUR_OF_DAY);
        int second=c.get(c.SECOND);
        int mintue=c.get(c.MINUTE);
        int week=c.get(c.DAY_OF_WEEK)-1;//星期减1
        System.out.println(year+"年"+month+"月"+day+"日"+hour+"时"+mintue+"分"+second+"秒"+"-星期"+week);
    }
}
