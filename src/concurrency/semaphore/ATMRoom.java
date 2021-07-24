package concurrency.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ATMRoom {

    public static void main(String[] args) {
        // simulate a atm room with two atm machines
        Semaphore atmRoom = new Semaphore(2);

        new Person(atmRoom, "Rock");
        new Person(atmRoom, "Laura");
        new Person(atmRoom, "James");
        new Person(atmRoom, "Mathew");
        new Person(atmRoom, "Arnold");
        new Person(atmRoom, "Victor");
    }

    private static class Person extends Thread {
        private final String name;
        private final Semaphore atmRoom;

        public Person(Semaphore atmRoom, String name) {
            this.name = name;
            this.atmRoom = atmRoom;
            this.setName(name);
            this.start();
        }

        @Override
        public void run() {
            try {
                System.out.format("%s is waiting to withdraw cash!\n", name);
                atmRoom.acquire();
                System.out.format("%s is withdraw cash!\n", name);
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
                atmRoom.release();
                System.out.format("%s is happy with cash!\n", name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
