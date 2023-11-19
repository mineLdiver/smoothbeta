package net.mine_diver.smoothbeta.client.render;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

public class UpdateThread extends Thread {
    private static final Object LOCK = new Object();
    private static final Queue<Runnable> TASKS = new ConcurrentLinkedQueue<>();

    public static void enqueue(Runnable task) {
        TASKS.add(task);
        LOCK.notifyAll();
    }

    public UpdateThread() {
        setDaemon(true);
    }

    @Override
    public void run() {
//        Executors.newSingleThreadExecutor().execute();
//        while (true) {
//            try {
//                LOCK.wait();
//            } catch (InterruptedException e) {
//                continue;
//            }
//            Runnable task;
//            while ((task = TASKS.poll()) != null) task.run();
//        }
    }
}
