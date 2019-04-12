package sample;
import java.awt.image.BufferedImage;

public class Filter {

    public BufferedImage applyMeanFilter(BufferedImage bimg, int maskSize){

        Mask mask = new Mask(maskSize);
        mask.setMeanMask();
        BufferedImage result = applyConvolution(bimg,mask);
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

    public  BufferedImage enhanceEdges(BufferedImage bimg, int maskSize){
        Mask mask = new Mask(maskSize);
        mask.setHighPassFilterMask();
        BufferedImage result = applyConvolution(bimg,mask);
        return result;
    }

    private BufferedImage applyConvolution(BufferedImage bimg, Mask mask){
        int rgb, red, green, blue;
        BufferedImage result = bimg;
        int widthLimit = bimg.getWidth() - mask.getSize();
        int heightLimit = bimg.getHeight() - mask.getSize();
        Image temp = new Image(bimg);
        int[][] redChannel = temp.getRedDataMatrixChannel();
        int[][] greenChannel = temp.getGreenDataMatrixChannel();
        int[][] blueChannel = temp.getBlueDataMatrixChannel();
        mask.setMeanMask();
        red = 0;
        green = 0;
        blue = 0;

        for (int i = 0; i <= widthLimit; i++) {
            for (int j = 0; j <= heightLimit; j++) {
                for (int k = 0; k < mask.getSize(); k++) {
                    for (int l = 0; l < mask.getSize(); l++) {
                        rgb = bimg.getRGB(i+k,j+l);
                        red += (ColorUtilities.getRed(rgb) * mask.getValue(k,l));
                        green += (ColorUtilities.getGreen(rgb) * mask.getValue(k,l));
                        blue += (ColorUtilities.getBlue(rgb) * mask.getValue(k,l));
                    }
                }
                redChannel[i + mask.getCenter()][j + mask.getCenter()] = red;
                greenChannel[i + mask.getCenter()][j + mask.getCenter()] = green;
                blueChannel[i + mask.getCenter()][j + mask.getCenter()] = blue;
                red = 0;
                green = 0;
                blue = 0;
            }
        }

        // Relleno la imagen a devolver
        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {
                result.setRGB(i,j,ColorUtilities.createRGB(redChannel[i][j], greenChannel[i][j], blueChannel[i][j]));
            }
        }

        return result;
    }


}
