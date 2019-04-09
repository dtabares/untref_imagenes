package sample;

public class Channel {

    private int [][] matrix;
    private int width;
    private int height;

    public Channel(int width, int height) {
        this.matrix = new int[width][height];
        this.width = width;
        this.height = height;
    }

    public void setValue(int x, int y, int value)
    {
        this.matrix[x][y] = value;
    }

    public int getValue(int x, int y)
    {
        return this.matrix[x][y];
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public int getMaxValue()
    {
        int maxValue = 0;
        for (int i = 0; i < this.getWidth(); i++)
        {
            for (int j = 0; j < this.getHeight(); j++) {

                if (this.getValue(i,j) > maxValue)
                {
                    maxValue = this.getValue(i,j);
                }
            }
        }

        return maxValue;
    }
}
