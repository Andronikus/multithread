package performance.latency.recolorimage;

import java.awt.image.BufferedImage;

public class SingleThreadRecolorImage extends RecolorImage{
    public SingleThreadRecolorImage(BufferedImage originalImage) {
        super(originalImage);
    }

    public void recolorImage(){
        super.recolorImage(0,0,getOriginalImage().getWidth(), getOriginalImage().getHeight());
    }
}
