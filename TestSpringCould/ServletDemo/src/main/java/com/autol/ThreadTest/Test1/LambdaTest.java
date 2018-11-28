package com.autol.ThreadTest.Test1;

import org.junit.jupiter.api.Test;

public class LambdaTest {
    //所需接口
    interface MathOperation {
        int operate(int a, int b);
    }

    @Test
    public void testPlus() {
        // 不用类型声明
        MathOperation plus = (a, b) -> a + b;
        System.out.println(plus.operate(1, 2));
    }

    @Test
    public void testSub() {
        // 类型声明, 没有大括号以及返回语句
        MathOperation sub = (int a, int b) -> a - b;
        System.out.println(sub.operate(2, 1));
    }

    @Test
    public void testMulti() {
        // 用大括号及返回语句
        MathOperation multi = (int a, int b) -> {
            return a * b;
        };
        System.out.println(multi.operate(1, 2));
    }
}
