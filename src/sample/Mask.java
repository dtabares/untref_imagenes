package sample;

public class Mask {

    private int size;
    private double matrix[][];
    private int center;


    public Mask(int size){
        this.size = size;
        this.matrix = new double[size][size];
        this.center = size/2;
    }

    public void setMeanMask(){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < this.matrix[0].length; j++){
                this.matrix[i][j] = (double) 1/(size*size);
            }
        }
    }

    public int getSize(){
        return this.size;
    }

    public double[][] getMatrix(){
        return this.matrix;
    }

    public double getValue(int x, int y){
        return this.matrix[x][y];
    }

    public int getCenter(){
        return this.center;
    }

}
