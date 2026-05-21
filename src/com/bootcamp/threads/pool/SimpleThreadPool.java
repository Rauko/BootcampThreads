package com.bootcamp.threads.pool;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SimpleThreadPool implements CustomExecutor {
    private final Queue<Runnable> tasks = new LinkedList<>();
    private final List<Worker> workers = new LinkedList<>();
    private boolean isShutdown;

    public SimpleThreadPool(int threadCount) {
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be positive");
        }

        for (int i = 1; i <= threadCount; i++) {
            Worker worker = new Worker("custom-pool-worker-" + i);
            workers.add(worker);
            worker.start();
        }
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        synchronized (tasks) {
            if (isShutdown) {
                throw new IllegalStateException("Thread pool is already shut down");
            }

            tasks.add(task);
            tasks.notify();
        }
    }

    @Override
    public void shutdown() {
        synchronized (tasks) {
            isShutdown = true;
            tasks.notifyAll();
        }
    }

    public void awaitTermination() throws InterruptedException {
        for (Worker worker : workers) {
            worker.join();
        }
    }

    private final class Worker extends Thread {
        private Worker(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                Runnable task;

                synchronized (tasks) {
                    while (tasks.isEmpty() && !isShutdown) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    if (tasks.isEmpty() && isShutdown) {
                        return;
                    }

                    task = tasks.poll();
                }

                try {
                    task.run();
                } catch (RuntimeException e) {
                    System.out.println(Thread.currentThread().getName()
                            + " caught task error: " + e.getMessage());
                }
            }
        }
    }
}
