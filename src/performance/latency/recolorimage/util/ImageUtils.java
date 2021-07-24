package performance.latency.recolorimage.util;

public class ImageUtils {

    public static int getRed(int rgb){
        return (rgb & 0xFF0000) >> 16;
    }

    public static int getGreen(int rgb){
        return (rgb & 0x00FF00) >> 8;
    }

    public static int getBlue(int rgb){
        return (rgb & 0x0000FF);
    }

    public static int createRGBAColors(int red, int green, int blue, int alpha){
        int rgb = 0;

        rgb |= red << 16;
        rgb |= green << 8;
        rgb |= blue;
        // alpha value
        rgb |= alpha << 24;

        return rgb;
    }

    public static boolean isShadeOfGray(int red, int green, int blue){
        return Math.abs(red-green) < 30 &&
                Math.abs(red-blue) < 30 &&
                Math.abs(green - blue) < 30;
    }
}
