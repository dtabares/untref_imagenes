package sample;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class ActiveContours {

    ImageUtilities imageUtilities;
    int [][] phiMatrix;
    BufferedImage bimg; //
    List<Pixel> lin;
    List<Pixel> lout;


    public ActiveContours(Image image, List<Pixel> lin, List<Pixel> lout){
        imageUtilities = new ImageUtilities();
        phiMatrix = new int[image.getWidth()][image.getHeight()];
        this.lin = lin;
        this.lout = lout;
        this.bimg = image.getBufferedImage();
    }

    public void setInitialCurve(){
        /*
        ************ Aca hay que crear el famoso cuadrado y calcular el promedio, actualizar lin y lout y la funcion phi como estado inicial ************
        Algoritmo (realizar n veces para frenarlo por las dudas)
        1. Definir el cuadradito dentro del objeto de interes y definir Lin Lout
        2. Para cada x en Lout si Fd > 0 lo sacamos de Lout y lo ponemos en Lin, luego para cada vecino donde Phy vale 3, hay que agregarlo a lin y actualizarlo en phi como 1
        3. Hecho el paso 2 revisar los pixels de Lin ya que pueden ser ahora puntos interiores al objeto, si es asi se sacan de lin y se maracn en phi como -3
        4. Para cada pixel de lin si Fd < 0 se borran de lin y se agregan a lout se miran los vecinos y si phi vale -3 entonces se agregan a lin y se marcan en phi como 1
        5. Se hace el paso 3 pero al reves
        NOTA: la funcion phi inicial define un cuadradito con todos valores -3, el borde del cuadrado es 1, el borde exterior es -1 y todo el resto es 3
         */
    }

    public void expand(){

    }

    public void contract(){

    }

    public void updatePhiMatrix(){

    }

    public BufferedImage paintContours(){

        BufferedImage bimg = imageUtilities.copyImageIntoAnother(this.bimg);
        //Pinto lin
        for (Pixel p: lin){
            bimg.setRGB(p.getX(),p.getY(), ColorUtilities.createRGB(255,0,0));
        }
        //Pinto lout
        for (Pixel p: lout) {
            bimg.setRGB(p.getX(),p.getY(), ColorUtilities.createRGB(246,219,41));
        }
        return bimg;
    }

    public List<Pixel> getLin() {
        return lin;
    }

    public List<Pixel> getLout() {
        return lout;
    }
}