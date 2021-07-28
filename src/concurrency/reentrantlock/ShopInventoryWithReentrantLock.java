package concurrency.reentrantlock;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ShopInventoryWithReentrantLock {
    public static void main(String[] args) {
        /**
         * Scenario: A structure (Inventory Database) shared by multiple threads. Some just read the values
         * and one that write periodically to the shared structure.
         *
         * Massive reads and just not often writes
         */

        final int HIGHEST_PRICE = 1000;
        Random random = new Random();

        InventoryDatabase inventoryDatabase = new InventoryDatabase();

        // populate the tree
        for(int i=0; i<HIGHEST_PRICE; i++){
            inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        }

        // Writer thread
        Thread writer = new Thread(() -> {
            while (true){
                inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
                inventoryDatabase.removeItem(random.nextInt(HIGHEST_PRICE));

                try {
                    Thread.sleep(TimeUnit.MILLISECONDS.toMillis(10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        writer.setDaemon(true);
        writer.start();

        // Readers
        final int NBR_READERS = 8;
        List<Thread> readers = new ArrayList<>();

        for(int i=0; i<NBR_READERS; i++){
            Thread reader = new Thread(() -> {
                for(int readIdx=0; readIdx < 100000; readIdx++){
                    int upperBound = random.nextInt(HIGHEST_PRICE);
                    int lowerBound = upperBound > 0 ? random.nextInt(upperBound) : 0;
                    int sum = inventoryDatabase.getNumberOfPricesInPriceRange(lowerBound, upperBound);
                }
            });
            reader.setDaemon(true);
            readers.add(reader);
        }

        // Benchmark
        long startOperation = System.currentTimeMillis();
        for(Thread reader: readers){
            reader.start();
        }

        for(Thread reader: readers){
            try {
                reader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endOperation = System.currentTimeMillis();

        System.out.printf("Read operation from inventory took %d ms", endOperation-startOperation);
    }

    public static class InventoryDatabase {
        /**
         * Using the ReentrantLock
         *
         * only one thread will be in the critical zone (lock ... unlock)
         *
         * Reading threads will be blocked each other!
         *
         */

        private final TreeMap<Integer, Integer> numberItemsOfPriceMap = new TreeMap<>();
        Lock lock = new ReentrantLock();

        // reader
        public int getNumberOfPricesInPriceRange(int lowerBound, int upperBound){
            lock.lock();
            try {

                Integer fromKey = numberItemsOfPriceMap.ceilingKey(lowerBound);
                Integer toKey = numberItemsOfPriceMap.floorKey(upperBound);

                if (fromKey == null || toKey == null) {
                    return 0;
                }

                if (fromKey > toKey){
                    toKey = fromKey + 1;
                }

                NavigableMap<Integer, Integer> rangeOfPrices = numberItemsOfPriceMap.subMap(fromKey, true, toKey, true);
                int sum = 0;
                for (Integer numberItemsForPrice : rangeOfPrices.values()) {
                    sum += numberItemsForPrice;
                }

                return sum;
            }finally {
                lock.unlock();
            }
        }

        // writer (add)
        public void addItem(int item){
            lock.lock();
            try {
                numberItemsOfPriceMap.merge(item, 1, Integer::sum);
            }finally {
                lock.unlock();
            }
        }
        // writer (remove)
        public void removeItem(int item){
            lock.lock();
            try {
                Integer numberOfItems = numberItemsOfPriceMap.get(item);
                if(numberOfItems == null || numberOfItems == 1){
                    numberItemsOfPriceMap.remove(item);
                }else {
                    numberItemsOfPriceMap.put(item, numberOfItems-1);
                }
            }finally {
                lock.unlock();
            }
        }
    }
}
