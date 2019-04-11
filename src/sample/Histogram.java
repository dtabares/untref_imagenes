package sample;

import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;

public class Histogram {

    private ImageUtilities imageUtilities;

    public Histogram(){
        this.imageUtilities = new ImageUtilities();
    }

    public double[] getHistogram(int [] channel){

        double [] histogram = new double [256];
        for (int i = 0; i < channel.length; i++) {
            histogram[channel[i]]+=1;
        }
        return histogram;

    }

    public void getImageHistogram(BufferedImage bimg) {

        boolean grey = imageUtilities.isGreyImage(bimg);
        int[][] channelMatrix = imageUtilities.getChannelMatrix(bimg);
        double redHistogram[] = this.getHistogram(channelMatrix[0]);
        double greenHistogram[] = this.getHistogram(channelMatrix[1]);
        double blueHistogram[] = this.getHistogram(channelMatrix[2]);
        displayHistogram(redHistogram, greenHistogram, blueHistogram, imageUtilities.isGreyImage(bimg));
    }

    public void displayHistogram(double[] redHistogram, double[] greenHistogram, double[] blueHistogram, boolean grey) {

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Histograma");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Niveles de Gris");
        //creating the chart
        final AreaChart<Number,Number> areaChart =
                new AreaChart<Number,Number>(xAxis,yAxis);
        Scene scene  = new Scene(areaChart,800,600);
        if(grey){
            //defining a series
            XYChart.Series series = new XYChart.Series();
            series.setName("Gris");
            int [] niveles = new int [256];
            for(int i = 0; i < 256; i++){
                niveles[i] = i ;
            }
            for (int i = 0; i < redHistogram.length; i++) {
                series.getData().add(new XYChart.Data(niveles[i],redHistogram[i]));
            }
            areaChart.setCreateSymbols(false); //hide dots
            areaChart.getData().add(series);
        }
        else{
            //defining a series
            XYChart.Series redSeries = new XYChart.Series();
            redSeries.setName("Rojo");
            int [] niveles = new int [256];
            for(int i = 0; i < 256; i++){
                niveles[i] = i ;
            }
            for (int i = 0; i < redHistogram.length; i++) {
                redSeries.getData().add(new XYChart.Data(niveles[i],redHistogram[i]));
            }
            areaChart.setCreateSymbols(false); //hide dots
            areaChart.getData().add(redSeries);
            //defining a series
            XYChart.Series greenSeries = new XYChart.Series();
            greenSeries.setName("Verde");
            for(int i = 0; i < 256; i++){
                niveles[i] = i ;
            }
            for (int i = 0; i < redHistogram.length; i++) {
                greenSeries.getData().add(new XYChart.Data(niveles[i],greenHistogram[i]));
            }
            areaChart.setCreateSymbols(false); //hide dots
            areaChart.getData().add(greenSeries);
            //defining a series
            XYChart.Series blueSeries = new XYChart.Series();
            blueSeries.setName("Azul");
            for(int i = 0; i < 256; i++){
                niveles[i] = i ;
            }
            for (int i = 0; i < redHistogram.length; i++) {
                blueSeries.getData().add(new XYChart.Data(niveles[i],blueHistogram[i]));
            }
            areaChart.setCreateSymbols(false); //hide dots
            areaChart.getData().add(blueSeries);
        }
        scene.getStylesheets().add("sample/Chart.css");
        newWindow.setScene(scene);
        newWindow.show();
    }

    public BufferedImage equalizeHistogram(BufferedImage bimg) {

        int rgb, red, green, blue;
        int pixelCount = bimg.getWidth() * bimg.getHeight();
        BufferedImage result = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
        int[][] channelMatrix = imageUtilities.getChannelMatrix(bimg);
        double redHistogram[] = this.getHistogram(channelMatrix[0]);
        double greenHistogram[] = this.getHistogram(channelMatrix[1]);
        double blueHistogram[] = this.getHistogram(channelMatrix[2]);
        double redRelativeFrequencies[] = new double[256];
        double greenRelativeFrequencies[] = new double[256];
        double blueRelativeFrequencies[] = new double[256];
        double redRealtiveFrequenciesSum[] = new double[256];
        double greenRealtiveFrequenciesSum[] = new double[256];
        double blueRealtiveFrequenciesSum[] = new double[256];
        double redSk[] = new double[256];
        double greenSk[] = new double[256];
        double blueSk[] = new double[256];
        //Calculo de frecuencias relativas
        for (int i = 0; i < redHistogram.length; i++) {
            redRelativeFrequencies[i] = redHistogram[i] / pixelCount;
            greenRelativeFrequencies[i] = greenHistogram[i] / pixelCount;
            blueRelativeFrequencies[i] = blueHistogram[i] / pixelCount;
        }
        //Acumulado de Frecuencias Relativas
        redRealtiveFrequenciesSum[0] = redRelativeFrequencies[0];
        greenRealtiveFrequenciesSum[0] = greenRelativeFrequencies[0];
        blueRealtiveFrequenciesSum[0] = blueRelativeFrequencies[0];
        for (int i = 1; i < 256; i++) {
            redRealtiveFrequenciesSum[i] = redRealtiveFrequenciesSum[i - 1] + redRelativeFrequencies[i];
            greenRealtiveFrequenciesSum[i] = greenRealtiveFrequenciesSum[i - 1] + greenRelativeFrequencies[i];
            blueRealtiveFrequenciesSum[i] = blueRealtiveFrequenciesSum[i - 1] + blueRelativeFrequencies[i];
        }
        //Ecualizacion
        for (int i = 1; i < redSk.length; i++) {
            redSk[i] = (int) Math.round(255 * redRealtiveFrequenciesSum[i]);
            greenSk[i] = (int) Math.round(255 * greenRealtiveFrequenciesSum[i]);
            blueSk[i] = (int) Math.round(255 * blueRealtiveFrequenciesSum[i]);
        }
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                rgb = bimg.getRGB(i, j);
                red = (int) redSk[ColorUtilities.getRed(rgb)];
                green = (int) greenSk[ColorUtilities.getGreen(rgb)];
                blue = (int) blueSk[ColorUtilities.getBlue(rgb)];
                result.setRGB(i, j, ColorUtilities.createRGB(red, green, blue));
            }

        }
    return result;
    }

}
