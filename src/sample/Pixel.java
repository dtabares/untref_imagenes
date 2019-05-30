package sample;

public class Pixel {

    private int x;
    private int y;
    private int value;

    public Pixel(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }

    public boolean equals(Pixel p){
        return (p.x == x && p.y == y && p.value == value);
    }

}
