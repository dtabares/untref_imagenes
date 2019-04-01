package sample;

public class ColorUtilities {

    public static int getRed(int rgb_pixel)
    {
        //>> (right shift)
        //Binary Right Shift Operator. The left operands value is moved right by the number of bits specified by the right operand.
        return (rgb_pixel >> 16) & 0xFF;
    }

    public static int getGreen(int rgb_pixel)
    {
        return (rgb_pixel >> 8) & 0xFF;
    }

    public static int getBlue(int rgb_pixel)
    {
        return (rgb_pixel >> 0) & 0xFF;
    }

    public static int createRGB(int red, int green, int blue)
    {
        //<< (left shift)
        //Binary Left Shift Operator. The left operands value is moved left by the number of bits specified by the right operand.
        final int default_alpha = 255;
        return (default_alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int createRGB(byte byteContent)
    {
        final int default_alpha = 255;
        int alpha = default_alpha << 24;
        int red = ((int) byteContent & 0xFF) << 16;
        int green = ((int) byteContent & 0xFF) << 8;
        int blue = ((int) byteContent & 0xFF);

        int rgb = alpha + red + green + blue;
        return rgb;

    }
}
