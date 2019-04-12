package sample;
import java.awt.image.BufferedImage;

public class Filter {

    public BufferedImage applyMeanFilter(BufferedImage bimg, int maskSize){
        int rgb,r,g,b;
        BufferedImage result = bimg;
        Mask mask = new Mask(maskSize);
        mask.setMeanMask();
        System.out.println("Size: " + bimg.getWidth() + " x " + bimg.getHeight());
        double value = mask.getMatrix()[mask.getCenterX()][mask.getCenterY()];
        for (int i = 0; i < bimg.getWidth() - maskSize; i++){
            for (int j = 0; j < bimg.getHeight() - maskSize; j++){
                    rgb = bimg.getRGB(mask.getCenterX(), mask.getCenterY());
                    r = ColorUtilities.getRed(rgb);
                    g = ColorUtilities.getGreen(rgb);
                    b = ColorUtilities.getBlue(rgb);
                    System.out.print("Position: " + i + "," + j);
                    System.out.print(" Changing R: " + r + " B: " + b + " G: " + g);
                    r = (int) (r + (r * value));
                    g = (int) (g + (g * value));
                    b = (int) (b + (b * value));
                    //Algunos valores se van de rango, pongo esta validacion para truncarlos, no se si esta bien
                    if(r>255){
                        r = 255;
                    }
                    if(g>255){
                        g = 255;
                    }
                    if(b>255){
                        b = 255;
                    }
                    System.out.println(" To R: " + r + " B: " + b + " G: " + g);
                    result.setRGB(i,j, ColorUtilities.createRGB(r,g,b));
                    mask.moveY();
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
