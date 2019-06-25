package sample;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Sift {

    public static boolean objectInScene = false;
    // Compulsory
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static BufferedImage applySift(File objectFile, File sceneFile){

        String bookObject = objectFile.getAbsolutePath();
        String bookScene = sceneFile.getAbsolutePath();

        System.out.println("Started....");
        System.out.println("Loading images...");
        Mat objectImage = Highgui.imread(bookObject, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat sceneImage = Highgui.imread(bookScene, Highgui.CV_LOAD_IMAGE_COLOR);

        //Matriz para guardar Keypoints
        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        //Usa un Feature Detector para extraer keypoints
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
        System.out.print("Detectando Keypoints en imagen de objeto");
        featureDetector.detect(objectImage, objectKeyPoints);
        KeyPoint[] keypoints = objectKeyPoints.toArray();
        System.out.println(" : " + keypoints.length);

        //Matriz para guardar Descriptores >> Descriptor: punto que caracteriza a una imagen
        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
        //Usa un descriptor extractor para extraer descriptores
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        System.out.println("Calculando descriptores de imagen de objeto");
        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);

        // Matriz para imagen de salida
        Mat outputImage = new Mat(objectImage.rows(), objectImage.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
        Scalar newKeypointColor = new Scalar(255, 0, 0);

        System.out.println("Drawing key points on object image...");
        //Dibuja los keypoints de la imagen objeto
        Features2d.drawKeypoints(objectImage, objectKeyPoints, outputImage, newKeypointColor, 0);

        // Matchea el objeto con la imagen donde esta el objeto
        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
        System.out.print("Detectando keypoints en imagen secundaria");
        featureDetector.detect(sceneImage, sceneKeyPoints);
        System.out.println(" : " + (int) sceneKeyPoints.size().height);
        System.out.println("Detectando descriptores en imagen secundaria");
        descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);

        Mat matchoutput = new Mat(sceneImage.rows() * 2, sceneImage.cols() * 2, Highgui.CV_LOAD_IMAGE_COLOR);
        Scalar matchestColor = new Scalar(0, 255, 210);

        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        System.out.println("Matching object and scene images...");
        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);

        System.out.println("Calculating good match list...");
        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();

        float nndrRatio = 0.7f; // Parametro para distancia euclidia entre descriptores

        for (int i = 0; i < matches.size(); i++) {
            MatOfDMatch matofDMatch = matches.get(i);
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];

            //Calculo de distancia euclidia
            if (m1.distance <= m2.distance * nndrRatio) {
                goodMatchesList.addLast(m1);
            }
        }

        System.out.println("Cantidad de Matcheos: " + goodMatchesList.size());
        if (goodMatchesList.size() >= 7) {
            System.out.println("Object Found!!!");

            if(goodMatchesList.size() >= keypoints.length * 0.7){
                objectInScene = true;
            }

            List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
            List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();

            LinkedList<Point> objectPoints = new LinkedList<>();
            LinkedList<Point> scenePoints = new LinkedList<>();

            for (int i = 0; i < goodMatchesList.size(); i++) {
                objectPoints.addLast(objKeypointlist.get(goodMatchesList.get(i).queryIdx).pt);
                scenePoints.addLast(scnKeypointlist.get(goodMatchesList.get(i).trainIdx).pt);
            }

            MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
            objMatOfPoint2f.fromList(objectPoints);
            MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
            scnMatOfPoint2f.fromList(scenePoints);

            Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);

            Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
            Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

            obj_corners.put(0, 0, new double[]{0, 0});
            obj_corners.put(1, 0, new double[]{objectImage.cols(), 0});
            obj_corners.put(2, 0, new double[]{objectImage.cols(), objectImage.rows()});
            obj_corners.put(3, 0, new double[]{0, objectImage.rows()});

            System.out.println("Transforming object corners to scene corners...");
            Core.perspectiveTransform(obj_corners, scene_corners, homography);

            Mat img = Highgui.imread(bookScene, Highgui.CV_LOAD_IMAGE_COLOR);

            //Aca dibuja las lineas del objeto reconocido
            Core.line(img, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
            Core.line(img, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
            Core.line(img, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
            Core.line(img, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);

            System.out.println("Drawing matches image...");
            MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(goodMatchesList);

            //Dibuja las lineas que matchean entre ambas imagenes
            Features2d.drawMatches(objectImage, objectKeyPoints, sceneImage, sceneKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 2);

            try {
                //Aca devuelve el resultado
                return Mat2BufferedImage(matchoutput);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Object Not Found");
        }
        System.out.println("Ended....");
        return null;
    }

    static BufferedImage Mat2BufferedImage(Mat matrix)throws Exception {
        MatOfByte mob=new MatOfByte();
        Highgui.imencode(".jpg", matrix, mob);
        byte ba[]=mob.toArray();

        BufferedImage bi= ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }
}