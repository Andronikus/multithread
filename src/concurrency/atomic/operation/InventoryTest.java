package concurrency.atomic.operation;

public class InventoryTest {
    public static void main(String[] args) throws InterruptedException {
        Inventory inventory = new Inventory();

        Manufacture manufacture = new Manufacture(inventory);
        Seller seller = new Seller(inventory);

        manufacture.start();
        seller.start();

        manufacture.join();
        seller.join();

        System.out.printf("items: %d", inventory.getItems());

    }
}

class Manufacture extends Thread {
    private final Inventory inventory;

    public Manufacture(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void run() {
        for(int i=0; i<10000; i++){
            inventory.increment();
        }
    }
}

class Seller extends Thread {
    private final Inventory inventory;

    public Seller(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void run() {
        for(int i=0; i<10000; i++){
            inventory.decrement();
        }
    }
}

// Shared object
class Inventory {
    private long items;

    public synchronized void increment(){
        // ++ operation is not atomic
        // get value + add 1 to value and write value
        items++;
    }

    public synchronized void decrement(){
        // -- operation is not atomic
        // get value + subtract 1 to value and write value
        items--;
    }

    public synchronized long getItems() {
        return items;
    }
}
