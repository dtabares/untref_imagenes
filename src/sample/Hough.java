package sample;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Hough {

    private int [][] cumulativeMatrix;
    private int [][][] cumulativeMatrixCircle;
    private double [] angleArray;
    private double [] radiusArray;
    private int [] xCenterArray;
    private int [] yCenterArray;
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

    public Hough(BufferedImage bimg, double maxRadius){
        imageUtilities = new ImageUtilities();
        int width = bimg.getWidth();
        int height = bimg.getHeight();
        int r = (int) Math.round(maxRadius);
        //int radiusMax = (int) Math.round(Math.sqrt(Math.pow(width,2) + Math.pow(height,2)));
        this.cumulativeMatrixCircle = new int[width][height][r];
        this.xCenterArray = new int[width];
        this.yCenterArray = new int[height];
        this.radiusArray = new double[r];
        if (cumulativeMatrixCircle!=null){
            for (int i = 0; i < cumulativeMatrixCircle.length; i++) {
                for (int j = 0; j < cumulativeMatrixCircle[0].length; j++) {
                    for (int k = 0; k < cumulativeMatrixCircle[0][0].length; k++) {
                        cumulativeMatrixCircle[i][j][k] = 0;
                    }
                }
            }
        }
    }

    private void discretizeParameters(int[][] imageMatrix, GeometricType t){
        switch (t){
            case LINE:
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
                break;
            case CIRCLE:
                if(xCenterArray !=null){
                    for (int i = 0; i < xCenterArray.length; i++) {
                        xCenterArray[i] = i * (int) (Math.round((double) imageMatrix.length / xCenterArray.length));
                    }
                }
                if(yCenterArray !=null){
                    for (int i = 0; i < yCenterArray.length; i++) {
                        yCenterArray[i] = i * (int) (Math.round((double) imageMatrix[0].length / yCenterArray.length));
                    }
                }
                if (radiusArray !=null){
                    double value;
                    for (int i = 0; i < radiusArray.length; i++) {
                        radiusArray[i] = i;
                    }
                }
                break;
        }
    }

    public BufferedImage findLines(BufferedImage bimg, double percent, GeometricType t){
        BufferedImage newBimg = imageUtilities.copyImageIntoAnother(bimg);
        Image image = new Image(newBimg);
        image.convertToGreyDataMatrix();
        int[][] greyDataMatrix = image.getGreyDataMatrix();
        this.discretizeParameters(greyDataMatrix,t);
        System.out.println("*** Searching white pixels ***");
        //Recorro la matriz y donde hay un blanco inicio la votacion
        for (int i = 0; i < greyDataMatrix.length; i++) {
            for (int j = 0; j < greyDataMatrix[0].length; j++) {
                if(greyDataMatrix[i][j]==255){
                    this.votePixel(i,j,t);
                }
            }
        }
        System.out.println("*** Finished searching white pixels ***");
        //Busco los mas votados
        int max = getMaxVotedParameters(t);
        System.out.println("Max votes: " +  max);
        //Recorro la matriz acumulada para buscar los parametros dentro del 80% del maximo
        switch (t){
            case LINE:
                for (int i = 0; i < cumulativeMatrix.length; i++) {
                    for (int j = 0; j < cumulativeMatrix[0].length; j++) {
                        if (max > 5 && cumulativeMatrix[i][j] >= max * (percent/100)){
                            //Dibujo las lineas si supera el 80% del max
                            System.out.println("Dibujando linea con angulo: " + angleArray[i] + " radio: " + radiusArray[j] + " tipo: " + getLineType(angleArray[i]) + " Votes: " + cumulativeMatrix[i][j]);
                            newBimg = drawLine(newBimg, angleArray[i], radiusArray[j]);
                        }
                    }
                }
                break;
            case CIRCLE:
                for (int i = 0; i < cumulativeMatrixCircle.length; i++) {
                    for (int j = 0; j < cumulativeMatrixCircle[0].length; j++) {
                        for (int k = 0; k < cumulativeMatrixCircle[0][0].length; k++) {
                            if (max > 5 && cumulativeMatrixCircle[i][j][k] >= max * (percent/100)){
                                //Dibujo las lineas si supera el 80% del max
                                System.out.println("Dibujando circulo con centro en x: " + xCenterArray[i] + " y: "+ yCenterArray[j]+" radio: " + radiusArray[k] + " Votes: " + cumulativeMatrixCircle[i][j][k]);
                                newBimg = drawCircle(newBimg, xCenterArray[i], yCenterArray[j], radiusArray[k]);
                            }
                        }
                    }
                }

                break;
        }
        return newBimg;
    }

    private void votePixel(int x, int y, GeometricType t){
        double formula;
        switch (t){
            case LINE:
                double angle,radius;
                //recorro los arrays de angulo y radio en el mismo sentido que recorreria la matriz acumuladora
                for (int i = 0; i < angleArray.length; i++) {
                    for (int j = 0; j < radiusArray.length; j++) {
                        angle = angleArray[i] * Math.PI / 180; // El angulo debe estar en radianes
                        radius = radiusArray[j];
                        formula = (x * Math.cos(angle)) + (y * Math.sin(angle));
                        //Si la posicion del pixel pertenece a una recta dada por ambos parametros entonces incremento la matriz acumulada
                        if ( Math.abs(formula - radius) >= 0 && Math.abs(formula -  radius) < 0.4){
                            cumulativeMatrix[i][j]++;
                        }
                    }
                }
                break;
            case CIRCLE:
                int xCenter,yCenter;
                double circleRadius;
                //recorro los arrays de angulo y radio en el mismo sentido que recorreria la matriz acumuladora
                for (int i = 0; i < xCenterArray.length; i++) {
                    for (int j = 0; j < yCenterArray.length; j++) {
                        for (int k = 0; k < radiusArray.length; k++) {
                            xCenter = xCenterArray[i];
                            yCenter = yCenterArray[j];
                            circleRadius = radiusArray[k];
                            formula  = Math.round(Math.sqrt(Math.pow(x-xCenter,2)+Math.pow(y-yCenter,2)) - circleRadius);
                            //Si si la distancia entre el centro y el punto es igual al radio entonces pertenece al circulo
                            if(i==50 && j==50 && (circleRadius > 35)){
                                double test = formula;
                            }
                            if (formula >= 0 && formula < 0.1){
                                cumulativeMatrixCircle[i][j][k]++;
                            }
                        }
                    }
                }
                break;
        }

    }

    public int getMaxVotedParameters(GeometricType t){
        System.out.println("*** Finding max voted parameters ***");
        int max = 0;
        switch (t){
            case LINE:
                //Recorro la matriz acumuladora para calcular el maximo
                for (int i = 0; i < cumulativeMatrix.length; i++) {
                    for (int j = 0; j < cumulativeMatrix[0].length; j++) {
                        if (max < cumulativeMatrix[i][j]){
                            max = cumulativeMatrix[i][j];
                        }
                    }
                }
                break;
            case CIRCLE:
                //Recorro la matriz acumuladora para calcular el maximo
                for (int i = 0; i < cumulativeMatrixCircle.length; i++) {
                    for (int j = 0; j < cumulativeMatrixCircle[0].length; j++) {
                        for (int k = 0; k < cumulativeMatrixCircle[0][0].length; k++) {
                            if (max < cumulativeMatrixCircle[i][j][k]){
                                max = cumulativeMatrixCircle[i][j][k];
                            }
                        }
                    }
                }
                break;
        }
        System.out.println("*** Finished finding max voted parameters ***");
        return max;
    }

    public BufferedImage drawLine(BufferedImage bimg, double angle, double radius){
        BufferedImage newBimg = imageUtilities.copyImageIntoAnother(bimg,13);
        angle = angle * Math.PI / 180;
        Image image = new Image(newBimg);
        image.convertToGreyDataMatrix();

        //Configuracion para graficar
        Graphics2D g2d = newBimg.createGraphics();
        g2d.setColor(Color.RED );
        BasicStroke bs = new BasicStroke(1);
        g2d.setStroke(bs);

        //Grafico las lineas con cuidado de que si el angulo es 0 la linea es vertical y se calcula distinto
        if (angle !=0){
            for (int i = 0; i < newBimg.getWidth(); i++) {
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

    public BufferedImage drawCircle(BufferedImage bimg, int xCenter, int yCenter, double circleRadius){
        BufferedImage newBimg = imageUtilities.copyImageIntoAnother(bimg,13);
        Image image = new Image(newBimg);
        image.convertToGreyDataMatrix();

        //Configuracion para graficar
        Graphics2D g2d = newBimg.createGraphics();
        g2d.setColor(Color.RED );
        BasicStroke bs = new BasicStroke(1);
        g2d.setStroke(bs);

        //Grafico un Circulo marcando el x,y de la esquina superior del rectangulo que lo contiene
        g2d.drawOval(xCenter-(int)circleRadius,yCenter-(int)circleRadius,(int)circleRadius*2,(int)circleRadius*2);
        return newBimg;
    }

    public LineType getLineType(double angle){
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

    public enum GeometricType{
        LINE, CIRCLE
    }

}
