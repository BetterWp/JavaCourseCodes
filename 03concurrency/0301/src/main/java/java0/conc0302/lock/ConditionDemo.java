package java0.conc0302.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ConditionDemo {
    final Lock lock = new ReentrantLock();
    final Condition notFull = lock.newCondition();
    final Condition notEmpty = lock.newCondition();

    final Object[] items = new Object[20];
    int putptr = 0, takeptr = 0, count = 0;

    public void put(Object x) throws InterruptedException {
        lock.lock();
        try {
            // 当count等于数组的大小时，当前线程等待，直到notFull通知，再进行生产
            while (count < items.length) {
                items[putptr] = x;
                System.out.println(Thread.currentThread().getName() + " product " + putptr++);
                ++count;
                notEmpty.signal();

            }
            putptr = 0;
            System.out.println("货仓已满，等待消费");
            notFull.await();
        } finally {
            lock.unlock();
        }
    }

    public Object take() throws InterruptedException {
        lock.lock();
        try {
            // 当count为0，进入等待，直到notEmpty通知，进行消费。
            Object x = new Object();
            while (count > 0) {
                x = items[takeptr];
                System.out.println(Thread.currentThread().getName() + " consume " + takeptr++);
                --count;
                notFull.signal();

            }
            takeptr = 0;
            System.out.println("货仓已空，等待生产");
            notEmpty.await();
            return x;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ConditionDemo conditionDemo = new ConditionDemo();
        new Thread(() -> {
            try {
                conditionDemo.put(new Object());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();
        Thread.sleep(1000);
        new Thread(() -> {
            try {
                conditionDemo.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
        Thread.sleep(1000);
        Thread.currentThread().getThreadGroup().list();
    }
}