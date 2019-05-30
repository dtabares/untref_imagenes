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
    double objectTheta;
    double backgroundTheta;


    public ActiveContours(Image image, List<Pixel> lin, List<Pixel> lout,double objectTheta, double backgroundTheta){
        imageUtilities = new ImageUtilities();
        this.lin = lin;
        this.lout = lout;
        this.bimg = image.getBufferedImage();
        this.objectTheta = objectTheta;
        this.backgroundTheta = backgroundTheta;
        phiMatrix = new int[image.getWidth()][image.getHeight()];
        this.fillPhiMatrix();
    }

    private void fillPhiMatrix() {
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
        int yMix = this.getMinBorderYPosition();
        int yMax = this.getMaxBorderYPosition();

        //Lleno el interior del objeto
        for (int i = xMin + 1; i < xMax; i++) {
            for (int j = yMix + 1; j < yMax; j++) {
                phiMatrix[i][j] = -3;
            }
        }
    }

    public void apply(){
        makeMagic();
    }

    public int getFd(Pixel p, double objectTheta){
        //***** Tomo un X que pertenece a Lout, tita(x) color del pixel y tita1 es el color del objeto, si || theta(x) - theta1 || < 10 entonces Fd = 1
        if (Math.sqrt(Math.pow(p.getValue(),2)-Math.pow(objectTheta,2))<10){
            return 1;
        }

        return -1;
    }

    public void makeMagic(){
        int counter = 0; //contador de control para no loopear de forma infinita
        while (counter < 1000){
            //2. Para cada x en Lout si Fd > 0 lo sacamos de Lout y lo ponemos en Lin, luego para cada vecino donde Phi vale 3, hay que agregarlo a lin y actualizarlo en phi como 1
            for (Pixel p: lout) {
                if (this.getFd(p,this.objectTheta)>0){
                    lout.remove(p);
                    lin.add(p);
                    int x = p.getX();
                    int y = p.getY();
                    if(phiMatrix[x-1][y] == 3){
                        lin.add(new Pixel(x-1,y,bimg.getRGB(x-1,y)));
                        phiMatrix[x-1][y]=1;
                    }
                    if(phiMatrix[x+1][y] == 3){
                        lin.add(new Pixel(x+1,y,bimg.getRGB(x+1,y)));
                        phiMatrix[x+1][y]=1;
                    }
                    if(phiMatrix[x][y-1] == 3){
                        lin.add(new Pixel(x,y-1,bimg.getRGB(x,y-1)));
                        phiMatrix[x][y-1]=1;
                    }
                    if(phiMatrix[x][y+1] == 3){
                        lin.add(new Pixel(x,y+1,bimg.getRGB(x,y+1)));
                        phiMatrix[x][y+1]=1;
                    }
                }
            }
            //3. Hecho el paso 2 revisar los pixels de Lin ya que pueden ser ahora puntos interiores al objeto, si es asi se sacan de lin y se maracn en phi como -3
            //4. Para cada pixel de lin si Fd < 0 se borran de lin y se agregan a lout se miran los vecinos y si phi vale -3 entonces se agregan a lin y se marcan en phi como 1
            for (Pixel p: lin) {
                if (this.getFd(p, this.objectTheta)<0){
                    lin.remove(p);
                    lout.add(p);
                    int x = p.getX();
                    int y = p.getY();
                    if(phiMatrix[x-1][y] == -3){
                        lin.add(new Pixel(x-1,y,bimg.getRGB(x-1,y)));
                        phiMatrix[x-1][y]=1;
                    }
                    if(phiMatrix[x+1][y] == -3){
                        lin.add(new Pixel(x+1,y,bimg.getRGB(x+1,y)));
                        phiMatrix[x+1][y]=1;
                    }
                    if(phiMatrix[x][y-1] == -3){
                        lin.add(new Pixel(x,y-1,bimg.getRGB(x,y-1)));
                        phiMatrix[x][y-1]=1;
                    }
                    if(phiMatrix[x][y+1] == -3){
                        lin.add(new Pixel(x,y+1,bimg.getRGB(x,y+1)));
                        phiMatrix[x][y+1]=1;
                    }
                }
            }
            //5. Se hace el paso 3 pero al reves
        }
    }

    public void expand(){

    }

    public void contract(){

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

    public int getMaxBorderXPosition(){
        int max = 0;
        for (Pixel p: lin
             ) {
            if(p.getX() > max){
                max = p.getX();
            }
        }
        return max;
    }

    public int getMinBorderXPosition(){
        int min = phiMatrix.length;
        for (Pixel p: lin
        ) {
            if(p.getX() < min){
                min = p.getX();
            }
        }
        return min;
    }

    public int getMaxBorderYPosition(){
        int max = 0;
        for (Pixel p: lin
        ) {
            if(p.getY() > max){
                max = p.getY();
            }
        }
        return max;
    }

    public int getMinBorderYPosition(){
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