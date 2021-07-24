package performance.latency.recolorimage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class RecolorImageMultiThreadTest {
    public static final String SOURCE_FILE = "./resources/many-flowers.jpg";
    public static final String DEST_FILE = "./resources/out/many-flowers.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        final int NBR_THREADS = 4;

        final Instant startTime = Instant.now();
        MultiThreadRecolorImage multiThreadRecolorImage = new MultiThreadRecolorImage(originalImage);
        multiThreadRecolorImage.recolorImage(NBR_THREADS);
        final Instant endTime = Instant.now();

        System.out.format("Recolor image with %d threads took %d ms", NBR_THREADS, Duration.between(startTime,endTime).toMillis());

        File outputFile = new File(DEST_FILE);
        ImageIO.write(multiThreadRecolorImage.getResultImage(), "jpg", outputFile);
    }
}