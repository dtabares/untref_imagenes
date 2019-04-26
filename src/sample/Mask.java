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

    public Mask(){

    }

    public void setMeanMask(){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < this.matrix[0].length; j++){
                this.matrix[i][j] = (double) 1/(size*size);
            }
        }
    }

    public void setMedianMask(){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < this.matrix[0].length; j++){
                this.matrix[i][j] = 1;
            }
        }
    }

    public void setWeightedMedianMask(){
        if (matrix != null){
            matrix[0][0] = 1;
            matrix[0][1] = 2;
            matrix[0][2] = 1;
            matrix[1][0] = 2;
            matrix[1][1] = 4;
            matrix[1][2] = 2;
            matrix[2][0] = 1;
            matrix[2][1] = 2;
            matrix[2][2] = 1;
        }

    }

    public void setGaussMask(double sigma) {
        double suma = 0;
        double valor;
        int radius = size / 2;
        //se aplica la formula con el 0,0 centrado en la mascara
        for (int i = 0 - radius; i < matrix.length - radius; i++) {
            for (int j = 0 - radius; j < this.matrix[0].length - radius; j++) {
                //fraccion representa la primer parte de la formula
                double fraccion = (1.0 / (2.0 * Math.PI * Math.pow(sigma, 2)));
                //e representa la segunda parte de la formula
                double e = Math.exp(-(Math.pow(i, 2) + Math.pow(j, 2)) / (Math.pow(sigma, 2)*2));
                valor = fraccion * e;
                this.matrix[i + radius][j + radius] = valor;
                //la variable suma es para analizar la matriz
                suma += valor;
            }
        }
        //printMask();
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
                //System.out.println("i: " +i + " j: " + j + " value:" + this.matrix[i][j]);

            }
        }
    }

    public void setHorizontalPrewittMask(){
        this.size = 3;
        this.matrix = new double[][] {{-1,-1,-1},{0,0,0},{1,1,1}};
        this.center = this.size/2;
    }

    public void setVericalPrewittMask(){
        this.size = 3;
        this.matrix = new double[][] {{-1,0,1},{-1,0,1},{-1,0,1}};
        this.center = this.size/2;
    }

    public void setHorizontalSobelMask(){
        this.size = 3;
        this.matrix = new double[][] {{-1,-2,-1},{0,0,0},{1,2,1}};
        this.center = this.size/2;
    }

    public void setVericalSobelMask(){
        this.size = 3;
        this.matrix = new double[][] {{-1,0,1},{-2,0,2},{-1,0,1}};
        this.center = this.size/2;
    }

    public void setLaplaceMask(){
        this.size = 3;
        this.matrix = new double[][] {{0,-1,0},{-1,4,-1},{0,-1,0}};
        this.center = this.size/2;
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

    public int getMaskSum(){
        int sum = 0;
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < this.matrix[0].length; j++){
                sum+=matrix[i][j];
            }
        }
        return sum;
    }

    public void printMask(){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length;j++){
                System.out.println(matrix[i]);
            }
        }
    }

}
