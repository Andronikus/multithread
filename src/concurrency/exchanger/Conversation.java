package concurrency.exchanger;

import java.util.concurrent.Exchanger;

public class Conversation {
    public static void main(String[] args) {
        Exchanger<String> sillyTalk = new Exchanger<>();

        new Duke(sillyTalk).start();
        new CoffeeShop(sillyTalk).start();
    }

    private static class Duke extends Thread{
        private final Exchanger<String> sillyTalk;

        public Duke(Exchanger<String> sillyTalk) {
            this.sillyTalk = sillyTalk;
        }

        @Override
        public void run() {
            try {
                String reply = sillyTalk.exchange("Knock knock");
                System.out.println("Coffee Shop: " + reply);

                reply = sillyTalk.exchange("Duke");
                System.out.println("Coffee Shop: " + reply);

                sillyTalk.exchange("The one that was born in this coffee shop");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class CoffeeShop extends Thread {
        private final Exchanger<String> sillyTalk;

        CoffeeShop(Exchanger<String> sillyTalk){
            this.sillyTalk = sillyTalk;
        }

        @Override
        public void run() {
            try {
                String reply = sillyTalk.exchange("Who's there?");
                System.out.println("Duke: " + reply);

                reply = sillyTalk.exchange("Duke who?");
                System.out.println("Duke: " + reply);

                reply = sillyTalk.exchange("");
                System.out.println("Duke: " + reply);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
