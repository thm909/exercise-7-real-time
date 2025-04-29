package org.example;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class DeadlockFree implements Runnable {
    private static final ReentrantLock lock1 = new ReentrantLock();
    private static final ReentrantLock lock2 = new ReentrantLock();
    private final Random random = new Random();

    public static void main(String[] args) {
        Thread t1 = new Thread(new DeadlockFree(), "thread-1");
        Thread t2 = new Thread(new DeadlockFree(), "thread-2");

        t1.start();
        t2.start();
    }

    public void run() {
        for (int i = 0; i < 10000; i++) {
            boolean order = random.nextBoolean();
            if (order) {
                tryLockBoth(lock1, lock2);
            } else {
                tryLockBoth(lock2, lock1);
            }
        }
    }

    private void tryLockBoth(ReentrantLock first, ReentrantLock second) {
        try {
            if (first.tryLock(50, TimeUnit.MILLISECONDS)) {
                System.out.println("[" + Thread.currentThread().getName() + "] Locked first lock");
                try {
                    Thread.sleep(10); // simulate some work
                    if (second.tryLock(50, TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println("[" + Thread.currentThread().getName() + "] Locked second lock");
                        } finally {
                            second.unlock();
                        }
                    } else {
                        System.out.println("[" + Thread.currentThread().getName() + "] Could not lock second lock, releasing first");
                    }
                } finally {
                    first.unlock();
                }
            } else {
                System.out.println("[" + Thread.currentThread().getName() + "] Could not lock first lock");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
