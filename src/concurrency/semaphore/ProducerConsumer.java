package concurrency.semaphore;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumer {

    public static void main(String[] args) {

        Container container = new Container();

        List<Thread> producers = new ArrayList<>();
        List<Thread> consumers = new ArrayList<>();

        Thread monitor = new Monitor(container);

        for(int i=0; i<5; i++){
            producers.add(new Producer(container, "Producer" + i));
        }

        for(int i=0; i<5; i++){
            consumers.add(new Consumer(container, "Consumer" + i));
        }

        for(Thread thread: producers){
            thread.start();
        }

        for(Thread thread: consumers){
            thread.start();
        }

        monitor.start();

        try {
            monitor.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        for (Thread thread: producers){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Producer extends Thread {
        private final Container container;

        Producer(Container container, String name){
            this.container = container;
            this.setName(name);
        }

        @Override
        public void run() {
            for(int i=0; i<3; i++){
                container.sendValue(Thread.currentThread().getName());

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Consumer extends Thread {
        private final Container container;

        Consumer(Container container, String name){
            this.container = container;
            this.setName(name);
            // this.setDaemon(true);
        }

        @Override
        public void run() {
            while(true){
                String value = container.consumeValue();
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Monitor extends Thread {
        private final Container container;

        Monitor(Container container){
            this.container = container;
            this.setName("Monitor");
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while(true){
                boolean getLock = container.getLock().tryLock();

                try {
                    if(getLock){
                        System.out.printf("%s: queue nbr values: %d \n", Thread.currentThread().getName(), container.queue.size());
                    }
                } finally {
                    if(getLock){
                        container.getLock().unlock();
                    }
                }

                try {
                    Thread.sleep(1500);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Container {
        private final int CAPACITY = 5;
        private final Queue<String> queue = new ArrayDeque<>();

        private final Semaphore full = new Semaphore(0);
        private final Semaphore empty = new Semaphore(CAPACITY);

        private final Lock lock = new ReentrantLock();

        public void sendValue(String value){
            try {
                empty.acquire();
                lock.lock();
                queue.offer(value);
                System.out.printf("value %s queued \n", value);
                lock.unlock();
                full.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public String consumeValue(){
            String value = null;
            try {
                full.acquire();
                lock.lock();
                value = queue.poll();
                System.out.printf("value %s consumed \n", value);
                lock.unlock();
                empty.release();
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            return value;
        }

        public Lock getLock() {
            return lock;
        }
    }
}
