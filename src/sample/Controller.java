package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import static sample.InterfaceHelper.getInputDialog;


public class Controller extends BorderPane {

    private BufferedImage leftImage = null;
    private List<BufferedImage> rightPaneImageList;
    private List<BufferedImage> historyImageList;
    private ImageUtilities imageUtilities;
    private Filter filter;

    @FXML private AnchorPane leftPane;
    @FXML private AnchorPane rightPane;
    @FXML private Text txtBottom;

    public void initialize()throws IOException{
        System.out.println("Starting...");
        this.rightPaneImageList = new LinkedList<>();
        this.historyImageList = new LinkedList<>();
        this.imageUtilities = new ImageUtilities();
        this.filter = new Filter();
        BufferedImage bimg = imageUtilities.openRawImage(new File("src/sample/lena512.raw"),512,512);
        leftImage = bimg;
        this.displayImageInPane(bimg,leftPane);
        this.test();
    }

    //Top Menu
    @FXML public BufferedImage openImageFile(){
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        BufferedImage bimg = null;
        try
        {
            fc.setTitle("Select Directory");
            File f = fc.showOpenDialog(browser);
            String fileExtension = this.imageUtilities.getImageExtension(f.getName());
            if(this.imageUtilities.isSupportedFormat(fileExtension))
            {
                switch(fileExtension)
                {
                    case "raw":
                        int width = Integer.valueOf(getInputDialog("Open Image","Raw Image Information","Insert Image Width"));
                        int height = Integer.valueOf(getInputDialog("Open Image","Raw Image Information","Insert Image Height"));
                        bimg = this.imageUtilities.openRawImage(f,width,height);
                        leftImage = bimg;
                        //wimg = this.imageUtilities.readRawImage(bimg,width,height);
                        break;
                    case "pgm":
                        bimg = this.imageUtilities.readPGM(f);
                        leftImage = bimg;
                        break;
                    default:
                        bimg = ImageIO.read(f);
                        leftImage = bimg;
                }
                this.displayImageInPane(bimg,leftPane);
                System.out.println(imageUtilities.isGreyImage(bimg));
                fc.setInitialDirectory(null);
            }
            else
            {
                Alerts.showAlert("La extension del archivo no esta soportada");
            }
        }
        catch (Exception e)
        {

            Alerts.showAlert(e.getMessage());
        }
        return bimg;
    }
    @FXML public void saveImageFile(){
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        BufferedImage imageToBeSaved = this.getLastModifiedImage();
        if (imageToBeSaved != null)
        {
            try {
                fc.setTitle("Select File");
                File f = fc.showSaveDialog (browser);
                this.imageUtilities.WriteImage(imageToBeSaved, f);
            }
            catch (Exception e)
            {
                Alerts.showAlert(e.getMessage());
            }
            fc.setInitialDirectory(null);
        }
        else
            {
                Alerts.showAlert("There is no image to save");
            }
    }
    @FXML public void closeApplication(){
        System.exit(0);
    }
    @FXML public void undo(){
        if(!rightPaneImageList.isEmpty()){
            historyImageList.add(rightPaneImageList.get(rightPaneImageList.size()-1));
            rightPaneImageList.remove(rightPaneImageList.size()-1);
            if(!rightPaneImageList.isEmpty()) {
                WritableImage wimg = imageUtilities.readImage(rightPaneImageList.get(rightPaneImageList.size()-1));
                ImageView image = new ImageView(wimg);
                rightPane.getChildren().setAll(image);
            }
            else
            {
                rightPane.getChildren().setAll();
            }
        }
        else
        {
            Alerts.showAlert("No hay nada para deshacer");
        }
    }
    @FXML public void redo(){
        if(!historyImageList.isEmpty())
        {
            rightPaneImageList.add(historyImageList.get(historyImageList.size()-1));
            historyImageList.remove(historyImageList.get(historyImageList.size()-1));
            WritableImage wimg = imageUtilities.readImage(rightPaneImageList.get(rightPaneImageList.size()-1));
            ImageView image = new ImageView(wimg);
            rightPane.getChildren().setAll(image);
        }
        else{
            Alerts.showAlert("No hay nada para rehacer");
        }
    }
    @FXML public void reset(){
        rightPaneImageList = new LinkedList<>();
        historyImageList = new LinkedList<>();
        rightPane.getChildren().setAll();
    }
    @FXML public void setRightToLeft(){
        if(!rightPaneImageList.isEmpty()) {
            leftImage = rightPaneImageList.get(rightPaneImageList.size()-1);
            rightPaneImageList.remove(rightPaneImageList.size()-1);
            this.reset();
            this.displayImageInPane(leftImage,leftPane);
        }
        else{
            Alerts.showAlert("No hay imagen para mover");
        }
    }
    @FXML public void enlargeLeftImage(){
        if (leftImage != null){
            int scaleFactor = Integer.valueOf(getInputDialog("Enlarge Image", "Enter a new Value", "scale Factor:"));
            BufferedImage newImage = this.imageUtilities.resize(leftImage,scaleFactor);
            this.displayImageInPane(newImage,leftPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen abierta");
        }
    }

    @FXML public void enlargeRightImage(){
        if (this.getLastModifiedImage() != null){
            int scaleFactor = Integer.valueOf(getInputDialog("Enlarge Image", "Enter a new Value", "scale Factor:"));
            BufferedImage newImage = this.imageUtilities.resize(this.getLastModifiedImage(),scaleFactor);
            this.displayImageInPane(newImage,rightPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen en el panel derecho");
        }
    }

    //TP0
    @FXML public void getPixelInformation(){
        try{
            ImageView leftImageView = null;
            ImageView rightImageView = null;
            if (!leftPane.getChildren().isEmpty())
            {
                leftImageView = (ImageView) leftPane.getChildren().get(0);
            }

            if (!rightPane.getChildren().isEmpty())
            {
                rightImageView = (ImageView) rightPane.getChildren().get(0);
            }

            if (leftImageView == null && rightImageView == null)
            {
                Alerts.showAlert("No hay una imagen cargada!");
            }
            else
            {
                if (leftImageView != null)
                {
                    leftImageView.setOnMouseClicked(e -> {
                        System.out.println("Left Coordinates Info: ["+e.getX()+", "+e.getY()+"]");
                        String message = this.imageUtilities.getPixelInformation(leftImage,(int)e.getX(),(int)e.getY());
                        this.setBottomText(message);
                    });
                }


                if (rightImageView != null) {
                    rightImageView.setOnMouseClicked(e -> {
                        System.out.println("Right Coordinates Info:[" + e.getX() + ", " + e.getY() + "]");
                        String message = this.imageUtilities.getPixelInformation(rightPaneImageList.get(rightPaneImageList.size()), (int) e.getX(), (int) e.getY());
                        this.setBottomText(message);
                    });
                }
            }

        }
        catch(Exception e){
            Alerts.showAlert(e.getMessage());
        }
    }
    @FXML public void modifyPixelInformation(){
        try{
            ImageView leftImageView = null;
            ImageView rightImageView = null;
            if (!leftPane.getChildren().isEmpty())
            {
                leftImageView = (ImageView) leftPane.getChildren().get(0);
            }

            if (!rightPane.getChildren().isEmpty())
            {
                rightImageView = (ImageView) rightPane.getChildren().get(0);
            }

            if (leftImageView == null && rightImageView == null)
            {
                Alerts.showAlert("No hay una imagen cargada!");
            }
            else
            {
                int red = Integer.valueOf(getInputDialog("Modify Pixel Information", "Enter a new Value", "Red:"));
                int green = Integer.valueOf(getInputDialog("Modify Pixel Information", "Enter a new Value", "Green:"));
                int blue = Integer.valueOf(getInputDialog("Modify Pixel Information", "Enter a new Value", "Blue:"));
                if (leftImageView != null)
                {
                    leftImageView.setOnMouseClicked(e -> {
                        System.out.println("Left Coordinates Info: ["+e.getX()+", "+e.getY()+"]");
                        BufferedImage modifiedImage = this.imageUtilities.modifyPixelInformation(leftImage,(int)e.getX(),(int)e.getY(),red,green,blue);
                        this.displayImageInPane(modifiedImage,rightPane);
                    });
                }


                if (rightImageView != null) {
                    rightImageView.setOnMouseClicked(e -> {
                        System.out.println("Right Coordinates Info:[" + e.getX() + ", " + e.getY() + "]");
                        BufferedImage modifiedImage = this.imageUtilities.modifyPixelInformation(rightPaneImageList.get(rightPaneImageList.size()), (int) e.getX(), (int) e.getY(),red,green,blue);
                        this.displayImageInPane(modifiedImage,rightPane);
                    });
                }
            }
        }
        catch(Exception e){
            Alerts.showAlert(e.getMessage());
        }
    }
    @FXML public void copyImageSelection(){

        try{
            ImageView leftImageView = null;
            if (!leftPane.getChildren().isEmpty())
            {
                leftImageView = (ImageView) leftPane.getChildren().get(0);
            }
            if (leftImageView == null)
            {
                Alerts.showAlert("No hay una imagen cargada!");
            }
            else
            {
                ImageSelection selection = new ImageSelection();
                leftImageView.setOnMouseClicked(e -> {
                    System.out.println("Left Coordinates Info: ["+e.getX()+", "+e.getY()+"]");
                    selection.submitClickCoordinates((int)e.getX(), (int) e.getY());

                    if(selection.allCoordinatesSubmitted())
                    {
                        //Calculate 4 points
                        selection.calculateWithAndHeight();

                        //Create new buffered image from those 4 points
                        BufferedImage imageSelection = leftImage.getSubimage(selection.getxOrigin(),selection.getyOrigin(),selection.getWidth(),selection.getHeight());

                        // Display it on the right pane and add it to the list
                        this.displayImageInPane(imageSelection,rightPane);
                        selection.reset();
                    }
                });
            }
        }
        catch(Exception e){
            Alerts.showAlert(e.getMessage());
        }
    }
    @FXML public void createImageWithCircle(){
        int width = 200;
        int height = 200;
        int rad = 50;
        Rectangle r = new Rectangle();
        r.setX(0);
        r.setY(0);
        r.setWidth(width);
        r.setHeight(height);
        r.setFill(Color.BLACK);
        Circle c = new Circle();
        c.setCenterX(width /2);
        c.setCenterY(height /2);
        c.setRadius(rad);
        c.setFill(Color.WHITE);
        leftPane.getChildren().setAll(r,c);
        WritableImage image = leftPane.snapshot(null, null);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        try {
            fc.setTitle("Select File");
            File f = fc.showSaveDialog (browser);
            String fileName = f.getName();
            this.imageUtilities.WriteImage(bImage, f);
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }
        fc.setInitialDirectory(null);
    }
    @FXML public void createImageWithSquare(){
        int width = 200;
        int height = 200;
        Rectangle background = new Rectangle();
        background.setX(0);
        background.setY(0);
        background.setWidth(width);
        background.setHeight(height);
        background.setFill(Color.BLACK);
        Rectangle square = new Rectangle();
        square.setX(50);
        square.setY(50);
        square.setWidth(100);
        square.setHeight(100);
        square.setFill(Color.WHITE);
        leftPane.getChildren().setAll(background,square);
        WritableImage image = leftPane.snapshot(null, null);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        try {
            fc.setTitle("Select File");
            File f = fc.showSaveDialog (browser);
            this.imageUtilities.WriteImage(bImage, f);
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }
        fc.setInitialDirectory(null);
    }
    @FXML public void createGrayScaleImage(){
        BufferedImage grayScaleImage = this.imageUtilities.createGrayScaleImage();
        this.displayImageInPane(grayScaleImage,rightPane);
    }
    @FXML public void createColorScaleImage(){
        BufferedImage colorScaleImage = this.imageUtilities.createColorScaleImage();
        this.displayImageInPane(colorScaleImage,rightPane);
    }
    @FXML public void RGBtoHSV(){
        if(leftPane.getChildren().isEmpty())
        {
            Alerts.showAlert("No hay una imagen cargada!");
        }
        else
        {

            Image image = new Image(leftImage);
            BufferedImage redBufferedImageChannel = image.getRedBufferedImageChannel();
            BufferedImage greenBufferedImageChannel = image.getGreenBufferedImageChannel();
            BufferedImage blueBufferedImageChannel = image.getBlueBufferedImageChannel();
            BufferedImage hueBufferedImageChannel = image.getHueBufferedImageChannel();
            BufferedImage saturationBufferedImageChannel = image.getSaturationBufferedImageChannel();
            BufferedImage valueBufferedImageChannel = image.getValueBufferedImageChannel();

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("separate_rgb_band.fxml"));
                Parent separateBandRoot = (Parent) fxmlLoader.load();
                SeparateBandController separateBandController = fxmlLoader.<SeparateBandController>getController();
                Stage separatedRGBBandStage = new Stage();
                separatedRGBBandStage.setTitle("RGB Bands");
                separatedRGBBandStage.setScene(new Scene(separateBandRoot));
                separateBandController.setLabel("RGB Bands");
                separateBandController.displayImages(redBufferedImageChannel,greenBufferedImageChannel,blueBufferedImageChannel);
                separatedRGBBandStage.show();

                FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("separate_hsv_band.fxml"));
                Parent separateHSVBandRoot = (Parent) fxmlLoader2.load();
                SeparateHSVBandController separatedHSVBandController = fxmlLoader2.<SeparateHSVBandController>getController();
                Stage separatedHSVBandStage = new Stage();
                separatedHSVBandStage.setTitle("HSV Bands");
                separatedHSVBandStage.setScene(new Scene(separateHSVBandRoot));
                separatedHSVBandController.setLabel("HSV Bands");
                separatedHSVBandController.displayImages(hueBufferedImageChannel,saturationBufferedImageChannel,valueBufferedImageChannel);
                separatedHSVBandStage.show();

            }
            catch (Exception e)
            {
                System.out.println("No se puede abrir una nueva ventana");
                e.printStackTrace();
            }
        }
    }
    @FXML public void averagePerBand(){
        try{
            ImageView leftImageView = null;
            if (!leftPane.getChildren().isEmpty())
            {
                leftImageView = (ImageView) leftPane.getChildren().get(0);
                //Si quedo un rectangulo dibujado lo borro
                if(leftPane.getChildren().size() > 1){
                    this.leftPane.getChildren().remove(1);
                }
            }
            if (leftImageView == null)
            {
                Alerts.showAlert("No hay una imagen cargada!");
            }
            else
            {
                ImageSelection selection = new ImageSelection();
                Rectangle selectionLine = new Rectangle();
                leftImageView.setOnMouseClicked(e -> {
                    selection.submitClickCoordinates((int)e.getX(), (int) e.getY());

                    if(selection.allCoordinatesSubmitted())
                    {
                        //Calculate 4 points
                        selection.calculateWithAndHeight();
                        selectionLine.setX(selection.getxOrigin());
                        selectionLine.setY(selection.getyOrigin());
                        selectionLine.setWidth(selection.getWidth());
                        selectionLine.setHeight(selection.getHeight());
                        selectionLine.setFill(Color.TRANSPARENT);
                        selectionLine.setStroke(Color.RED);
                        selectionLine.setStrokeWidth(3);


                        //Create new buffered image from those 4 points
                        BufferedImage imageSelection = leftImage.getSubimage(selection.getxOrigin(),selection.getyOrigin(),selection.getWidth(),selection.getHeight());

                        // Display it on the right pane and add it to the list
                        float[] averages = this.imageUtilities.averagePerBand(imageSelection);

                        int imageSize = selection.getHeight() * selection.getWidth();
                        String message = "Selection Size: " + imageSize + " ---- ";

                        if (averages[0] == averages[1] && averages[0] == averages[2])
                        {
                            message = message + "Grey average: " + averages[0];
                        }
                        else
                        {
                            message = message + "Red average: " + averages[0] + " Green average: " + averages[1] + " Blue average: " + averages[2];
                        }
                        this.setBottomText(message);
                        this.leftPane.getChildren().add(selectionLine);
                        selection.reset();
                    }
                    else {
                        //Removes selection line
                        this.leftPane.getChildren().remove(selectionLine);
                    }
                });
            }
        }
        catch(Exception e){
            Alerts.showAlert(e.getMessage());
        }
    }

    //TP1
    @FXML public BufferedImage imageAddition(){
        BufferedImage temp = leftImage;
        BufferedImage bimg = this.openImageFile();
        leftImage = temp;
        BufferedImage result = imageUtilities.imageAddition(leftImage,bimg);
        this.displayImageInPane(result,rightPane);
        this.displayImageInPane(leftImage, leftPane);
        return result;
    }
    @FXML public BufferedImage imageDifference(){
        BufferedImage temp = leftImage;
        BufferedImage bimg = this.openImageFile();
        leftImage = temp;
        BufferedImage result = imageUtilities.imageDifference(leftImage,bimg);
        this.displayImageInPane(result,rightPane);
        this.displayImageInPane(leftImage, leftPane);
        return result;
    }
    @FXML public BufferedImage imageScalarProduct(){
        int scalar = Integer.valueOf(getInputDialog("Scalar Product", "Enter new Value", "scalar:"));
        BufferedImage result = imageUtilities.imageScalarProduct(leftImage,scalar);
        this.displayImageInPane(result,rightPane);
        return result;
    }
    @FXML public BufferedImage imagePow(){

        double gamma = Double.valueOf(getInputDialog("Gamma Power Function", "Enter a new Value", "Gamma:"));
        BufferedImage result = imageUtilities.gammaPowFunction(leftImage,gamma);
        this.displayImageInPane(result,rightPane);
        return result;
    }
    @FXML public BufferedImage imageNegative(){
        BufferedImage result = null;
        if (leftImage != null) {
            result = imageUtilities.imageNegative(leftImage);
            this.displayImageInPane(result, rightPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen abierta");
        }
        return result;
    }
    @FXML public BufferedImage imageContrast(){
        BufferedImage result = null;
        if (leftImage != null) {
            result = imageUtilities.imageContrast(leftImage);
            this.displayImageInPane(result,rightPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen abierta");
        }
        return result;
    }
    @FXML public BufferedImage imageBinary(){
        BufferedImage result = null;
        if (leftImage != null) {
            int threshold = Integer.valueOf(getInputDialog("Umbralization", "Enter a new Value", "Threshold:"));
            result = imageUtilities.imageBinary(leftImage, (int) threshold);
            this.displayImageInPane(result,rightPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen abierta");
        }
        return result;
    }
    @FXML public void showHistogram(){
        if(leftImage!=null){
            Histogram h = new Histogram();
            h.getImageHistogram(leftImage);
        }
        else{
            Alerts.showAlert("No hay ninguna imagen cargada");
        }

    }
    @FXML public BufferedImage equalizeHistogram(){
        BufferedImage result = null;
        if(leftImage!=null){
            Histogram histogram = new Histogram();
            result = histogram.equalizeHistogram(leftImage);
            this.displayImageInPane(result,rightPane);
        }
        else{
            Alerts.showAlert("No hay ninguna imagen cargada");
        }
        return result;
    }
    @FXML public void gaussianNumberGenerator(){
        double standardDev = Double.valueOf(getInputDialog("Gaussian Number Generator", "Enter a new Value", "Standard Deviation:"));
        double mean = Double.valueOf(getInputDialog("Gaussian Number Generator", "Enter a new Value", "Mean:"));
        String message = "Gaussian Number: ";
        int randomNumber = NumberGenerator.generateRandomGaussianNumber(mean, standardDev);
        message = message + randomNumber;
        this.setBottomText(message);
    }
    @FXML public void rayleighNumberGenerator(){
        double phi = Double.valueOf(getInputDialog("Rayleigh Number Generator", "Enter a new Value", "Phi:"));
        String message = "Rayleigh Number: ";
        int randomNumber = NumberGenerator.generateRandomRayleighNumber(phi);
        message = message + randomNumber;
        this.setBottomText(message);
    }
    @FXML public void exponentialNumberGenerator(){
        double lambda = Double.valueOf(getInputDialog("Exponential Number Generator", "Enter a new Value", "Lambda:"));
        String message = "Exponential Number: ";
        int randomNumber = NumberGenerator.generateRandomExponentialNumber(lambda);
        message = message + randomNumber;
        this.setBottomText(message);
    }
    @FXML public void generateGaussianNoisedImage(){
        double standardDev = Double.valueOf(getInputDialog("Gaussian Noised Image Generator", "Enter a new Value", "Standard Deviation:"));
        double mean = Double.valueOf(getInputDialog("Gaussian Noised Image Generator", "Enter a new Value", "Mean:"));
        BufferedImage noisedImage = this.imageUtilities.generateGaussianNoisedImage(mean,standardDev);
        this.displayImageInPane(noisedImage,rightPane);
    }
    @FXML public void generateRayleighNoisedImage(){
        double phi = Double.valueOf(getInputDialog("Rayleigh Noised Image Generator", "Enter a new Value", "Phi:"));
        BufferedImage noisedImage = this.imageUtilities.generateRayleighNoisedImage(phi);
        this.displayImageInPane(noisedImage,rightPane);
    }
    @FXML public void generateExponentialNoisedImage(){
        double lambda = Double.valueOf(getInputDialog("Exponential Noised Image Generator", "Enter a new Value", "Lambda:"));
        BufferedImage noisedImage = this.imageUtilities.generateExponentialNoisedImage(lambda);
        this.displayImageInPane(noisedImage,rightPane);
    }
    @FXML public void addMultiplicativeExponentialNoise(){
        double lambda = Double.valueOf(getInputDialog("Add Exponential Noise", "Enter a new Value", "Lambda:"));
        int affectedPixelPercentage = Integer.valueOf(getInputDialog("Add Exponential Noise", "Enter a new Value", "Affected Pixel %:"));
        BufferedImage noisedImage = this.imageUtilities.addMultiplicativeExponentialNoise(lambda, affectedPixelPercentage, leftImage);
        this.displayImageInPane(noisedImage,rightPane);
    }
    @FXML public void addMultiplicativeRayleighNoise(){
        double lambda = Double.valueOf(getInputDialog("Add Rayleigh Noise", "Enter a new Value", "Phi:"));
        int affectedPixelPercentage = Integer.valueOf(getInputDialog("Add Rayleigh Noise", "Enter a new Value", "Affected Pixel %:"));
        BufferedImage noisedImage = this.imageUtilities.addMultiplicativeRayleighNoise(lambda, affectedPixelPercentage, leftImage);
        this.displayImageInPane(noisedImage,rightPane);
    }
    @FXML public void addAdditiveGaussianNoise(){
        double standardDev = Double.valueOf(getInputDialog("Add Gaussian Noise", "Enter a new Value", "Standard Deviation:"));
        double mean = Double.valueOf(getInputDialog("Add Gaussian Noise", "Enter a new Value", "Mean:"));
        int affectedPixelPercentage = Integer.valueOf(getInputDialog("Add Gaussian Noise", "Enter a new Value", "Affected Pixel %:"));
        BufferedImage noisedImage = this.imageUtilities.addAdditiveGaussianNoise(mean,standardDev, affectedPixelPercentage, leftImage);
        this.displayImageInPane(noisedImage,rightPane);
    }
    @FXML public void addSaltAndPepperNoise(){
        double p0 = Double.valueOf(getInputDialog("Add Salt & Pepper Noise", "Enter a value between 0 and 1", "p0:"));
        while (p0 < 0 || p0 > 1){
            p0 = Double.valueOf(getInputDialog("Add Salt & Pepper Noise", "Enter a value between 0 and 1", "p0:"));
        }
        double p1 = 1.0 - p0;

        BufferedImage noisedImage = this.imageUtilities.addSaltAndPepperNoise(p0,p1,leftImage);
        this.displayImageInPane(noisedImage,rightPane);
    }
    @FXML public BufferedImage applyMeanFilter(){

        BufferedImage result = null;
        if (leftImage != null) {
            int maskSize = Integer.valueOf(getInputDialog("Apply Mean Filter", "Enter a new Value", "Mask size:"));
            while(isOdd(maskSize)) {
                Alerts.showAlert("El numero no es impar");
                maskSize = Integer.valueOf(getInputDialog("Apply Mean Filter", "Enter a new Value", "Mask size:"));
            }
            result = this.filter.applyMeanFilter(leftImage,maskSize);
            this.displayImageInPane(result, rightPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen abierta");
        }
        return result;
    }
    @FXML public BufferedImage applyMedianFilter(){

        BufferedImage result = null;
        if (leftImage != null) {
            int maskSize = Integer.valueOf(getInputDialog("Apply Median Filter", "Enter a new Value", "Mask size:"));
            while(isOdd(maskSize)) {
                Alerts.showAlert("El numero no es impar");
                maskSize = Integer.valueOf(getInputDialog("Apply Median Filter", "Enter a new Value", "Mask size:"));
            }
            result = this.filter.applyMedianFilter(leftImage,maskSize);
            this.displayImageInPane(result, rightPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen abierta");
        }
        return result;
    }
    @FXML public BufferedImage applyWeightedMedianFilter(){

        BufferedImage result = null;
        if (leftImage != null) {
            result = this.filter.applyWeightedMedianFilter(leftImage);
            this.displayImageInPane(result, rightPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen abierta");
        }
        return result;
    }
    @FXML public BufferedImage applyGaussFilter(){
        BufferedImage result = null;
        if (leftImage != null) {
            double sigma = Double.valueOf(getInputDialog("Apply Gauss Filter", "Enter a new Value", "Sigma:"));
            //Filter f = new Filter();
            result = this.filter.applyGaussFilter(leftImage,sigma);
            this.displayImageInPane(result,rightPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen abierta");
        }
        return result;
    }
    @FXML public void enhanceEdges(){
        int maskSize = Integer.valueOf(getInputDialog("Enhance Edges", "Enter a new Value", "Mask size:"));
        while(isOdd(maskSize)) {
            Alerts.showAlert("El numero no es impar");
            maskSize = Integer.valueOf(getInputDialog("Enhance Edges", "Enter a new Value", "Mask size:"));
        }
        //Filter f = new Filter();
        BufferedImage result = this.filter.enhanceEdges(leftImage,maskSize);
        this.displayImageInPane(result,rightPane);
    }

    //TP2
    @FXML public void prewitt(){
        BufferedImage result = this.filter.applyPrewitt(leftImage);
        this.displayImageInPane(result,rightPane);
    }
    @FXML public void sobel(){
        BufferedImage result = this.filter.applySobel(leftImage);
        this.displayImageInPane(result,rightPane);
    }
    @FXML public void laplace(){
        BufferedImage result = this.filter.applyLaplace(leftImage,false);
        this.displayImageInPane(result,rightPane);
    }
    @FXML public void laplaceAndZeroCrossing() {
        BufferedImage result = this.filter.applyLaplace(leftImage, true);
        this.displayImageInPane(result, rightPane);
    }
    @FXML public void laplaceWithSlopeAndZeroCrossing(){
        double percent = Double.valueOf(getInputDialog("Apply Laplace With Slope", "Enter a new Value", "Percent:"));
        BufferedImage result = this.filter.applyLaplaceWithSlope(leftImage, percent,true);
        this.displayImageInPane(result, rightPane);
    }
    @FXML public void compareDirectionalMasks(){
        if(leftPane.getChildren().isEmpty())
        {
            Alerts.showAlert("No hay una imagen cargada!");
        }
        else{
            BufferedImage horizontalPrewitt = this.filter.applyUnidirectionalPrewitt(leftImage,BorderDetectionDirection.HORIZONTAL);
            BufferedImage verticalPrewitt = this.filter.applyUnidirectionalPrewitt(leftImage,BorderDetectionDirection.VERTICAL);
            BufferedImage diag45Prewitt = this.filter.applyUnidirectionalPrewitt(leftImage,BorderDetectionDirection.DIAGONAL45);
            BufferedImage diag135Prewitt = this.filter.applyUnidirectionalPrewitt(leftImage,BorderDetectionDirection.DIAGONAL135);

            BufferedImage horizontalSobel = this.filter.applyUnidirectionalSobel(leftImage,BorderDetectionDirection.HORIZONTAL);
            BufferedImage verticalSobel = this.filter.applyUnidirectionalSobel(leftImage,BorderDetectionDirection.VERTICAL);
            BufferedImage diag45Sobel = this.filter.applyUnidirectionalSobel(leftImage,BorderDetectionDirection.DIAGONAL45);
            BufferedImage diag135Sobel = this.filter.applyUnidirectionalSobel(leftImage,BorderDetectionDirection.DIAGONAL135);

            BufferedImage horizontalUnnamed = this.filter.applyUnidirectionalUnnamed(leftImage,BorderDetectionDirection.HORIZONTAL);
            BufferedImage verticalUnnamed = this.filter.applyUnidirectionalUnnamed(leftImage,BorderDetectionDirection.VERTICAL);
            BufferedImage diag45Unnamed = this.filter.applyUnidirectionalUnnamed(leftImage,BorderDetectionDirection.DIAGONAL45);
            BufferedImage diag135Unnamed = this.filter.applyUnidirectionalUnnamed(leftImage,BorderDetectionDirection.DIAGONAL135);

            BufferedImage horizontalKirsh = this.filter.applyUnidirectionalKirsh(leftImage,BorderDetectionDirection.HORIZONTAL);
            BufferedImage verticalKirsh = this.filter.applyUnidirectionalKirsh(leftImage,BorderDetectionDirection.VERTICAL);
            BufferedImage diag45Kirsh = this.filter.applyUnidirectionalKirsh(leftImage,BorderDetectionDirection.DIAGONAL45);
            BufferedImage diag135Kirsh = this.filter.applyUnidirectionalKirsh(leftImage,BorderDetectionDirection.DIAGONAL135);

            try {
                FXMLLoader fxmlLoaderPrewitt = new FXMLLoader(getClass().getResource("tp2_5_display_4_border_detection_direction.fxml"));
                Parent prewittRoot = (Parent) fxmlLoaderPrewitt.load();
                BorderDirectionController prewittDirectionController = fxmlLoaderPrewitt.<BorderDirectionController>getController();
                Stage prewittStage = new Stage();
                prewittStage.setTitle("Prewitt");
                prewittStage.setScene(new Scene(prewittRoot));
                prewittDirectionController.displayImages(horizontalPrewitt,verticalPrewitt,diag135Prewitt,diag45Prewitt);
                prewittStage.show();

                FXMLLoader fxmlLoaderSobel = new FXMLLoader(getClass().getResource("tp2_5_display_4_border_detection_direction.fxml"));
                Parent sobelRoot = (Parent) fxmlLoaderSobel.load();
                BorderDirectionController sobelDirectionController = fxmlLoaderSobel.<BorderDirectionController>getController();
                Stage sobelStage = new Stage();
                sobelStage.setTitle("Sobel");
                sobelStage.setScene(new Scene(sobelRoot));
                sobelDirectionController.displayImages(horizontalSobel,verticalSobel,diag135Sobel,diag45Sobel);
                sobelStage.show();


                FXMLLoader fxmlLoaderUnnamed = new FXMLLoader(getClass().getResource("tp2_5_display_4_border_detection_direction.fxml"));
                Parent unnamedRoot = (Parent) fxmlLoaderUnnamed.load();
                BorderDirectionController unnamedDirectionController = fxmlLoaderUnnamed.<BorderDirectionController>getController();
                Stage unnamedStage = new Stage();
                unnamedStage.setTitle("Unnamed");
                unnamedStage.setScene(new Scene(unnamedRoot));
                unnamedDirectionController.displayImages(horizontalUnnamed,verticalUnnamed,diag135Unnamed,diag45Unnamed);
                unnamedStage.show();

                FXMLLoader fxmlLoaderKirsh = new FXMLLoader(getClass().getResource("tp2_5_display_4_border_detection_direction.fxml"));
                Parent kirshRoot = (Parent) fxmlLoaderKirsh.load();
                BorderDirectionController kirshDirectionController = fxmlLoaderKirsh.<BorderDirectionController>getController();
                Stage kirshStage = new Stage();
                kirshStage.setTitle("Kirsh");
                kirshStage.setScene(new Scene(kirshRoot));
                kirshDirectionController.displayImages(horizontalKirsh,verticalKirsh,diag135Kirsh,diag45Kirsh);
                kirshStage.show();

            }
            catch (Exception e)
            {
                System.out.println("No se puede abrir una nueva ventana");
                e.printStackTrace();
            }

        }
    }
    @FXML public void log(){
        double sigma = Double.valueOf(getInputDialog("Apply LoG", "Enter a new Value", "Sigma:"));
        BufferedImage result = this.filter.applyLoG(leftImage,sigma,false);
        this.displayImageInPane(result,rightPane);
    }
    @FXML public void logAndZeroCrossing(){
        double sigma = Double.valueOf(getInputDialog("Apply LoG", "Enter a new Value", "Sigma:"));
        BufferedImage result = this.filter.applyLoG(leftImage,sigma,true);
        this.displayImageInPane(result,rightPane);
    }
    @FXML public void globalThresholding(){
        if(leftPane.getChildren().isEmpty())
        {
            Alerts.showAlert("No hay una imagen cargada!");
        }
        else{
            int delta = Integer.valueOf(getInputDialog("Global Thresholding", "Enter a new Value", "Delta:"));
            BufferedImage bimg = Threshold.applyGlobalThresholding(leftImage, delta, this);
            this.displayImageInPane(bimg,rightPane);
        }
    }
    @FXML public void Otsu(){
        if(leftPane.getChildren().isEmpty())
        {
            Alerts.showAlert("No hay una imagen cargada!");
        }
        else{
            BufferedImage bimg = Threshold.appyOtsu(leftImage,this);
            this.displayImageInPane(bimg,rightPane);
        }
    }
    @FXML public void isotropicDifussion(){
        int iterations = Integer.valueOf(getInputDialog("Isotropic Difussion", "Enter a new Value", "Iterations:"));
        BufferedImage bimg = filter.applyIsotropicDifusion(leftImage, iterations);
        this.displayImageInPane(bimg,rightPane);
    }
    @FXML public void anisotropicDifussion(){
        int iterations = Integer.valueOf(getInputDialog("Isotropic Difussion", "Enter a new Value", "Iterations:"));
        double sigma = Double.valueOf(getInputDialog("Isotropic Difussion", "Enter a new Value", "Sigma:"));
        BufferedImage bimg = filter.applyAnisotropicDifusion(leftImage, iterations, sigma);
        this.displayImageInPane(bimg,rightPane);
    }
    @FXML public void bilateralFilter(){
        if (leftImage != null) {
            double sigma_s = Double.valueOf(getInputDialog("Bilateral Filter", "Enter a new Value", "Sigma S:"));
            double sigma_r = Double.valueOf(getInputDialog("Bilateral Filter", "Enter a new Value", "Sigma R:"));
            BufferedImage bimg = filter.applyBilateralFilter(leftImage,sigma_r,sigma_s);
            this.displayImageInPane(bimg,rightPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen abierta");
        }
    }

    // TP 3

    @FXML public void canny(){
        BufferedImage result = null;
        if (leftImage != null) {
            double sigma = Double.valueOf(getInputDialog("Canny", "Enter a new Value", "Sigma for Gauss:"));
            int t1 = Integer.valueOf(getInputDialog("Canny", "Enter a new Value", "T1:"));
            int t2 = Integer.valueOf(getInputDialog("Canny", "Enter a new Value", "T2:"));

            result = this.filter.applyCanny(leftImage,sigma,t1,t2);
            this.displayImageInPane(result,rightPane);
        }
        else
        {
            Alerts.showAlert("No hay una imagen abierta");
        }
    }

    @FXML public void getHoughLines(){
        double angle = Double.valueOf(getInputDialog("Hough", "Enter a new Value", "Detection Percent:"));
        Hough h = new Hough((360),400);
        this.displayImageInPane(h.findLines(leftImage, angle, Hough.GeometricType.LINE),rightPane);
    }

    @FXML public void getHoughCircles(){
        double angle = Double.valueOf(getInputDialog("Hough", "Enter a new Value", "Detection Percent:"));
        Hough h = new Hough(leftImage);
        this.displayImageInPane(h.findLines(leftImage, angle, Hough.GeometricType.CIRCLE),rightPane);
    }

    @FXML public void drawLine(){
        double angle = Double.valueOf(getInputDialog("Draw Test Line", "Enter a new Value", "Angle:"));
        double radius = Double.valueOf(getInputDialog("Draw Test Line", "Enter a new Value", "Radius:"));
        Hough h = new Hough( 10,10);
        this.displayImageInPane(h.drawLine(leftImage,angle,radius),rightPane);
        this.setBottomText("Angle: " + angle + " Radius: " + radius);
    }

    @FXML public void drawCircle(){
        int x = Integer.valueOf(getInputDialog("Draw Test Circle", "Enter a new Value", "x Center:"));
        int y = Integer.valueOf(getInputDialog("Draw Test Circle", "Enter a new Value", "y Center:"));
        double radiusCircle = Double.valueOf(getInputDialog("Draw Test Circle", "Enter a new Value", "Radius Circle:"));
        Hough h = new Hough( 10,10);
        this.displayImageInPane(h.drawCircle(leftImage,x,y, radiusCircle),rightPane);
        //this.setBottomText("X: " + x + " Y: " + y + " Radius: " + radius);
    }

    //Panels

    public void displayImageInPane(BufferedImage bimg, AnchorPane pane){
        if (pane == rightPane) {
            rightPaneImageList.add(bimg);
        }
        WritableImage wimg = imageUtilities.readImage(bimg);
        ImageView image = new ImageView(wimg);
        pane.getChildren().setAll(image);
    }

    //General functions

    public void setBottomText(String string)
    {
        this.txtBottom.setText("   " + string);
    }
    private BufferedImage getLastModifiedImage(){
        BufferedImage lastModifiedImage = null;
        if (rightPaneImageList.size() > 0)
        {
            lastModifiedImage = rightPaneImageList.get(rightPaneImageList.size() - 1);
        }
        return lastModifiedImage;

    }
    private boolean isOdd(int number){
        return number % 2 == 0;
    }

    //Tests

    public void test()throws IOException{
        BufferedImage bimg = ImageIO.read(new File("C:\\Users\\Fernando.Ares\\Desktop\\Imagenes\\circleSmall.png"));
        leftImage = bimg;
        this.displayImageInPane(bimg,leftPane);
    }
}
