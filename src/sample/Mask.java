package sample;

public class Mask {

    private int size;
    private double matrix[][];
    private int positionX;
    private int positionY;

    public Mask(int size){
        this.size = size;
        this.matrix = new double[size][size];
        this.positionX = 0;
        this.positionY = 0;
    }

    public void setMeanMask(){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < this.matrix[0].length; j++){
                this.matrix[i][j] = (double) 1/(size*size);
            }
        }
    }

    public void setGaussMask(double sigma){
        double suma=0;
        double valor;
        int radius = size/2;
        for (int i = 0 - radius; i < matrix.length - radius; i++) {
            for (int j = 0 - radius; j < this.matrix[0].length - radius; j++) {
                double fraccion = (1.0 / (2.0 * Math.PI * Math.pow(sigma, 2)));
                double e = Math.exp(-(Math.pow(i, 2) + Math.pow(j, 2)) / (Math.pow(sigma, 2)));
                valor = fraccion * e;
                this.matrix[i + radius][j + radius] = valor;
                suma+=valor;
            }
        }
        //printMask();
    }

    public int getSize(){
        return this.size;
    }

    public double[][] getMatrix(){
        return this.matrix;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void moveX() {
        this.positionX++;
    }

    public void moveY() {
        this.positionY++;
    }

    public void resetX(){
        this.positionX = 0;
    }

    public void restetY(){
        this.positionY = 0;
    }

    public int getCenterX(){
        return (int) positionX+(size/2);
    }
    public int getCenterY(){
        return (int) positionY+(size/2);
    }

    public void printMask(){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length;j++){
                System.out.println(matrix[i]);
            }
        }
    }
}
