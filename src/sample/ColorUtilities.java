package sample;

import java.awt.image.BufferedImage;

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

    public static float[] RGBtoHSV(int rgbPixel)
    {
        int rgbRedComponent = getRed(rgbPixel);
        int rgbGreenComponent = getGreen(rgbPixel);
        int rgbBlueComponent = getBlue(rgbPixel);

        return RGBtoHSV(rgbRedComponent,rgbGreenComponent,rgbBlueComponent);
    }

    public static int[] HSVtoRGB(float hue, float saturation, float value)
    {
        int[] rgbValues = new int[3];
        int redValue = 0;
        int greenValue = 0;
        int blueValue = 0;

        if (saturation == 0)
        {
            redValue = Math.round(255 * value);
            greenValue = redValue;
            blueValue = redValue;
        }

        hue = hue - (float) Math.floor(hue);
        int i = (int) (6 * hue);

        float f = 6 * hue - i;
        float p = value * (1 - saturation);
        float q = value * (1 - saturation * f);
        float t = value * (1 - saturation * (1 - f));

        switch (i)
        {
            case 0:
                redValue = Math.round(255 * value);
                greenValue = Math.round(255 * t);
                blueValue = Math.round(255 * p);
                break;
            case 1:
                redValue = Math.round(255 * q);
                greenValue = Math.round(255 * value);
                blueValue = Math.round((255 * p));
                break;
            case 2:
                redValue = Math.round(255 * p);
                greenValue = Math.round(255 * value);
                blueValue = Math.round(255 * t);
                break;
            case 3:
                redValue = Math.round(255 * p);
                greenValue = Math.round(255 * q);
                blueValue = Math.round(255 * value);
                break;
            case 4:
                redValue = Math.round(255 * t);
                greenValue = Math.round(255 * p);
                blueValue = Math.round(255 * value);
                break;
            case 5:
                redValue = Math.round(255 * value);
                greenValue = Math.round(255 * p);
                blueValue = Math.round(255 * q);
        }
        rgbValues[0] = redValue;
        rgbValues[1] = greenValue;
        rgbValues[2] = blueValue;
        return rgbValues;
    }

    public static float[] RGBtoHSV(int red, int green, int blue)
    {
        float hue, saturation, value;
        float[] hsbvalues = new float[3];

        float cmax = Math.max(red,Math.max(green,blue));
        float cmin = Math.min(red,Math.min(green,blue));
        float delta = cmax - cmin;
        System.out.println("cmax: " + cmax);
        System.out.println("cmin: " + cmin);
        System.out.println("Delta: " + delta);

        value = cmax / 255f;

        if (cmax == 0)
        {
            saturation = 0;
        }
        else
        {
            saturation = (delta/cmax);
        }

        if (delta == 0)
        {
            hue = 0;
        }
        else
        {
            if (cmax == red)
            {
                hue = ((green - blue)/(delta * 6));
            }
            else
            {
                if(cmax == green)
                {
                    hue = 1f / 3 + (blue - red) / delta;
                }
                else
                {
                    hue = 2f / 3 + (red - green) / delta;
                }
            }
            if (hue < 0)
            {
                hue = hue + 1f;
            }
        }
        hsbvalues[0] = hue;
        hsbvalues[1] = saturation;
        hsbvalues[2] = value;

        return hsbvalues;
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

    public static byte byteFromRGB(int rgb)
    {
        return (byte) rgb;
    }
}
