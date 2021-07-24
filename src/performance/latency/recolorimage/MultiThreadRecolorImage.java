package performance.latency.recolorimage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MultiThreadRecolorImage extends RecolorImage{
    public MultiThreadRecolorImage(BufferedImage originalImage) {
        super(originalImage);
    }

    public void recolorImage(int nbrThreads){
        if(nbrThreads < 1) throw new RuntimeException("nbrThreads should be a value > 0!");

        int width = getOriginalImage().getWidth();
        int height = getOriginalImage().getHeight() / nbrThreads;

        List<Thread> threads = new ArrayList<>();

        for(int i=0; i<nbrThreads; i++){
            final int imageSection = i;

            Thread newThread = new Thread(() -> {
                recolorImage(0, height* imageSection, width,height);
            });

            threads.add(newThread);
        }

        for(Thread thread: threads){
            thread.start();
        }

        for(Thread thread: threads){
            try{
                thread.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
