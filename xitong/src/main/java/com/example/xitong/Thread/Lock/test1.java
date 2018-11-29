package com.example.xitong.Thread.Lock;



/**
 *
 * 一. synchronized 的局限性 与 Lock 的优点
 * 如果一个代码块被synchronized关键字修饰，当一个线程获取了对应的锁，并执行该代码块时，其他线程便只能一直等待直至占有锁的线程释放锁。
 * 事实上，占有锁的线程释放锁一般会是以下三种情况之一：
 *
 * 占有锁的线程执行完了该代码块，然后释放对锁的占有；
 * 占有锁线程执行发生异常，此时JVM会让线程自动释放锁；
 * 占有锁线程进入 WAITING 状态从而释放锁，例如在该线程中调用wait()方法等。
 * synchronized 是Java语言的内置特性，可以轻松实现对临界资源的同步互斥访问。那么，为什么还会出现Lock呢？试考虑以下三种情况：
 *
 * lock优势：---
 * Case 1 ：
 * ---强制打断等待状态
 * 在使用synchronized关键字的情形下，假如占有锁的线程由于要等待IO或者其他原因（比如调用sleep方法）被阻塞了，
 * 但是又没有释放锁，那么其他线程就只能一直等待，别无他法。这会极大影响程序执行效率。
 * 因此，就需要有一种机制可以不让等待的线程一直无期限地等待下去（比如只等待一定的时间 (解决方案：tryLock(long time, TimeUnit unit))
 * 或者 能够响应中断 (解决方案：lockInterruptibly())），这种情况可以通过 Lock 解决。
 *
 * Case 2 ：
 *---读读操作不冲突不用等待
 * 我们知道，当多个线程读写文件时，读操作和写操作会发生冲突现象，写操作和写操作也会发生冲突现象，
 * 但是读操作和读操作不会发生冲突现象。但是如果采用synchronized关键字实现同步的话，就会导致一个问题，
 * 即当多个线程都只是进行读操作时，也只有一个线程在可以进行读操作，其他线程只能等待锁的释放而无法进行读操作。
 * 因此，需要一种机制来使得当多个线程都只是进行读操作时，线程之间不会发生冲突。同样地，Lock也可以解决这种情况 (解决方案：ReentrantReadWriteLock) 。
 *
 * Case 3 ：
 *---知晓线程是否成功获得了锁
 * 我们可以通过Lock得知线程有没有成功获取到锁 (解决方案：ReentrantLock) ，但这个是synchronized无法办到的。
 *
 * 上面提到的三种情形，我们都可以通过Lock来解决，但 synchronized 关键字却无能为力。
 * 事实上，Lock 是 java.util.concurrent.locks包 下的接口，Lock 实现提供了比 synchronized 关键字 更广泛的锁操作，
 * 它能以更优雅的方式处理线程同步问题。也就是说，Lock提供了比synchronized更多的功能。但是要注意以下几点：
 *lock--synchronized区别---
 * 1）synchronized是Java的关键字，因此是Java的内置特性，是基于JVM层面实现的。
 * 而Lock是一个Java接口，是基于JDK层面实现的，通过这个接口可以实现同步访问；
 *
 * 2）采用synchronized方式不需要用户去手动释放锁，当synchronized方法或者synchronized代码块执行完之后，
 * 系统会自动让线程释放对锁的占用；而 Lock则必须要用户去手动释放锁，如果没有主动释放锁，就有可能导致死锁现象
 * */

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock接口的方法的作用
 * public interface Lock {
 *    1. void lock();//获取锁
 *     首先，lock()方法是平常使用得最多的一个方法，就是用来获取锁。如果锁已被其他线程获取，则进行等待。
 *     在前面已经讲到，如果采用Lock，必须主动去释放锁，并且在发生异常时，不会自动释放锁。
 *     因此，一般来说，使用Lock必须在try…catch…块中进行，并且将释放锁的操作放在finally块中进行，
 *     以保证锁一定被被释放，防止死锁的发生。通常使用Lock来进行同步的话，是以下面这种形式去使用的：
 * Lock lock = ...;
 * lock.lock();
 * try{
 *     //处理任务
 * }catch(Exception ex){
 *
 * }finally{
 *     lock.unlock();   //释放锁
 * }
 *   2.  void lockInterruptibly() throws InterruptedException;  // 获取可以响应中断的锁
 *      lockInterruptibly()方法比较特殊，当通过这个方法去获取锁时，如果线程 正在等待获取锁，则这个线程能够 响应中断，
 *      即中断线程的等待状态。例如，当两个线程同时通过lock.lockInterruptibly()想获取某个锁时，假若此时线程A获取到了锁，
 *      而线程B只有在等待，那么对线程B调用threadB.interrupt()方法能够中断线程B的等待过程。
 *      由于lockInterruptibly()的声明中抛出了异常，
 *      所以lock.lockInterruptibly()必须放在try块中或者在调用lockInterruptibly()的方法外声明抛出 InterruptedException，
 *      但推荐使用后者，原因稍后阐述。因此，lockInterruptibly()一般的使用形式如下：
 *
 * public void method() throws InterruptedException {
 *     lock.lockInterruptibly();
 *     try {
 *      //.....
 *     }
 *     finally {
 *         lock.unlock();
 *     }
 * }
 *    3. boolean tryLock();
 *      boolean tryLock(long time, TimeUnit unit) throws InterruptedException;  // 可以响应中断
 *      tryLock()方法是有返回值的，它表示用来尝试获取锁，如果获取成功，则返回true；如果获取失败（即锁已被其他线程获取），则返回false，
 *      也就是说，这个方法无论如何都会立即返回（在拿不到锁时不会一直在那等待）。
 *      tryLock(long time, TimeUnit unit)方法和tryLock()方法是类似的，只不过区别在于这个方法在拿不到锁时会等待一定的时间，
 *      在时间期限之内如果还拿不到锁，就返回false，同时可以响应中断。如果一开始拿到锁或者在等待期间内拿到了锁，则返回true。
 *       一般情况下，通过tryLock来获取锁时是这样使用的：
 *
 * Lock lock = ...;
 * if(lock.tryLock()) {
 *      try{
 *          //处理任务
 *      }catch(Exception ex){
 *
 *      }finally{
 *          lock.unlock();   //释放锁
 *      }
 * }else {
 *     //如果不能获取锁，则直接做其他事情
 * }
 *     void unlock();//释放锁
 *     Condition newCondition();
 * }
 * */
public class test1 {
    private ArrayList<Integer> arrayList = new ArrayList<Integer>();
    private  Lock lock1 = new ReentrantLock();
    public static void main(String[] args) {
        final test1 test = new test1();

        new Thread("A") {
            public void run() {
                test.insert(Thread.currentThread());
            }
        }.start();

        new Thread("B") {
            public void run() {
                test.insert(Thread.currentThread());
            }
        }.start();
    }

    public void insert(Thread thread) {
       // Lock lock = new ReentrantLock();  // 注意这个地方:lock被声明为局部变量（局部变量每次都是new一个新的，所以不是同一把锁，形不成锁的机制），
        // ReentrantLock是唯一一个实现lock接口的类，并且ReentrantLock提供了更多的方法
        lock1.lock();//使用全局变量lock1就可以解决局部变量不是同一把锁的问题
        try {
            System.out.println("线程" + thread.getName() + "得到了锁...");
            for (int i = 0; i < 5; i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {
            System.out.println("异常");
        } finally {
            System.out.println("线程" + thread.getName() + "释放了锁...");
            lock1.unlock();
        }
    }
}
