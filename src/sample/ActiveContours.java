package sample;

import java.awt.image.BufferedImage;
import java.util.*;

public class ActiveContours {

    ImageUtilities imageUtilities;
    int [][] phiMatrix;
    BufferedImage bimg; //
    ArrayList<Pixel> lin;
    ArrayList<Pixel> lout;
    double objectTheta;
    double backgroundTheta;


    public ActiveContours(Image image, List<Pixel> lin, List<Pixel> lout,double objectTheta){
        imageUtilities = new ImageUtilities();
        this.lin = this.cloneList(lin);
        this.lout = this.cloneList(lout);
        this.bimg = image.getBufferedImage();
        this.objectTheta = objectTheta;
        this.phiMatrix = new int[image.getWidth()][image.getHeight()];
        this.fillInitialPhiMatrix();
    }

    public ActiveContours(Image image, List<Pixel> lin, List<Pixel> lout,double objectTheta, int[][] phiMatrix){
        imageUtilities = new ImageUtilities();
        this.lin = this.cloneList(lin);
        this.lout = this.cloneList(lout);
        this.bimg = image.getBufferedImage();
        this.objectTheta = objectTheta;
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

    public void apply(){
        ArrayList<Pixel> toBeRemoved = new ArrayList<>();
        ArrayList<Pixel> toBeAdded = new ArrayList<>();
        int counter = 0; //contador de control para no loopear de forma infinita
        boolean finished = false;
        while (counter < 1000000 && finished == false){
            //1. Para cada x en Lout si Fd > 0 lo sacamos de Lout y lo ponemos en Lin,
            // luego para cada vecino donde Phi vale 3, hay que agregarlo a Lout y actualizarlo en phi como 1
            ListIterator<Pixel> iterator = this.lout.listIterator();
            while( iterator.hasNext()){
                Pixel p = iterator.next();
                if (this.calculateFd(p) > 0){
                    //lout.remove(p);
                    toBeRemoved.add(p);
                    lin.add(p);
                    int x = p.getX();
                    int y = p.getY();
                    phiMatrix[x][y]=-1;
                    //Reviso los vecinos,  si pertenece al fondo (phi == 3) lo agregamos a Lout y cambiamos phi = 1
                    // Ojo aca con los bordes no estamos validando y nos podemos ir a out of bounds

                    //Reviso a izquierda
                    if(phiMatrix[x-1][y] == 3){
                        //lout.add(new Pixel(x-1,y,bimg.getRGB(x-1,y)));
                        toBeAdded.add(new Pixel(x-1,y,bimg.getRGB(x-1,y)));
                        phiMatrix[x-1][y]=1;
                    }
                    //Reviso a derecha
                    if(phiMatrix[x+1][y] == 3){
                        //lout.add(new Pixel(x+1,y,bimg.getRGB(x+1,y)));
                        toBeAdded.add(new Pixel(x+1,y,bimg.getRGB(x+1,y)));
                        phiMatrix[x+1][y]=1;
                    }

                    //Reviso a arriba
                    if(phiMatrix[x][y-1] == 3){
                        //lout.add(new Pixel(x,y-1,bimg.getRGB(x,y-1)));
                        toBeAdded.add(new Pixel(x,y-1,bimg.getRGB(x,y-1)));
                        phiMatrix[x][y-1]=1;
                    }
                    //Reviso a abajo
                    if(phiMatrix[x][y+1] == 3){
                        //lout.add(new Pixel(x,y+1,bimg.getRGB(x,y+1)));
                        toBeAdded.add(new Pixel(x,y+1,bimg.getRGB(x,y+1)));
                        phiMatrix[x][y+1]=1;
                    }
                }
            }
            this.lout.removeAll(toBeRemoved);
            this.lout.addAll(toBeAdded);
            toBeRemoved.clear();
            toBeAdded.clear();
            //2. Luego del paso anterior, algunos pixels de Lin ahora pueden ser interiores, entonces hay que recorrer
            //cada p de Lin, y buscar quien NO tiene un vecino Lout (phi == 1). El que no tenga, lo sacamos de Lin y seteamos
            // phi(p) = -3.

            iterator = this.lin.listIterator();
            while(iterator.hasNext()){
                Pixel p = iterator.next();
                // Ojo aca con los bordes no estamos validando y nos podemos ir a out of bounds
                int x = p.getX();
                int y = p.getY();

                int leftPhi = phiMatrix[x-1][y];
                int rightPhi = phiMatrix[x+1][y];
                int upperPhi = phiMatrix[x][y-1];
                int lowerPhi = phiMatrix[x][y+1];

                if (leftPhi < 0 && rightPhi < 0  && upperPhi < 0 && lowerPhi < 0 ){
                    //iterator.remove();
                    toBeRemoved.add(p);
                    phiMatrix[x][y] = -3;
                }
            }

            this.lin.removeAll(toBeRemoved);
            toBeRemoved.clear();


            //3.Luego vuelvo a recorrer los Lin y les calculo Fd. Si Fd < 0, hay que borrarlo de Lin y agregarlo a Lout.
            //De ese pixel, reviso los 4 vecinos(pv), y los que sean phi(pv) == -3 los agrego a lin y cambio phi(pv) == -1

            // Ojo aca con los bordes no estamos validando y nos podemos ir a out of bounds
            iterator = this.lin.listIterator();
            while(iterator.hasNext()){
                Pixel p = iterator.next();
                if (this.calculateFd(p)<0){

                    //iterator.remove();
                    toBeRemoved.add(p);
                    lout.add(p);
                    int x = p.getX();
                    int y = p.getY();
                    phiMatrix[x][y] = 1;
                    if(phiMatrix[x-1][y] == -3){
                        //lin.add(new Pixel(x-1,y,bimg.getRGB(x-1,y)));
                        toBeAdded.add(new Pixel(x-1,y,bimg.getRGB(x-1,y)));
                        phiMatrix[x-1][y]=-1;
                    }
                    if(phiMatrix[x+1][y] == -3){
                        //lin.add(new Pixel(x+1,y,bimg.getRGB(x+1,y)));
                        toBeAdded.add(new Pixel(x+1,y,bimg.getRGB(x+1,y)));
                        phiMatrix[x+1][y]=-1;
                    }
                    if(phiMatrix[x][y-1] == -3){
                        //lin.add(new Pixel(x,y-1,bimg.getRGB(x,y-1)));
                        toBeAdded.add(new Pixel(x,y-1,bimg.getRGB(x,y-1)));
                        phiMatrix[x][y-1]=-1;
                    }
                    if(phiMatrix[x][y+1] == -3){
                        //lin.add(new Pixel(x,y+1,bimg.getRGB(x,y+1)));
                        toBeAdded.add(new Pixel(x,y+1,bimg.getRGB(x,y+1)));
                        phiMatrix[x][y+1]=-1;
                    }
                }
            }
            this.lin.removeAll(toBeRemoved);
            this.lout.addAll(toBeAdded);
            toBeRemoved.clear();
            toBeAdded.clear();

            //4. Vuelvo a loopear por cada pixel de Lout, ya que algunos pixels pudieron transformarse en exteriores.
            //Si p NO tiene vecino Lin, quiere decir que es un Lout aislado que ahora debe ser parte del fondo, entonces
            //lo eliminamos de Lout y seteamos phi(p) = 3
            iterator = this.lout.listIterator();
            while(iterator.hasNext()){
                Pixel p = iterator.next();
                int x = p.getX();
                int y = p.getY();

                int leftPhi = phiMatrix[x-1][y];
                int rightPhi = phiMatrix[x+1][y];
                int upperPhi = phiMatrix[x][y-1];
                int lowerPhi = phiMatrix[x][y+1];

                if (leftPhi > 0 && rightPhi > 0  && upperPhi > 0  && lowerPhi > 0 ){
                    //iterator.remove();
                    toBeRemoved.add(p);
                    phiMatrix[x][y] = 3;
                }
            }
            this.lout.removeAll(toBeRemoved);
            toBeRemoved.clear();

            //5. Hago el chequeo para ver si terminamos
            finished = this.finished();


            counter++;
        }
        System.out.println(counter);
    }

    private int calculateFd(Pixel p){
        //***** Tomo un X que pertenece a Lout, tita(x) color del pixel y tita1 es el color del objeto, si || theta(x) - theta1 || < 10 entonces Fd = 1
        if (Math.sqrt(Math.pow(p.getValue(),2)-Math.pow(this.objectTheta,2)) < 10){
            return 1;
        }

        return -1;
    }

    private boolean finished(){

        for (Pixel p: lin) {
            if (this.calculateFd(p)<0){
                return false;
            }
        }

        for (Pixel p: lout) {
            if (this.calculateFd(p)>0){
                return false;
            }
        }

        return true;

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