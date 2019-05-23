package sample;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Hough {

    private int [][] cumulativeMatrix;
    private double [] angleArray;
    private double [] radiusArray;
    private ImageUtilities imageUtilities;

    //Para crear el objeto debo indicar las dimensiones de la matriz acumuladora y rango de parametros
    public Hough(int angleLenght, int radiusLenght){
        imageUtilities = new ImageUtilities();
        this.cumulativeMatrix = new int[angleLenght][radiusLenght];
        this.angleArray = new double[angleLenght];
        this.radiusArray = new double[radiusLenght];
        if (cumulativeMatrix!=null){
            for (int i = 0; i < cumulativeMatrix.length; i++) {
                for (int j = 0; j < cumulativeMatrix[0].length; j++) {
                    cumulativeMatrix[i][j] = 0;
                }
            }
        }
    }

    private void discretizeParameters(int[][] imageMatrix){
        if (angleArray !=null){
            for (int i = 0; i < angleArray.length; i++) {
                angleArray[i] = (i * ((360.0) / angleArray.length));
            }
        }

        if (radiusArray !=null){
            double value;
            for (int i = 0; i < radiusArray.length; i++) {
                //La discretizacion es para el rango total dividido la cantidad de posiciones
                radiusArray[i] = i * (400.0 / radiusArray.length);
                //radiusArray[i] = (Math.round(value * 10) / 10.0);
            }
        }
    }

    public BufferedImage findLines(BufferedImage bimg){
        System.out.println("*** Finding lines ***");
        BufferedImage newBimg = imageUtilities.copyImageIntoAnother(bimg);
        Image image = new Image(newBimg);
        image.convertToGreyDataMatrix();
        int[][] greyDataMatrix = image.getGreyDataMatrix();
        this.discretizeParameters(greyDataMatrix);
        System.out.println("*** Searching white pixels ***");
        //Recorro la matriz y donde hay un blanco inicio la votacion
        for (int i = 0; i < greyDataMatrix.length; i++) {
            for (int j = 0; j < greyDataMatrix[0].length; j++) {
                if(greyDataMatrix[i][j]==255){
                    //System.out.println(i + " " + j);
                    this.votePixel(i,j);
                }
            }
        }
        System.out.println("*** Finished searching white pixels ***");
        //Busco los mas votados
        int max = getMaxVotedParameters();
        System.out.println("Max votes: " +  max);
        int counter = 0;
        //Recorro la matriz acumulada para buscar los parametros dentro del 80% del maximo
        for (int i = 0; i < cumulativeMatrix.length; i++) {
            for (int j = 0; j < cumulativeMatrix[0].length; j++) {
                if (max > 5 && cumulativeMatrix[i][j] >= max * 0.5){
                    //Dibujo las lineas si supera el 80% del max
                        System.out.println("Dibujando linea con angulo: " + angleArray[i] + " radio: " + radiusArray[j] + " tipo: " + getLineType(angleArray[i]) + " Votes: " + cumulativeMatrix[i][j]);
                        newBimg = drawLine(newBimg, angleArray[i], radiusArray[j]);

                }
            }
        }
        System.out.println("*** Finished finding lines ***");
        return newBimg;
    }

    private void votePixel(int x, int y){
        //recorro los arrays de angulo y radio en el mismo sentido que recorreria la matriz acumuladora
        for (int i = 0; i < angleArray.length; i++) {
            for (int j = 0; j < radiusArray.length; j++) {
                double angleTemp = angleArray[i];
                double angle = angleTemp * Math.PI / 180;
                double radius = radiusArray[j];
                double A = x * Math.cos(angle);
                double B = y * Math.sin(angle);
                double line = Math.round(A + B);
                if(angleTemp == 76 && (radius > 48.0 && radius < 49.0)){
                    //if(x == 147 && y == 22) {
                        int test = 1;
                    //}
                }
                //Si la posicion del pixel pertenece a una recta dada por ambos parametros entonces incremento la matriz acumulada
                if ( Math.abs(line - radius) >= 0 && Math.abs(line -  radius) < 0.3){
                    cumulativeMatrix[i][j]++;
                }
            }
        }
    }

    public int getMaxVotedParameters(){
        System.out.println("*** Finding max voted parameters ***");
        int max = 0;
        //Recorro 1 vez para calcular el maximo
        for (int i = 0; i < cumulativeMatrix.length; i++) {
            for (int j = 0; j < cumulativeMatrix[0].length; j++) {
                if (max < cumulativeMatrix[i][j]){
                    max = cumulativeMatrix[i][j];
                }
            }
        }
        System.out.println("*** Finished finding max voted parameters ***");
        return max;
    }

    public BufferedImage drawLine(BufferedImage bimg, double angle, double radius){
        BufferedImage newBimg = imageUtilities.copyImageIntoAnother(bimg);
        angle = angle * Math.PI / 180;
        radius = Math.round(radius);
        Image image = new Image(newBimg);
        image.convertToGreyDataMatrix();

        Graphics2D g2d = newBimg.createGraphics();
        g2d.setColor(Color.RED );
        BasicStroke bs = new BasicStroke(1);
        g2d.setStroke(bs);

        if (angle !=0){
            for (int i = -1000; i < 1000; i++) {
                int x1 = i;
                int y1 = (int) Math.round((radius - (x1 * Math.cos(angle))) / Math.sin(angle));
                x1 = i;
                g2d.drawLine(x1,y1,x1,y1);
            }
        }
         else{
            for (int j = 0; j < newBimg.getHeight(); j++) {
                int x1 = (int) radius;
                int y1 = j;
                g2d.drawLine(x1,y1,x1,y1);
            }
        }
        return newBimg;
    }

    public LineType getLineType(double angle){
        //Importante el angulo se toma como referencia desde el (0,0) hasta la recta usando una perpendicular, si el angulo es 0 la linea es vertical
        if (angle == 0 || angle == 180){
            return LineType.VERTICAL;
        }
        else if (angle == 90 || angle == 270){
            return LineType.HORIZONTAL;
        }
        else if ((angle > 0 && angle < 90) ||(angle > 180 && angle < 270)){
            return LineType.DIAGONAL_UP;
        }
        else {
            return LineType.DIAGONAL_DOWN;
        }
    }

    public enum LineType{
        HORIZONTAL, VERTICAL, DIAGONAL_UP, DIAGONAL_DOWN
    }

}
