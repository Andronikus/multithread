package concurrency.countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RunningRaceStarter {
    public static void main(String[] args) {
        final int COUNTER = 5;
        CountDownLatch starter = new CountDownLatch(COUNTER);

        // Runners
        new Runner(starter, "James");
        new Runner(starter, "Laura");
        new Runner(starter, "Matheus");
        new Runner(starter, "Jetty");

        long raceStartCounter = starter.getCount();

        System.out.println("Racing is starting in...");
        while (raceStartCounter > 0){
            try {
                System.out.printf("%d\n", raceStartCounter);
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));

                if(raceStartCounter == 1){
                    System.out.println("GO!");
                }
                starter.countDown();
                raceStartCounter = starter.getCount();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static class Runner extends Thread {
        private final CountDownLatch starter;

        private Runner(CountDownLatch starter, String name) {
            this.starter = starter;
            this.setName(name);
            this.start();
        }

        @Override
        public void run() {
            try {
                starter.await();
                System.out.printf("%s start the race\n", getName());
                Thread.sleep(TimeUnit.SECONDS.toMillis(5 + Math.round(Math.random()*5)));
                System.out.printf("%s cross the finish line\n", getName());
            } catch (InterruptedException e) {
                System.out.printf("Ouch! %s felt down!\n", getName());
            }
        }
    }
}
