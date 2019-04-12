package sample;
import java.awt.image.BufferedImage;

public class Filter {

    public BufferedImage applyMeanFilter(BufferedImage bimg, int maskSize){
        int rgb;
        double r=0,g=0,b=0;
        BufferedImage result = bimg;
        Mask mask = new Mask(maskSize);
        mask.setMeanMask();
        for (int i = 0; i < bimg.getWidth() - maskSize + 1; i++){
            for (int j = 0; j < bimg.getHeight() - maskSize + 1; j++){
                for (int k = 0; k < maskSize; k++){
                        for ( int m = 0; m < maskSize; m++){
                            rgb = bimg.getRGB(i+k, j+m);
                            int rTemp = ColorUtilities.getRed(rgb);
                            int gTemp = ColorUtilities.getGreen(rgb);
                            int bTemp = ColorUtilities.getBlue(rgb);
                            r = (r + (rTemp * mask.getMatrix()[k][m]));
                            g = (g + (gTemp * mask.getMatrix()[k][m]));
                            b = (b + (bTemp * mask.getMatrix()[k][m]));
                        }
                    }
                    result.setRGB(mask.getCenterX(),mask.getCenterY(), ColorUtilities.createRGB((int)r,(int)g,(int)b));
                    mask.moveY();
                    r=0;g=0;b=0;
            }
            mask.restetY();
            mask.moveX();
        }
        return result;
    }

    public BufferedImage applyMedianFilter(BufferedImage bimg, int maskSize){
        BufferedImage result = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());
        return result;
    }

    public  BufferedImage applyWeightedMedianFilter(BufferedImage bimg){
        BufferedImage result = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());
        return result;
    }

    public  BufferedImage applyGaussFilter(BufferedImage bimg, int sigma, int mu, int maskSize){
        BufferedImage result = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());
        return result;
    }

    public  BufferedImage enhance(BufferedImage bimg){
        BufferedImage result = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());
        return result;
    }


}
