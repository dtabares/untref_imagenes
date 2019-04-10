package sample;

public class NumberGenerator {

    public static int generateRandomExponentialNumber(double lambda){
        double x;
        int randomNumber;
        x = Math.random();

        if (x == 0){
            randomNumber = 0;
        }
        else {
            randomNumber = (int) Math.round((-1 * Math.log(x)/ lambda));
        }

        System.out.println("Random Exp Number: " + randomNumber);
        return randomNumber;
    }


    public static int generateRandomGaussianNumber(double mean, double sd){
        int randomNumber;
        double x1 = Math.random();
        double x2 = Math.random();

        randomNumber = (int) (Math.sqrt(-2 * Math.log(x1)) * Math.cos(2 * Math.PI * x2));
        System.out.println("Random Gaussian Number: " + randomNumber);
        return (int) (randomNumber * sd + mean);
    }

    public static int generateRandomRayleighNumber(double phi){

        int randomNumber;
        double x = Math.random();

        randomNumber = (int) (phi * Math.sqrt( -2 * Math.log(1 - x)));
        System.out.println("Random Rayleigh Number: " + randomNumber);
        return randomNumber;

    }
}
