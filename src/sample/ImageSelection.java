package sample;

public class ImageSelection {

    private int xOrigin;
    private int yOrigin;
    private int xFinal;
    private int yFinal;
    private int width;
    private int height;
    private boolean firstClickCoordinatesSubmitted;
    private boolean secondClickCoordinatesSubmitted;
    private int size;

    public ImageSelection() {
        this.firstClickCoordinatesSubmitted = false;
        this.secondClickCoordinatesSubmitted = false;
    }

    public int getxOrigin() {
        return xOrigin;
    }

    public int getyOrigin() {
        return yOrigin;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void submitClickCoordinates(int x, int y)
    {
        if (this.firstClickCoordinatesSubmitted == false)
        {
            this.xOrigin = x;
            this.yOrigin = y;
            this.firstClickCoordinatesSubmitted = true;
        }
        else
        {
            this.xFinal = x;
            this.yFinal = y;
            this.secondClickCoordinatesSubmitted = true;
        }
    }

    public boolean allCoordinatesSubmitted()
    {
        return this.firstClickCoordinatesSubmitted && this.secondClickCoordinatesSubmitted;
    }

    public void reset()
    {
        this.firstClickCoordinatesSubmitted = false;
        this.secondClickCoordinatesSubmitted = false;
    }

    public void calculateWithAndHeight(){
        //First I need to make sure the smallest X coordinate is set as the origin
        if (this.xFinal < this.xOrigin)
        {
            int temp = this.xOrigin;
            this.xOrigin = this.xFinal;
            this.xFinal = temp;
        }

        //same for Y
        if (this.yFinal < this.yOrigin)
        {
            int temp = this.yOrigin;
            this.yOrigin = this.yFinal;
            this.yFinal = temp;
        }

        this.height = this.yFinal - this.yOrigin;
        this.width = this.xFinal - this.xOrigin;
        this.size = this.width * this.height;
    }

    public int getxFinal() {
        return xFinal;
    }

    public int getyFinal() {
        return yFinal;
    }

    public int getSize() {
        return size;
    }
}
