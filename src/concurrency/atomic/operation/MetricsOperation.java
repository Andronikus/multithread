package concurrency.atomic.operation;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MetricsOperation {
    public static void main(String[] args) {
        Metrics metrics = new Metrics();

        CaptureMetric serverA = new CaptureMetric(metrics);
        CaptureMetric serverB = new CaptureMetric(metrics);

        PrintMetrics printMetrics = new PrintMetrics(metrics);

        serverA.start();
        serverB.start();
        printMetrics.start();
    }
}

class Metrics {
    private long captureNumber = 0;
    // double primitive type is not guarantee to be atomic operation. volatile will make it to be atomic
    private volatile double average = 0.0;

    // when shared by multiple threads this piece of code should be synchronized
    public synchronized void addCapture(long capture) {
        // all this calculations will be executed by one thread at a time
        double currentValue = average * captureNumber;
        captureNumber++;
        average = (currentValue + capture) / captureNumber;
    }

    // reference attribution is atomic and average (double) is was already made atomic with volatile keyword
    public double getAverage() {
        return average;
    }
}

class CaptureMetric extends Thread{
    Metrics metrics;
    Random random = new Random();

    CaptureMetric(Metrics metrics){
        this.metrics = metrics;
    }

    @Override
    public void run() {
        while (true){
            try {
                long start = System.currentTimeMillis();
                Thread.sleep(random.nextInt(20));
                long end = System.currentTimeMillis();
                metrics.addCapture(end-start);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class PrintMetrics extends Thread{
    Metrics metrics;

    PrintMetrics(Metrics metrics){
        this.metrics = metrics;
    }

    @Override
    public void run() {
        while (true){
            System.out.printf("metric average: %f \n", metrics.getAverage());
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
