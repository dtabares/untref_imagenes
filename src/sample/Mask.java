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

    public void setHighPassFilterMask(){
        double centerValue = ((this.size * this.size) - 1.0)/(size*size);
        double otherValue = (- 1.0)/(size*size);
        for (int i = 0; i < matrix.length ; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (i == this.center && j == this.center){
                    this.matrix[i][j] = centerValue;
                }
                else{
                    this.matrix[i][j] = otherValue;
                }
                System.out.println("i: " +i + " j: " + j + " value:" + this.matrix[i][j]);

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
