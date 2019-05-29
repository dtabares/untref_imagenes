package sample;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class ActiveContours {

    ImageUtilities imageUtilities;
    int [][] phiMatrix;
    BufferedImage bimg;
    List<Pixel> lin;
    List<Pixel> lout;

    public ActiveContours(BufferedImage bimg){
        imageUtilities = new ImageUtilities();
        this.bimg = bimg;
        phiMatrix = new int[bimg.getWidth()][bimg.getHeight()];
        lin = new LinkedList<>();
        lout = new LinkedList<>();
    }

    public void setInitialCurve(){
        //Aca hay que crear el famoso cuadrado y calcular el promedio, actualizar lin y lout y la funcion phi como estado inicial
    }

    public void expand(){

    }

    public void contract(){

    }

    public BufferedImage paintContours(){

        BufferedImage bimg = imageUtilities.copyImageIntoAnother(this.bimg) ;
        //Pinto lin
        for (Pixel p: lin){
            bimg.setRGB(p.x,p.y, ColorUtilities.createRGB(255,0,0));
        }
        //Pinto lout
        for (Pixel p: lout) {
            bimg.setRGB(p.x,p.y, ColorUtilities.createRGB(0,0,255));
        }
        return bimg;
    }

}