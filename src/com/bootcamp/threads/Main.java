package com.bootcamp.threads;

import com.bootcamp.threads.pool.SimpleThreadPool;

public class Main {
    private static final long TASK_DURATION_MILLIS = 700;

    public static void main(String[] args) throws InterruptedException {
        SimpleThreadPool threadPool = new SimpleThreadPool(3);

        for (int i = 1; i <= 10; i++) {
            int taskNumber = i;

            threadPool.execute(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " started task " + taskNumber);

                sleep();

                System.out.println(threadName + " finished task " + taskNumber);
            });
        }

        threadPool.shutdown();
        threadPool.awaitTermination();

        System.out.println("All tasks are finished");
    }

    private static void sleep() {
        try {
            Thread.sleep(TASK_DURATION_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
