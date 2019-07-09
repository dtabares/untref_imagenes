package sample.tpFinal;

import sample.ColorUtilities;
import sample.Image;
import sample.ImageSelection;
import sample.Pixel;

import java.util.LinkedList;
import java.util.List;

public class LevelSetObject {

    public int id;
    public int[] objectColor;
    public ImageSelection objectSelection;
    public List<Pixel> lin;
    public List<Pixel> lout;
    public int [][] phiMatrix;

    public LevelSetObject(Image image){
        this.phiMatrix = new int[image.getWidth()][image.getHeight()];
    }

    public void calculateObjectColor(Image image){

        int red = 0;
        int green = 0;
        int blue = 0;
        int[] rgb = new int[3];
        int p;
        int counter = 0;

        for (int i = this.objectSelection.getxOrigin(); i <= objectSelection.getxFinal(); i++) {
            for (int j = objectSelection.getyOrigin(); j <= objectSelection.getyFinal(); j++) {
                p = image.getBufferedImage().getRGB(i,j);
                red += ColorUtilities.getRed(p);
                green += ColorUtilities.getGreen(p);
                blue += ColorUtilities.getBlue(p);
                counter++;
            }
        }

        rgb[0] = red /counter;
        rgb[1] = green /counter;
        rgb[2] = blue /counter;


        this.objectColor =  rgb;
    }

    public void generateLinAndLoutBasedOnObjectSelection(Image image){
            this.lin = new LinkedList<>();
            this.lout = new LinkedList<>();
            int[][] imageDataMatrix = image.getGreyDataMatrix();

            //Genero Lin
            for (int i = this.objectSelection.getxOrigin(); i <= this.objectSelection.getxFinal(); i++) {
                for (int j = this.objectSelection.getyOrigin(); j <= this.objectSelection.getyFinal(); j++) {

                    if (j == this.objectSelection.getyOrigin() || j == this.objectSelection.getyFinal()) {
                        Pixel p = new Pixel(i, j, imageDataMatrix[i][j]);
                        this.lin.add(p);
                    } else {
                        if (i == this.objectSelection.getxOrigin() || i == this.objectSelection.getxFinal()) {
                            Pixel p = new Pixel(i, j, imageDataMatrix[i][j]);
                            this.lin.add(p);
                        }
                    }


                }
            }

            //Genero Lout
            int loutXOrigin = this.objectSelection.getxOrigin() - 1;
            int loutXFinal = this.objectSelection.getxFinal() + 1;
            int loutYOrigin = this.objectSelection.getyOrigin() - 1;
            int loutYFinal = this.objectSelection.getyFinal() + 1;

            for (int i = loutXOrigin; i <= loutXFinal; i++) {
                for (int j = loutYOrigin; j <= loutYFinal; j++) {
                    if (j == loutYOrigin || j == loutYFinal) {
                        Pixel p = new Pixel(i, j, imageDataMatrix[i][j]);
                        this.lout.add(p);
                    } else {
                        if (i == loutXOrigin || i == loutXFinal) {
                            Pixel p = new Pixel(i, j, imageDataMatrix[i][j]);
                            this.lout.add(p);
                        }
                    }
                }
            }
    }

    public void fillInitialPhiMatrix() {
        //NOTA: la funcion phi inicial define un cuadradito con todos valores -3, el borde del cuadrado es -1, el borde exterior es 1 y todo el resto es 3
        //Lleno la matriz de 3
        for (int i = 0; i < this.phiMatrix.length; i++) {
            for (int j = 0; j < this.phiMatrix[0].length; j++) {
                this.phiMatrix[i][j] = 3;
            }
        }
        //Recorro lout y relleno
        for (Pixel p: lout) {
            this.phiMatrix[p.getX()][p.getY()] = 1;
        }
        //Recorro lin y relleno
        for (Pixel p: lin) {
            this.phiMatrix[p.getX()][p.getY()] = -1;
        }

        //Caluclo maximos y minimos del borde interion
        int xMin = this.getMinBorderXPosition();
        int xMax = this.getMaxBorderXPosition();
        int yMin = this.getMinBorderYPosition();
        int yMax = this.getMaxBorderYPosition();

        //Lleno el interior del objeto
        for (int i = xMin + 1; i < xMax; i++) {
            for (int j = yMin + 1; j < yMax; j++) {
                this.phiMatrix[i][j] = -3;
            }
        }
    }

    private int getMaxBorderXPosition(){
        int max = 0;
        for (Pixel p: lin
        ) {
            if(p.getX() > max){
                max = p.getX();
            }
        }
        return max;
    }

    private int getMinBorderXPosition(){
        int min = phiMatrix.length;
        for (Pixel p: lin
        ) {
            if(p.getX() < min){
                min = p.getX();
            }
        }
        return min;
    }

    private int getMaxBorderYPosition(){
        int max = 0;
        for (Pixel p: lin
        ) {
            if(p.getY() > max){
                max = p.getY();
            }
        }
        return max;
    }

    private int getMinBorderYPosition(){
        int min = phiMatrix[0].length;
        for (Pixel p: lin
        ) {
            if(p.getY() < min){
                min = p.getY();
            }
        }
        return min;
    }

}
