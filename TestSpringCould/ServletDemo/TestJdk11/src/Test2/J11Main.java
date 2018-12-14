package Test2;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class J11Main {
    public static void main(String[] args) {

        LocalDate date = LocalDate.of(2018, 9, 27);
        //获取年份：
        int year = date.getYear();
        //获取月：
        int month = date.getMonthValue();
        //获取日：
        int day = date.getDayOfMonth();
        //在原来的日期上加N天/月/年
        LocalDate localDate = date.plus(1, ChronoUnit.DAYS);
        //是否是闰年：
        boolean isLeapYear = date.isLeapYear();
        //输出当前日期的字符串
        System.out.println(year+"年"+month+"月"+day+"日"+date+"+1天后是"+localDate+"是否是润年"+isLeapYear);

        //获取当前日期
        LocalDateTime now = LocalDateTime.now();
        //指定的格式输出
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yMdHms");//大写w是这个月的第几周，小写的事这一年的第几周,大写的D代表是这一年的第几天
        System.out.println(now.format(formatter));
    }
}
