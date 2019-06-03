package sample;

import java.awt.image.BufferedImage;
import java.util.*;

public class ActiveContours {

    ImageUtilities imageUtilities;
    int [][] phiMatrix;
    BufferedImage bimg; //
    ArrayList<Pixel> lin;
    ArrayList<Pixel> lout;
    int objectColor[];


    public ActiveContours(Image image, List<Pixel> lin, List<Pixel> lout,int[]  objectColor){
        imageUtilities = new ImageUtilities();
        this.lin = this.cloneList(lin);
        this.lout = this.cloneList(lout);
        this.bimg = image.getBufferedImage();
        this.objectColor = objectColor;
        this.phiMatrix = new int[image.getWidth()][image.getHeight()];
        this.fillInitialPhiMatrix();
    }

    public ActiveContours(Image image, List<Pixel> lin, List<Pixel> lout,int[] objectColor, int[][] phiMatrix){
        imageUtilities = new ImageUtilities();
        this.lin = this.cloneList(lin);
        this.lout = this.cloneList(lout);
        this.bimg = image.getBufferedImage();
        this.objectColor = objectColor;
        this.phiMatrix = phiMatrix;
    }

    public void fillInitialPhiMatrix() {
        //NOTA: la funcion phi inicial define un cuadradito con todos valores -3, el borde del cuadrado es -1, el borde exterior es 1 y todo el resto es 3
        //Lleno la matriz de 3
        for (int i = 0; i < phiMatrix.length; i++) {
            for (int j = 0; j < phiMatrix[0].length; j++) {
                phiMatrix[i][j] = 3;
            }
        }
        //Recorro lout y relleno
        for (Pixel p: lout) {
            phiMatrix[p.getX()][p.getY()] = 1;
        }
        //Recorro lin y relleno
        for (Pixel p: lin) {
            phiMatrix[p.getX()][p.getY()] = -1;
        }

        //Caluclo maximos y minimos del borde interion
        int xMin = this.getMinBorderXPosition();
        int xMax = this.getMaxBorderXPosition();
        int yMin = this.getMinBorderYPosition();
        int yMax = this.getMaxBorderYPosition();

        //Lleno el interior del objeto
        for (int i = xMin + 1; i < xMax; i++) {
            for (int j = yMin + 1; j < yMax; j++) {
                phiMatrix[i][j] = -3;
            }
        }
    }

    public void applyReloaded(boolean runCycleTwo, int error) {
        int counter = 0; //contador de control para no loopear de forma infinita
        boolean finished = false; //condicion de finalizacion
        //Loop que recorre hasta un tope de iteraciones o hasta terminar el algoritmo
        while (counter < 200000 && finished == false) {
            // 1. Para cada x en Lout si Fd > 0 lo sacamos de Lout y lo ponemos en Lin
            for (int i = 0; i < lout.size(); i++) {
                Pixel p = lout.get(i);
                if (this.calculateFd(p,error) > 0) {
                    lin.add(p);
                    int x = p.getX();
                    int y = p.getY();
                    phiMatrix[x][y] = -1;

                    //Reviso los vecinos, si pertenece al fondo (phi == 3) lo agregamos a Lout y cambiamos phi = 1 (Ojo aca con los bordes no estamos validando y nos podemos ir a out of bounds)

                    //Reviso a izquierda
                    //if (x-1>0 && x+1<phiMatrix.length && y-1>0 && y+1<phiMatrix[0].length){
                        if (phiMatrix[x - 1][y] == 3) {
                            lout.add(new Pixel(x - 1, y, bimg.getRGB(x - 1, y)));
                            phiMatrix[x - 1][y] = 1;
                        }
                        //Reviso a derecha
                        if (phiMatrix[x + 1][y] == 3) {
                            lout.add(new Pixel(x + 1, y, bimg.getRGB(x + 1, y)));
                            phiMatrix[x + 1][y] = 1;
                        }
                        //Reviso a arriba
                        if (phiMatrix[x][y - 1] == 3) {
                            lout.add(new Pixel(x, y - 1, bimg.getRGB(x, y - 1)));
                            phiMatrix[x][y - 1] = 1;
                        }
                        //Reviso a abajo
                        if (phiMatrix[x][y + 1] == 3) {
                            //lout.add(new Pixel(x,y+1,bimg.getRGB(x,y+1)));
                            lout.add(new Pixel(x, y + 1, bimg.getRGB(x, y + 1)));
                            phiMatrix[x][y + 1] = 1;
                        }
                        lout.remove(i);
                    //}
                }
            }

            //2. Algunos pixels de Lin ahora pueden ser interiores, entonces hay que recorrer cada p de Lin, y buscar quien NO tiene un vecino Lout (phi == 1). El que no tenga, lo sacamos de Lin y seteamos phi(p) = -3.
            for (int i = 0; i < lin.size(); i++) {
                Pixel p = lin.get(i);
                int x = p.getX();
                int y = p.getY();
                //if (x-1>0 && x+1<phiMatrix.length && y-1>0 && y+1<phiMatrix[0].length) {
                    int leftPhi = phiMatrix[x - 1][y];
                    int rightPhi = phiMatrix[x + 1][y];
                    int upperPhi = phiMatrix[x][y - 1];
                    int lowerPhi = phiMatrix[x][y + 1];

                    if (leftPhi < 0 && rightPhi < 0 && upperPhi < 0 && lowerPhi < 0) {
                        lin.remove(p);
                        phiMatrix[x][y] = -3;
                    }
                //}
            }

            //3.Luego vuelvo a recorrer los Lin y les calculo Fd. Si Fd < 0, hay que borrarlo de Lin y agregarlo a Lout. De ese pixel, reviso los 4 vecinos(pv), y los que sean phi(pv) == -3 los agrego a lin y cambio phi(pv) == -1
            for (int i = 0; i < lin.size(); i++) {
                Pixel p = lin.get(i);
                if (this.calculateFd(p,error) < 0) {
                    lout.add(p);
                    int x = p.getX();
                    int y = p.getY();
                    phiMatrix[x][y] = 1;

                    //if (x-1>0 && x+1<phiMatrix.length && y-1>0 && y+1<phiMatrix[0].length) {
                        //Reviso a izquierda
                        if (phiMatrix[x - 1][y] == -3) {
                            lin.add(new Pixel(x - 1, y, bimg.getRGB(x - 1, y)));
                            phiMatrix[x - 1][y] = -1;
                        }
                        //Reviso a derecha
                        if (phiMatrix[x + 1][y] == -3) {
                            lin.add(new Pixel(x + 1, y, bimg.getRGB(x + 1, y)));
                            phiMatrix[x + 1][y] = -1;
                        }
                        //Reviso arriba
                        if (phiMatrix[x][y - 1] == -3) {
                            lin.add(new Pixel(x, y - 1, bimg.getRGB(x, y - 1)));
                            phiMatrix[x][y - 1] = -1;
                        }
                        //Reviso abajo
                        if (phiMatrix[x][y + 1] == -3) {
                            lin.add(new Pixel(x, y + 1, bimg.getRGB(x, y + 1)));
                            phiMatrix[x][y + 1] = -1;
                        }
                        lin.remove(i);
                    //}
                }
            }

            //4. Vuelvo a loopear por cada pixel de Lout, ya que algunos pixels pudieron transformarse en exteriores.
            //Si p NO tiene vecino Lin, quiere decir que es un Lout aislado que ahora debe ser parte del fondo, entonces
            //lo eliminamos de Lout y seteamos phi(p) = 3
            for (int i = 0; i < lout.size(); i++) {
                Pixel p = lout.get(i);
                int x = p.getX();
                int y = p.getY();
                //if (x-1>0 && x+1<phiMatrix.length && y-1>0 && y+1<phiMatrix[0].length) {
                    int leftPhi = phiMatrix[x - 1][y];
                    int rightPhi = phiMatrix[x + 1][y];
                    int upperPhi = phiMatrix[x][y - 1];
                    int lowerPhi = phiMatrix[x][y + 1];

                    if (leftPhi > 0 && rightPhi > 0 && upperPhi > 0 && lowerPhi > 0) {
                        lout.remove(i);
                        phiMatrix[x][y] = 3;
                    }
                //}
            }

            //5. Hago el chequeo para ver si terminamos
            finished = this.finished(error);
            counter++;
        }
        System.out.println("Finished with: " + counter + " iterations");

        if (runCycleTwo){
        System.out.println("Starting Cycle Two");

        /*
        - Ciclo 2
        1. Para cada pixel en Lout computar G o Phi, si G o Phi < 0 aplicamos switch_in
        2. Para cada pixel en Lin hacemos intercambio de pixels
        3. Para cada pixel en Lin computar G o Phi, si G o Phi > 0 aplicamos switch_out
        4. Para cada pixel en Lout hacemos intercambio de pixels
         */

        //Defino un sigma
        int sigma = 3;
        int gaussCounter = 0;
        int maskSize = (int) Math.round(2 * sigma + 1);
        Mask gaussMask = new Mask(maskSize);
        gaussMask.setGaussMaskRevised(sigma);
        int Ng = maskSize;
        while (gaussCounter < Ng) {
            for (int i = 0; i < lout.size(); i++) {
                Pixel p = lout.get(i);
                if (calculateGoPhi(p, gaussMask) < 0) {
                    lin.add(p);
                    int x = p.getX();
                    int y = p.getY();
                    phiMatrix[x][y] = -1;

                    //Reviso a izquierda
                    //if (x-1>0 && x+1<phiMatrix.length && y-1>0 && y+1<phiMatrix[0].length) {
                        if (phiMatrix[x - 1][y] == 3) {
                            lout.add(new Pixel(x - 1, y, bimg.getRGB(x - 1, y)));
                            phiMatrix[x - 1][y] = 1;
                        }
                        //Reviso a derecha
                        if (phiMatrix[x + 1][y] == 3) {
                            lout.add(new Pixel(x + 1, y, bimg.getRGB(x + 1, y)));
                            phiMatrix[x + 1][y] = 1;
                        }
                        //Reviso a arriba
                        if (phiMatrix[x][y - 1] == 3) {
                            lout.add(new Pixel(x, y - 1, bimg.getRGB(x, y - 1)));
                            phiMatrix[x][y - 1] = 1;
                        }
                        //Reviso a abajo
                        if (phiMatrix[x][y + 1] == 3) {
                            //lout.add(new Pixel(x,y+1,bimg.getRGB(x,y+1)));
                            lout.add(new Pixel(x, y + 1, bimg.getRGB(x, y + 1)));
                            phiMatrix[x][y + 1] = 1;
                        }
                        lout.remove(i);
                    //}
                }
            }

            for (int i = 0; i < lin.size(); i++) {
                Pixel p = lin.get(i);
                int x = p.getX();
                int y = p.getY();

                //if (x-1>0 && x+1<phiMatrix.length && y-1>0 && y+1<phiMatrix[0].length) {
                    int leftPhi = phiMatrix[x - 1][y];
                    int rightPhi = phiMatrix[x + 1][y];
                    int upperPhi = phiMatrix[x][y - 1];
                    int lowerPhi = phiMatrix[x][y + 1];

                    if (leftPhi < 0 && rightPhi < 0 && upperPhi < 0 && lowerPhi < 0) {
                        lin.remove(p);
                        phiMatrix[x][y] = -3;
                    }
                //}
            }

            for (int i = 0; i < lin.size(); i++) {
                Pixel p = lin.get(i);
                if (this.calculateGoPhi(p, gaussMask) > 0) {
                    lout.add(p);
                    int x = p.getX();
                    int y = p.getY();
                    phiMatrix[x][y] = 1;

                    //Reviso a izquierda
                    //if (x-1>0 && x+1<phiMatrix.length && y-1>0 && y+1<phiMatrix[0].length) {
                        if (phiMatrix[x - 1][y] == -3) {
                            lin.add(new Pixel(x - 1, y, bimg.getRGB(x - 1, y)));
                            phiMatrix[x - 1][y] = -1;
                        }
                        //Reviso a derecha
                        if (phiMatrix[x + 1][y] == -3) {
                            lin.add(new Pixel(x + 1, y, bimg.getRGB(x + 1, y)));
                            phiMatrix[x + 1][y] = -1;
                        }
                        //Reviso arriba
                        if (phiMatrix[x][y - 1] == -3) {
                            lin.add(new Pixel(x, y - 1, bimg.getRGB(x, y - 1)));
                            phiMatrix[x][y - 1] = -1;
                        }
                        //Reviso abajo
                        if (phiMatrix[x][y + 1] == -3) {
                            lin.add(new Pixel(x, y + 1, bimg.getRGB(x, y + 1)));
                            phiMatrix[x][y + 1] = -1;
                        }
                        lin.remove(i);
                    //}
                }
            }

            for (int i = 0; i < lout.size(); i++) {
                Pixel p = lout.get(i);
                int x = p.getX();
                int y = p.getY();

                //if (x-1>0 && x+1<phiMatrix.length && y-1>0 && y+1<phiMatrix[0].length) {
                    int leftPhi = phiMatrix[x - 1][y];
                    int rightPhi = phiMatrix[x + 1][y];
                    int upperPhi = phiMatrix[x][y - 1];
                    int lowerPhi = phiMatrix[x][y + 1];

                    if (leftPhi > 0 && rightPhi > 0 && upperPhi > 0 && lowerPhi > 0) {
                        lout.remove(i);
                        phiMatrix[x][y] = 3;
                    }
                //}
            }
            gaussCounter++;
        }
    }
    }

    private double calculateGoPhi(Pixel p, Mask mask){
        double result = 0;
        int x = p.getX();
        int y = p.getY();
        int maskSize = mask.getSize();
        int radius = maskSize/2;
        int widthLimit = phiMatrix.length - maskSize;
        int heightLimit = phiMatrix[0].length - maskSize;
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius ; j <= y + radius; j++) {
                for (int k = 0; k < mask.getSize(); k++) {
                    for (int l = 0; l < mask.getSize(); l++) {
                        //pongo un limite para que no se vaya de rango
                        if (i > maskSize && i < widthLimit && j > maskSize && j < heightLimit) {
                            result += (double) phiMatrix[i][j] * mask.getValue(k, l);
                        }
                    }
                }
            }
        }
        //System.out.println("Gauss value for pixel " + x + " " + y + " " + result );
        return result;
    }

    private int calculateFd(Pixel p, int error){
        //***** Tomo un X que pertenece a Lout, tita(x) color del pixel y tita1 es el color del objeto, si || theta(x) - theta1 || < 10 entonces Fd = 1

        int rgb = this.bimg.getRGB(p.getX(),p.getY());
        int objectRed = this.objectColor[0];
        int objectGreen = this.objectColor[1];
        int objectBlue = this.objectColor[2];
        int pRed = ColorUtilities.getRed(rgb);
        int pGreen = ColorUtilities.getGreen(rgb);
        int pBlue = ColorUtilities.getBlue(rgb);

        int redDifference = objectRed - pRed;
        int greenDifference = objectGreen - pGreen;
        int blueDifference = objectBlue - pBlue;

        double norm = Math.sqrt(Math.pow(redDifference,2) + Math.pow(greenDifference,2) + Math.pow(blueDifference,2));
        if (norm < error){
            return 1;
        }

        return -1;
    }

    private boolean finished(int error){

        for (Pixel p: lin) {
            if (this.calculateFd(p,error)<0){
                return false;
            }
        }

        for (Pixel p: lout) {
            if (this.calculateFd(p,error)>0){
                return false;
            }
        }

        return true;

    }


    public BufferedImage paintContours(){

        BufferedImage bimg = imageUtilities.copyImageIntoAnother(this.bimg);
        //Pinto lin
        for (Pixel p: lin){
            bimg.setRGB(p.getX(),p.getY(), ColorUtilities.createRGB(255,0,255));
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

    public int[][] getPhiMatrix() {
        return phiMatrix;
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

    private ArrayList<Pixel> cloneList(List<Pixel> original){
        ArrayList<Pixel> clonedList = new ArrayList<>();
        ListIterator<Pixel> iterator = original.listIterator();
        while( iterator.hasNext()) {
            Pixel original_pixel = iterator.next();
            Pixel cloned_pixel = new Pixel(original_pixel.getX(), original_pixel.getY(), original_pixel.getValue());
            clonedList.add(cloned_pixel);
        }

        return clonedList;
    }
}