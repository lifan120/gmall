package com.atguigu.gmall;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Demo
 *
 * @Author: 李繁
 * @CreateTime: 2020-05-13
 * @Description:
 */

public class Demo {
        public static void main(String[] args) throws Exception {
            Thread add = new AddThread();
            Thread dec = new DecThread();
            add.start();
            dec.start();
            add.join();
            dec.join();
            System.out.println(Counter.count);
            dec.wait();


        }


}


class AddThread extends Thread {
    public void run() {
        for (int i=0; i<10000; i++) { Counter.count += 1; }
    }
}

class DecThread extends Thread {
    public void run() {
        for (int i=0; i<10000; i++) { Counter.count -= 1; }
    }
}
class Counter {
    public  static int count = 0;
}
