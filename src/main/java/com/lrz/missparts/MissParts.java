package com.lrz.missparts;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author HustLrz
 * @Date Created in 15:03 2017/11/28
 */
public class MissParts {

    public void match() {
        Mat objectImage = Imgcodecs.imread("res/img/missparts/missparts_std.jpg");
        Mat sceneImage = Imgcodecs.imread("res/img/missparts/missparts_test.jpg");

        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.AKAZE);
        System.out.println("Detecting key points...");
        featureDetector.detect(objectImage, objectKeyPoints);
        KeyPoint[] keypoints = objectKeyPoints.toArray();

        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.AKAZE);
        System.out.println("Computing descriptors...");
        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);

        Mat outputImage = new Mat(objectImage.rows(), objectImage.cols(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Scalar newKeypointColor = new Scalar(255, 0, 0);

        System.out.println("Drawing key points on object image...");
        Features2d.drawKeypoints(objectImage, objectKeyPoints, outputImage, newKeypointColor, 0);

        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
        System.out.println("Detecting key points in background image...");
        featureDetector.detect(sceneImage, sceneKeyPoints);
        System.out.println("Computing descriptors in background image...");
        descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);

        Mat matchoutput = new Mat(sceneImage.rows() * 2, sceneImage.cols() * 2, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Scalar matchestColor = new Scalar(255, 0, 0);

        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        System.out.println("Matching object and scene images...");
        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);

        System.out.println("Calculating good match list...");
        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();

        float nndrRatio = 0.4f;

        for (int i = 0; i < matches.size(); i++) {
            MatOfDMatch matofDMatch = matches.get(i);
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];

            if (m1.distance <= m2.distance * nndrRatio) {
                goodMatchesList.addLast(m1);

            }
        }

        if (goodMatchesList.size() >= 7) {
            System.out.println("Object Found!!!");

            List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
            List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();

            LinkedList<Point> objectPoints = new LinkedList<Point>();
            LinkedList<Point> scenePoints = new LinkedList<Point>();

            for (int i = 0; i < goodMatchesList.size(); i++) {
                Point point1 = objKeypointlist.get(goodMatchesList.get(i).queryIdx).pt;
                Point point2 = scnKeypointlist.get(goodMatchesList.get(i).trainIdx).pt;
                double distance = (point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y);
                System.out.println(distance);
                if (distance > 100) {
                    continue;
                }
                System.out.println(point1.x + " " + point1.y + "   " + point2.x + " " + point2.y + "   " + distance);
                objectPoints.addLast(point1);
                scenePoints.addLast(point2);
            }

            MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
            objMatOfPoint2f.fromList(objectPoints);
            MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
            scnMatOfPoint2f.fromList(scenePoints);


            Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);

            Mat result = new Mat(objectImage.rows(), objectImage.cols(), objectImage.type());
            System.out.println("Transforming object to scene...");
            Imgproc.warpPerspective(objectImage, result, homography, new Size(sceneImage.cols(), sceneImage.rows()));

            System.out.println("Drawing matches image...");
            MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(goodMatchesList);

            Features2d.drawMatches(objectImage, objectKeyPoints, sceneImage, sceneKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 2);
            Imgcodecs.imwrite("res/img/missparts/outputImage.jpg", outputImage);
            Imgcodecs.imwrite("res/img/missparts/matchoutput.jpg", matchoutput);
            Imgcodecs.imwrite("res/img/missparts/result.jpg", result);

        }
    }

    public void subtract() {
        Mat src1 = Imgcodecs.imread("res/img/missparts/result.jpg", CvType.CV_8UC1);
        Mat src2 = Imgcodecs.imread("res/img/missparts/missparts_test.jpg", CvType.CV_8UC1);

        Imgproc.GaussianBlur(src1, src1, new Size(5, 5), 0, 0, 4);
        Imgproc.GaussianBlur(src2, src2, new Size(5, 5), 0, 0, 4);

        Mat dst = new Mat();
        Core.subtract(src1, src2, dst);

        Imgcodecs.imwrite("res/img/missparts/subtract_result.jpg", dst);
    }
}
