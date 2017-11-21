package com.lrz.capacity;

import com.lrz.TemplateMatch;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 检测电容极性是否错误
 * not finished
 * @Author HustLrz
 * @Date Created in 8:51 2017/11/21
 */
public class PolarDetect {

    private static String templatePath = "G:/template.jpg";

    public void detect(Mat src){
        Mat template = Imgcodecs.imread(templatePath);
        Point markPoint = TemplateMatch.match(src,template);

    }

    private Point findCircleCenter(Mat src){
        Mat src_gray = new Mat();
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
        Mat circles = new Mat();
        Imgproc.HoughCircles(src_gray, circles, Imgproc.CV_HOUGH_GRADIENT, 1.5, 20);
        return null;
    }
}
