package sample;

import javax.sound.sampled.Line;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Hough {

    private int [][] cumulativeMatrix;
    private double [] angleArray;
    private double [] radiusArray;

    //Para crear el objeto debo indicar las dimensiones de la matriz acumuladora y rango de parametros
    public Hough(int angleLenght, int radiusLenght){
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
                //La discretizacion es para el rango total dividido la cantidad de posiciones, este va de -90 a 90
                angleArray[i] = (i * ((180.0) / angleArray.length)) - 90;
            }
            //Hardcodeo el valor 90 al final del array para no perderlo
            angleArray[angleArray.length-1]=90.0;
        }
        if (radiusArray !=null){
            for (int i = 0; i < radiusArray.length; i++) {
                //La discretizacion es para el rango total dividido la cantidad de posiciones
                radiusArray[i] = i * (100.0 / radiusArray.length);
            }
        }
    }

    public BufferedImage findLines(BufferedImage bimg){
        Image image = new Image(bimg);
        image.convertToGreyDataMatrix();
        int[][] greyDataMatrix = image.getGreyDataMatrix();
        this.discretizeParameters(greyDataMatrix);
        //Recorro la matriz y donde hay un blanco inicio la votacion
        for (int i = 0; i < greyDataMatrix.length; i++) {
            for (int j = 0; j < greyDataMatrix[0].length; j++) {
                if(greyDataMatrix[i][j]==255){
                    System.out.println(i + " " + j);
                    this.votePixel(i,j);
                }
            }
        }
        //Busco los mas votados
        int max = getMaxVotedParameters();
        //Recorro la matriz acumulada para buscar los parametros dentro del 80% del maximo
        for (int i = 0; i < cumulativeMatrix.length; i++) {
            for (int j = 0; j < cumulativeMatrix[0].length; j++) {
                if (max > 10 && cumulativeMatrix[i][j] >= max * 0.8){
                    //Dibujo las lineas si supera el 80% del max
                    System.out.println("Dibujando linea con angulo: " +  angleArray[i] + " radio: " + radiusArray[j] + " tipo: " + getLineType(angleArray[i]));
                    bimg = drawLine(bimg, angleArray[i], radiusArray[j]);
                }
            }
        }
        return bimg;
    }

    private void votePixel(int x, int y){
        //recorro los arrays de angulo y radio en el mismo sentido que recorreria la matriz acumuladora
        for (int i = 0; i < angleArray.length; i++) {
            for (int j = 0; j < radiusArray.length; j++) {
                double angle = angleArray[i] * Math.PI / 180;
                double radius = radiusArray[j];
                double line = Math.round(x * Math.cos(angle)) + (y * Math.sin(angle));
                //Si la posicion del pixel pertenece a una recta dada por ambos parametros entonces incremento la matriz acumulada
                if ( line == radius){
                    cumulativeMatrix[i][j]++;
                }
            }
        }
    }

    public int getMaxVotedParameters(){
        int max = 0;
        //Recorro 1 vez para calcular el maximo
        for (int i = 0; i < cumulativeMatrix.length; i++) {
            for (int j = 0; j < cumulativeMatrix[0].length; j++) {
                if (max < cumulativeMatrix[i][j]){
                    max = cumulativeMatrix[i][j];
                }
            }
        }

        return max;
    }

    public BufferedImage drawLine(BufferedImage bimg, double angle, double radius){
        LineType type = getLineType(angle);
        angle = angle * Math.PI / 180;
        radius = Math.round(radius);
        Image image = new Image(bimg);
        image.convertToGreyDataMatrix();

        Graphics2D g2d = bimg.createGraphics();
        g2d.setColor(Color.RED );
        BasicStroke bs = new BasicStroke(1);
        g2d.setStroke(bs);

        if (angle !=0){
            for (int i = -1000; i < 1000; i++) {
                int x1 = i;
                int y1 = (int) Math.round( radius - (x1*Math.cos(angle)) / Math.sin(angle));
                x1 = i;
                g2d.drawLine(x1,y1,x1,y1);
            }
        }
         else{
            for (int j = 0; j < bimg.getHeight(); j++) {
                int x1 = (int) radius;
                int y1 = j;
                g2d.drawLine(x1,y1,x1,y1);
            }
        }

        return bimg;
    }

    public LineType getLineType(double angle){
        //Importante el angulo se toma como referencia desde el (0,0) hasta la recta usando una perpendicular, si el angulo es 0 la linea es vertical
        if (angle == 0 ){
            return LineType.VERTICAL;
        }
        else if (angle == -90 || angle == 90){
            return LineType.HORIZONTAL;
        }
        else if (angle > -90 && angle < 0){
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
