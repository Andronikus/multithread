package performance.latency.recolorimage;

import performance.latency.recolorimage.util.ImageUtils;

import java.awt.image.BufferedImage;

public abstract class RecolorImage {
    private final BufferedImage originalImage;
    private final BufferedImage resultImage;

    public RecolorImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
        this.resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),BufferedImage.TYPE_INT_RGB);
    }

    public BufferedImage getResultImage() {
        return resultImage;
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public void recolorImage(int leftCorner, int topCorner, int width, int height){
        for(int x = leftCorner; x < (leftCorner + width) && x < originalImage.getWidth(); x++){
            for(int y = topCorner; y < (topCorner + height) && y < originalImage.getHeight(); y++){
                recolorPixel(x,y);
            }
        }
    }

    private void recolorPixel(int x, int y){
        int pixelRgb = originalImage.getRGB(x,y);

        int originalRed = ImageUtils.getRed(pixelRgb);
        int originalGreen = ImageUtils.getGreen(pixelRgb);
        int originalBlue = ImageUtils.getBlue(pixelRgb);

        int newRed;
        int newGreen;
        int newBlue;

        if(ImageUtils.isShadeOfGray(originalRed, originalGreen, originalBlue)){
            newRed = Math.min(255, originalRed + 10);
            newGreen = Math.max(0, originalGreen - 80);
            newBlue = Math.max(0, originalBlue - 20);
        }else {
            newRed = originalRed;
            newGreen = originalGreen;
            newBlue = originalBlue;
        }

        int newRgb = ImageUtils.createRGBAColors(newRed, newGreen, newBlue, 0xFF0000);
        setRGB(resultImage, x, y,newRgb);
    }

    private void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x,y,image.getColorModel().getDataElements(rgb,null));
    }
}
