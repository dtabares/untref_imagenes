package sample.tpFinal;

import sample.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class LevelSet {

    ImageUtilities imageUtilities;
    BufferedImage bimg;
    List<LevelSetObject> objectList;
    public int [][] psiMatrix; // No es la matriz Phi, sino la matriz Psi
    public int [][] phiMatrix;

    public LevelSet(Image image, List<LevelSetObject> objectList){
        imageUtilities = new ImageUtilities();
        this.bimg = image.getBufferedImage();
        this.objectList = objectList;
        this.psiMatrix = new int[bimg.getWidth()][bimg.getHeight()];
        this.phiMatrix = new int[bimg.getWidth()][bimg.getHeight()];
        this.initializePhiAndPsiMatrix();
        //getPsiNonZeroCount();
    }

    public void updateImage(Image image){
        this.bimg = image.getBufferedImage();
    }

    public void apply(boolean runCycleTwo, int error) {
        // Va a recorrer la lista de objetos y aplicar el metodo a cada uno de ellos
        for (LevelSetObject o : objectList) {
            int counter = 0; //contador de control para no loopear de forma infinita
            boolean finished = false; //condicion de finalizacion
            //Loop que recorre hasta un tope de iteraciones o hasta terminar el algoritmo
            while (counter < 15 && finished == false) {

                //region Switch_in
                // 1. Para cada x en Lout si Fd > 0 lo sacamos de Lout y lo ponemos en Lin
                for (int i = 0; i < o.lout.size(); i++) {
                    Pixel p = o.lout.get(i);
                    if (this.getFd(p,error,o) > 0 && this.getTr(p,o) == 1) {
                        o.lin.add(p);
                        int x = p.getX();
                        int y = p.getY();
                        this.phiMatrix[x][y] = -1;
                        this.psiMatrix[x][y] = o.id;
                        //Reviso los vecinos, si pertenece al fondo (phi == 3) lo agregamos a Lout y cambiamos phi = 1 (Ojo aca con los bordes no estamos validando y nos podemos ir a out of bounds)

                        //Me fijo que no se vaya de rango
                        if (x-1>0){
                            //Reviso a izquierda
                            if (this.phiMatrix[x - 1][y] == 3) {
                                o.lout.add(new Pixel(x - 1, y, bimg.getRGB(x - 1, y)));
                                this.phiMatrix[x - 1][y] = 1;
                            }
                        }
                        if (x+1<this.phiMatrix.length) {
                            //Reviso a derecha
                            if (this.phiMatrix[x + 1][y] == 3) {
                                o.lout.add(new Pixel(x + 1, y, bimg.getRGB(x + 1, y)));
                                this.phiMatrix[x + 1][y] = 1;
                            }
                        }

                        if (y-1>0) {
                            //Reviso a arriba
                            if (this.phiMatrix[x][y - 1] == 3) {
                                o.lout.add(new Pixel(x, y - 1, bimg.getRGB(x, y - 1)));
                                this.phiMatrix[x][y - 1] = 1;
                            }
                        }

                        if (y+1<this.phiMatrix[0].length){
                            //Reviso a abajo
                            if (this.phiMatrix[x][y + 1] == 3) {
                                //lout.add(new Pixel(x,y+1,bimg.getRGB(x,y+1)));
                                o.lout.add(new Pixel(x, y + 1, bimg.getRGB(x, y + 1)));
                                this.phiMatrix[x][y + 1] = 1;
                            }
                        }
                        o.lout.remove(i);

                    }
                }
                //endregion Switch_in

                //2. Algunos pixels de Lin ahora pueden ser interiores, entonces hay que recorrer cada p de Lin, y buscar quien NO tiene un vecino Lout (phi == 1). El que no tenga, lo sacamos de Lin y seteamos phi(p) = -3.
                for (int i = 0; i < o.lin.size(); i++) {
                    Pixel p = o.lin.get(i);
                    int x = p.getX();
                    int y = p.getY();
                    //Los inicializo en negativo, para que si alguna posicion no existe (estoy en un borde)
                    //La condicion que pregunta por los valores de Phi pase
                    int leftPhi=-1,rightPhi=-1,upperPhi=-1,lowerPhi=-1;
                    if (x - 1 > 0) {
                        leftPhi = this.phiMatrix[x - 1][y];
                    }
                    if (x + 1 < this.phiMatrix.length) {
                        rightPhi = this.phiMatrix[x + 1][y];
                    }
                    if (y - 1 > 0) {
                        lowerPhi = this.phiMatrix[x][y - 1];
                    }
                    if (y + 1 < this.phiMatrix[0].length) {
                        upperPhi = this.phiMatrix[x][y + 1];
                    }
                    if (leftPhi < 0 && rightPhi < 0 && upperPhi < 0 && lowerPhi < 0) {
                        o.lin.remove(p);
                        this.phiMatrix[x][y] = -3;
                    }
                }

                //region Switch_out
                //3.Luego vuelvo a recorrer los Lin y les calculo Fd. Si Fd < 0, hay que borrarlo de Lin y agregarlo a Lout. De ese pixel, reviso los 4 vecinos(pv), y los que sean phi(pv) == -3 los agrego a lin y cambio phi(pv) == -1
                for (int i = 0; i < o.lin.size(); i++) {
                    Pixel p = o.lin.get(i);
                    if (this.getFd(p,error,o) < 0) {
                        o.lout.add(p);
                        int x = p.getX();
                        int y = p.getY();
                        this.phiMatrix[x][y] = 1;
                        this.psiMatrix[x][y] = 0;
                        if (x-1>0){
                            //Reviso a izquierda
                            if (this.phiMatrix[x - 1][y] == -3) {
                                o.lin.add(new Pixel(x - 1, y, bimg.getRGB(x - 1, y)));
                                this.phiMatrix[x - 1][y] = -1;
                            }
                        }
                        if(x+1<this.phiMatrix.length){
                            //Reviso a derecha
                            if (this.phiMatrix[x + 1][y] == -3) {
                                o.lin.add(new Pixel(x + 1, y, bimg.getRGB(x + 1, y)));
                                this.phiMatrix[x + 1][y] = -1;
                            }
                        }
                        if(y-1>0){
                            //Reviso arriba
                            if (this.phiMatrix[x][y - 1] == -3) {
                                o.lin.add(new Pixel(x, y - 1, bimg.getRGB(x, y - 1)));
                                this.phiMatrix[x][y - 1] = -1;
                            }
                        }
                        if(y+1<this.phiMatrix[0].length){
                            //Reviso abajo
                            if (this.phiMatrix[x][y + 1] == -3) {
                                o.lin.add(new Pixel(x, y + 1, bimg.getRGB(x, y + 1)));
                                this.phiMatrix[x][y + 1] = -1;
                            }
                        }
                        o.lin.remove(i);
                    }
                }
                //endregion Switch_out

                //4. Vuelvo a loopear por cada pixel de Lout, ya que algunos pixels pudieron transformarse en exteriores.
                //Si p NO tiene vecino Lin, quiere decir que es un Lout aislado que ahora debe ser parte del fondo, entonces
                //lo eliminamos de Lout y seteamos phi(p) = 3
                for (int i = 0; i < o.lout.size(); i++) {
                    Pixel p = o.lout.get(i);
                    int x = p.getX();
                    int y = p.getY();
                    //Los inicializo en positivo, para que si alguna posicion no existe (estoy en un borde)
                    //La condicion que pregunta por los valores de Phi pase
                    int leftPhi=1,rightPhi=1,upperPhi=1,lowerPhi=1;
                    if (x-1>0){
                        leftPhi = this.phiMatrix[x - 1][y];
                    }
                    if(x+1<this.phiMatrix.length){
                        rightPhi = this.phiMatrix[x + 1][y];
                    }
                    if(y-1>0){
                        upperPhi = this.phiMatrix[x][y - 1];
                    }
                    if(y+1<this.phiMatrix[0].length){
                        lowerPhi = this.phiMatrix[x][y + 1];
                    }
                    if (leftPhi > 0 && rightPhi > 0 && upperPhi > 0 && lowerPhi > 0) {
                        o.lout.remove(i);
                        this.phiMatrix[x][y] = 3;
                    }
                }

                //5. Hago el chequeo para ver si terminamos
                finished = this.finished(error,o);
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
                    for (int i = 0; i < o.lout.size(); i++) {
                        Pixel p = o.lout.get(i);
                        if (getGoPhi(p, gaussMask,o) < 0 && this.getTr(p,o) == 1) {
                            o.lin.add(p);
                            int x = p.getX();
                            int y = p.getY();
                            this.phiMatrix[x][y] = -1;
                            this.psiMatrix[x][y] = o.id;
                            //Reviso a izquierda
                            if (x-1>0 && x+1<this.phiMatrix.length && y-1>0 && y+1<this.phiMatrix[0].length) {
                                if (this.phiMatrix[x - 1][y] == 3) {
                                    o.lout.add(new Pixel(x - 1, y, bimg.getRGB(x - 1, y)));
                                    this.phiMatrix[x - 1][y] = 1;
                                }
                                //Reviso a derecha
                                if (this.phiMatrix[x + 1][y] == 3) {
                                    o.lout.add(new Pixel(x + 1, y, bimg.getRGB(x + 1, y)));
                                    this.phiMatrix[x + 1][y] = 1;
                                }
                                //Reviso a arriba
                                if (this.phiMatrix[x][y - 1] == 3) {
                                    o.lout.add(new Pixel(x, y - 1, bimg.getRGB(x, y - 1)));
                                    this.phiMatrix[x][y - 1] = 1;
                                }
                                //Reviso a abajo
                                if (this.phiMatrix[x][y + 1] == 3) {
                                    //lout.add(new Pixel(x,y+1,bimg.getRGB(x,y+1)));
                                    o.lout.add(new Pixel(x, y + 1, bimg.getRGB(x, y + 1)));
                                    this.phiMatrix[x][y + 1] = 1;
                                }
                                o.lout.remove(i);
                            }
                        }
                    }

                    for (int i = 0; i < o.lin.size(); i++) {
                        Pixel p = o.lin.get(i);
                        int x = p.getX();
                        int y = p.getY();
                        int leftPhi,rightPhi,upperPhi,lowerPhi;
                        if (x-1>0 && x+1<this.phiMatrix.length && y-1>0 && y+1<this.phiMatrix[0].length) {
                            leftPhi = this.phiMatrix[x - 1][y];
                            rightPhi = this.phiMatrix[x + 1][y];
                            upperPhi = this.phiMatrix[x][y - 1];
                            lowerPhi = this.phiMatrix[x][y + 1];

                            if (leftPhi < 0 && rightPhi < 0 && upperPhi < 0 && lowerPhi < 0) {
                                o.lin.remove(p);
                                this.phiMatrix[x][y] = -3;
                            }
                        }
                    }

                    for (int i = 0; i < o.lin.size(); i++) {
                        Pixel p = o.lin.get(i);
                        if (this.getGoPhi(p, gaussMask,o) > 0) {
                            o.lout.add(p);
                            int x = p.getX();
                            int y = p.getY();
                            this.phiMatrix[x][y] = 1;
                            this.psiMatrix[x][y] = 0;
                            //Reviso a izquierda
                            if (x-1>0 && x+1<this.phiMatrix.length && y-1>0 && y+1<this.phiMatrix[0].length) {
                                if (this.phiMatrix[x - 1][y] == -3) {
                                    o.lin.add(new Pixel(x - 1, y, bimg.getRGB(x - 1, y)));
                                    this.phiMatrix[x - 1][y] = -1;
                                }
                                //Reviso a derecha
                                if (this.phiMatrix[x + 1][y] == -3) {
                                    o.lin.add(new Pixel(x + 1, y, bimg.getRGB(x + 1, y)));
                                    this.phiMatrix[x + 1][y] = -1;
                                }
                                //Reviso arriba
                                if (this.phiMatrix[x][y - 1] == -3) {
                                    o.lin.add(new Pixel(x, y - 1, bimg.getRGB(x, y - 1)));
                                    this.phiMatrix[x][y - 1] = -1;
                                }
                                //Reviso abajo
                                if (this.phiMatrix[x][y + 1] == -3) {
                                    o.lin.add(new Pixel(x, y + 1, bimg.getRGB(x, y + 1)));
                                    this.phiMatrix[x][y + 1] = -1;
                                }
                                o.lin.remove(i);
                            }
                        }
                    }

                    for (int i = 0; i < o.lout.size(); i++) {
                        Pixel p = o.lout.get(i);
                        int x = p.getX();
                        int y = p.getY();
                        int leftPhi,rightPhi,upperPhi,lowerPhi;
                        if (x-1>0 && x+1<this.phiMatrix.length && y-1>0 && y+1<this.phiMatrix[0].length) {
                            leftPhi = this.phiMatrix[x - 1][y];
                            rightPhi = this.phiMatrix[x + 1][y];
                            upperPhi = this.phiMatrix[x][y - 1];
                            lowerPhi = this.phiMatrix[x][y + 1];
                            if (leftPhi > 0 && rightPhi > 0 && upperPhi > 0 && lowerPhi > 0) {
                                o.lout.remove(i);
                                this.phiMatrix[x][y] = 3;
                            }
                        }
                    }
                    gaussCounter++;
                }
            }
        }
        //LikeHood Test
        for (LevelSetObject o : objectList) {
            for (int i = 0; i < o.lout.size(); i++) {
                Pixel p = o.lout.get(i);
                Pixel neighborPixel;
                Pixel mostSimilarPixel = null;
                //La maxima diferencia de color puede ser 255, entre blanco y negro, asi que seteandolo así
                //alguno va a entrar seguro.
                double maxLikeHood = 256;
                double temp;
                if(this.getTr(p,o) > 1){
                    int x = p.getX();
                    int y = p.getY();
                    //Reviso a izquierda
                    if (x-1>0){
                        neighborPixel = new Pixel(x - 1, y, bimg.getRGB(x - 1, y ));
                        temp = this.colorSimilarity(p,neighborPixel);
                        if (temp<maxLikeHood){
                            maxLikeHood = temp;
                            mostSimilarPixel = neighborPixel;
                        }
                    }
                    //Reviso a derecha
                    if (x+1>0){
                        neighborPixel = new Pixel(x + 1, y, bimg.getRGB(x + 1, y ));
                        temp = this.colorSimilarity(p,neighborPixel);
                        if (temp<maxLikeHood){
                            maxLikeHood = temp;
                            mostSimilarPixel = neighborPixel;
                        }
                    }
                    //Reviso a arriba
                    if (y-1>0){
                        neighborPixel = new Pixel(x, y - 1, bimg.getRGB(x, y-1 ));
                        temp = this.colorSimilarity(p,neighborPixel);
                        if (temp<maxLikeHood){
                            maxLikeHood = temp;
                            mostSimilarPixel = neighborPixel;
                        }
                    }
                    //Reviso a abajo
                    if (y+1>0){
                        neighborPixel = new Pixel(x, y + 1, bimg.getRGB(x, y+1 ));
                        temp = this.colorSimilarity(p,neighborPixel);
                        if (temp<maxLikeHood){
                            maxLikeHood = temp;
                            mostSimilarPixel = neighborPixel;
                        }
                    }

                    if (mostSimilarPixel != null){
                        this.psiMatrix[x][y] = this.psiMatrix[mostSimilarPixel.getX()][mostSimilarPixel.getY()];
                    }
                    //Si por alguna razon no entramos dentro del if anterior queda igual, como parte del background
                }
            }
        }

    }

    private double getGoPhi(Pixel p, Mask mask, LevelSetObject o){
        double result = 0;
        int x = p.getX();
        int y = p.getY();
        int maskSize = mask.getSize();
        int radius = maskSize/2;
        int widthLimit = this.phiMatrix.length - maskSize;
        int heightLimit = this.phiMatrix[0].length - maskSize;
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius ; j <= y + radius; j++) {
                for (int k = 0; k < mask.getSize(); k++) {
                    for (int l = 0; l < mask.getSize(); l++) {
                        //pongo un limite para que no se vaya de rango
                        if (i > maskSize && i < widthLimit && j > maskSize && j < heightLimit) {
                            result += (double) this.phiMatrix[i][j] * mask.getValue(k, l);
                        }
                    }
                }
            }
        }
        //System.out.println("Gauss value for pixel " + x + " " + y + " " + result );
        return result;
    }

    private int getFd(Pixel p, int error, LevelSetObject object){
        //***** Tomo un X que pertenece a Lout, tita(x) color del pixel y tita1 es el color del objeto, si || theta(x) - theta1 || < 10 entonces Fd = 1

        int rgb = this.bimg.getRGB(p.getX(),p.getY());
        int objectRed = object.objectColor[0];
        int objectGreen = object.objectColor[1];
        int objectBlue = object.objectColor[2];
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

    private double colorSimilarity(Pixel p, Pixel neighbour){
        int pixelRGB = this.bimg.getRGB(p.getX(),p.getY());
        int neighbourRGB = this.bimg.getRGB(neighbour.getX(),neighbour.getY());
        int pixelRed = ColorUtilities.getRed(pixelRGB);
        int pixelGreen = ColorUtilities.getGreen(pixelRGB);
        int pixelBlue =ColorUtilities.getBlue(pixelRGB);
        int neighbourRed = ColorUtilities.getRed(neighbourRGB);
        int neighbourGreen = ColorUtilities.getGreen(neighbourRGB);
        int neighbourBlue = ColorUtilities.getBlue(neighbourRGB);

        int redDifference = pixelRed - neighbourRed;
        int greenDifference = pixelGreen - neighbourGreen;
        int blueDifference = pixelBlue - neighbourBlue;

        double similarity = Math.sqrt(Math.pow(redDifference,2) + Math.pow(greenDifference,2) + Math.pow(blueDifference,2));

        return similarity;
    }


    public int getPsiNonZeroCount(){
        int count=0;
        for (int i = 0; i < psiMatrix.length; i++) {
            for (int j = 0; j < psiMatrix[0].length; j++) {
                if (psiMatrix[i][j]!=0){
                    count++;
                }
            }
        }
        System.out.println("Matriz psi con: " + count + " valores distintos de 0");
        return count;
    }

    public int getTr(Pixel p, LevelSetObject o){
        int alpha = this.getAlpha(p);
        int tObj = this.getTobj(p);
        int tBg = this.getTbg(p,o);
        int result = Math.min(alpha,Math.max(tObj,tBg));
        //System.out.println("alpha: " + alpha +" tObj: " + tObj + " tBg: " + tBg + " result: " + result);
        return result;
    }

    public int getTobj(Pixel p){
        int tObj = 0;
        int x = p.getX();
        int y = p.getY();
        int psiValueForP = psiMatrix[x][y];
        //miro a los 4 vecinos con cuidado de no desbordar
        //Si alguno de los vecinos pertenece tiene el mismo valor que p y es mayor a cero significa que esta conectado con p
        if (psiValueForP > 0) {
            if (x - 1 > 0) {
                if (psiMatrix[x - 1][y] == psiValueForP) {
                    tObj++;
                }
            }
            if (x + 1 < psiMatrix.length) {
                if (psiMatrix[x + 1][y] == psiValueForP) {
                    tObj++;
                }
            }
            if (y - 1 > 0) {
                if (psiMatrix[x][y - 1] == psiValueForP) {
                    tObj++;
                }
            }
            if (y + 1 < psiMatrix[0].length) {
                if (psiMatrix[x][y + 1] == psiValueForP) {
                    tObj++;
                }
            }
        }
        return tObj;
    }

    public int getTbg(Pixel p, LevelSetObject o){
        int tBg = 0;
        int x = p.getX();
        int y = p.getY();
        int psiValueForP = psiMatrix[x][y];
        //miro a los 8 vecinos
        if (psiValueForP == 0) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    //Me fijo que no desborde la mascara
                    if (x + i > 0 && x + i < this.phiMatrix.length && y + j > 0 && y + j < this.phiMatrix[0].length) {
                        //Me fijo en la matriz Phi del objeto, si es negativo es que esta dentro del objeto o en su linea interior
                        if (this.phiMatrix[x + i][y + j] < 0) {
                            tBg++;
                        }
                    }
                }
            }
        }
        return tBg;
    }

    public int getAlpha(Pixel p){
        List<Integer> regions = new LinkedList<>();
        int x = p.getX();
        int y = p.getY();
        //miro a los vecinos y determino si alguno se intersecta con algun objeto
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                //Me fijo que no desborde la mascara
                if(x+i > 0 && x+i < psiMatrix.length && y+j > 0 && y+j < psiMatrix[0].length) {
                    int psiMatrixValue = psiMatrix[x + i][y + j];
                    //Buscamos que no sea el fondo, y para no contar dos veces el mismo objeto nos fijamos que ya no lo tengamos
                    if (psiMatrixValue != 0 && !regions.contains(psiMatrixValue)) {
                        regions.add(psiMatrixValue);
                    }
                }
            }
        }
        return regions.size();
    }

    private boolean finished(int error, LevelSetObject o){

        for (Pixel p: o.lin) {
            if (this.getFd(p,error,o)<0){
                return false;
            }
        }

        for (Pixel p: o.lout) {
            if (this.getFd(p,error,o)>0){
                return false;
            }
        }

        return true;

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

    private boolean belongsToAnotherObjectLine(Pixel pixel, LevelSetObject object){
        for (LevelSetObject o : objectList) {
            if (object != o){
                for (Pixel p : o.lout) {
                    return (pixel.getX() == p.getX() || pixel.getY() == p.getY());
                }
            }
        }
        return false;
    }

    private void initializePhiAndPsiMatrix() {
        //NOTA: la Matriz Phi se definede la siguiente manera:
        // 3 para pixels que pertenencen al fondo
        // 1 para pixels que pertenecen a Lout de algún objeto
        // -1 para pixels que pertenencen a Lin de algún objeto
        // -3 para pixels que pertenecen al interior de un objeto

        //La Matriz Psi se definede la siguiente manera:
        // 0 si el pixel pertenece al fondo
        // N si el pixel pertenece al objeto N

        //Ambas matrices tienen las mismas dimensiones

        //Lleno la matriz Phi de 3 y Psi de 0
        for (int i = 0; i < this.phiMatrix.length; i++) {
            for (int j = 0; j < this.phiMatrix[0].length; j++) {
                this.phiMatrix[i][j] = 3;
                this.psiMatrix[i][j] = 0;
            }
        }

        for (LevelSetObject o : objectList) {
            //Recorro lout y relleno
            for (Pixel p: o.lout) {
                this.phiMatrix[p.getX()][p.getY()] = 1;
                //this.psiMatrix[p.getX()][p.getY()] = o.id;
            }
            //Recorro lin y relleno
            for (Pixel p: o.lin) {
                this.phiMatrix[p.getX()][p.getY()] = -1;
                this.psiMatrix[p.getX()][p.getY()] = o.id;
            }

            //Caluclo maximos y minimos del borde interion
            int xMin = o.getMinBorderXPosition(phiMatrix.length);
            int xMax = o.getMaxBorderXPosition();
            int yMin = o.getMinBorderYPosition(phiMatrix[0].length);
            int yMax = o.getMaxBorderYPosition();

            //Lleno el interior del objeto
            for (int i = xMin + 1; i < xMax; i++) {
                for (int j = yMin + 1; j < yMax; j++) {
                    this.phiMatrix[i][j] = -3;
                    this.psiMatrix[i][j] = o.id;
                }
            }

        }
    }
}