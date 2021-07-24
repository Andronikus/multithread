package performance.latency.recolorimage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class RecolorImageSingleThreadTest {
    public static final String SOURCE_FILE = "./resources/performance/latency/recolorimage/many-flowers.jpg";
    public static final String DEST_FILE = "./resources/performance/latency/recolorimage/many-flowers_recolored.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));

        final Instant startTime = Instant.now();
        SingleThreadRecolorImage singleThreadRecolorImage = new SingleThreadRecolorImage(originalImage);
        singleThreadRecolorImage.recolorImage();
        final Instant endTime = Instant.now();

        System.out.format("Recolor image with single thread took %d ms", Duration.between(startTime,endTime).toMillis());

        File outputFile = new File(DEST_FILE);
        ImageIO.write(singleThreadRecolorImage.getResultImage(), "jpg", outputFile);
    }
}