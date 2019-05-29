package sample;

public class Pixel {

    public int x;
    public int y;
    public int value;

    public boolean equals(Pixel p){
        return (p.x == x && p.y == y && p.value == value);
    }

}
