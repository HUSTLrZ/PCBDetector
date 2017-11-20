package com.lrz;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author HustLrz
 * @Date Created in 11:47 2017/11/17
 */
public class CapLocate {

    public List<Mat> capLocate(Mat src) {
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(src, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        for (int i = 0; i < contours.size(); i++) {
            int S = (int) Imgproc.contourArea(contours.get(i));
            if (S < 15000 || S > 23000) {
                continue;
            }
            MatOfPoint2f mtx = new MatOfPoint2f(contours.get(i).toArray());
            int L = (int) Imgproc.arcLength(mtx, true);
            double roundness = 4 * Math.PI * S / (L * L);
            System.out.println(S + " " + L + " " + roundness);
            Point center = new Point();
            float[] radius = new float[1];
            Imgproc.minEnclosingCircle(mtx, center, radius);
            Imgproc.circle(src, center, (int) radius[0] + 20, new Scalar(255, 0, 255, 255));
        }
        Imgcodecs.imwrite("res/img/capacity/img.jpg", src);
        return null;
    }
}
