package com.bootcamp.threads.pool;

public interface CustomExecutor {
    void execute(Runnable task);

    void shutdown();
}
