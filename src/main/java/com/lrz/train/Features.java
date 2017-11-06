package com.lrz.train;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author HustLrz
 * @Date Created in 15:12 2017/11/6
 */
public class Features implements SVMCallback {

    public Mat getHistogramFeatures(Mat in) {
        Mat out = new Mat(in.size(), in.type());
        if (in.channels() == 3) {
            Mat hsv = new Mat();
            List<Mat> hsvSplit = new ArrayList<Mat>();
            Imgproc.cvtColor(in, hsv, Imgproc.COLOR_BGR2HSV);
            Core.split(hsv, hsvSplit);
            Imgproc.equalizeHist(hsvSplit.get(2), hsvSplit.get(2));
            Core.merge(hsvSplit, hsv);
            Imgproc.cvtColor(hsv, out, Imgproc.COLOR_HSV2BGR);
            hsv = null;
            hsvSplit = null;
            System.gc();
        } else if (in.channels() == 1) {
            Imgproc.equalizeHist(in, out);
        }
        return out;
    }
}
