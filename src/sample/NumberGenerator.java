package sample;

public class NumberGenerator {

    public static int generateRandomExponentialNumber(double lambda){
        double x;
        int randomNumber;
        x = Math.random();
        System.out.println(x);
        while (x == 0){
            x = Math.random();
        }
        System.out.println(x);

        randomNumber = (int) Math.round((-1 * Math.log(x)/ lambda));

        return randomNumber;
    }
    public static int generateRandomGaussianNumber(double mean, double sd){
        double randomNumber;
        double x1 = Math.random();
        double x2 = Math.random();

        randomNumber = (Math.sqrt(-2 * Math.log(x1)) * Math.cos(2 * Math.PI * x2));
        randomNumber = (randomNumber * sd + mean);

        return (int) Math.round(randomNumber);
    }
    public static int generateRandomRayleighNumber(double phi){

        int randomNumber;
        double x = Math.random();

        randomNumber = (int) (phi * Math.sqrt( -2 * Math.log(1 - x)));

        return randomNumber;
    }
}
