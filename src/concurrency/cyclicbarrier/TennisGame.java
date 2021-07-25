package concurrency.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class TennisGame {
    public static void main(String[] args) {
        // barrier - 4 players (Threads) When all the 4 players arrived to this barrier
        // the mix double tennis game starts
        CyclicBarrier startGame = new CyclicBarrier(4,new MixDoubleTennisGame());

        new Player(startGame, "James");
        new Player(startGame, "Laura");
        new Player(startGame, "Matheus");
        new Player(startGame, "Johanna");
    }

    private static class Player extends Thread {
        CyclicBarrier barrier;

        Player(CyclicBarrier barrier, String name){
            this.barrier = barrier;
            this.setName(name);
            this.start();
        }

        @Override
        public void run() {
            long timeToArriveRandom = Math.round(Math.random()*10);
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1 + timeToArriveRandom));
                System.out.println("Player " + getName() + " is ready!");
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    private static class MixDoubleTennisGame extends Thread {
        @Override
        public void run() {
            System.out.println("All players are ready! game starts... \n");
        }
    }
}
