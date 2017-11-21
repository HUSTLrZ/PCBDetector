package com.lrz;

import com.lrz.capacity.PolarDetect;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class App {
    static {
        //加载opencv动态链接库
        String path = "G:/opencv/build/java/x64/opencv_java320.dll";
        System.load(path);
    }

    public static void main(String[] args) {
        Mat src = Imgcodecs.imread("res/img/capacity/debug_crop_21.jpg"); //读取原始电路板图片
//        ResLocate resLocate = new ResLocate();
//        resLocate.resLocate(src);
//        ResIdentify resIdentify = new ResIdentify();
//        resIdentify.resIdentify(src);
//        SVMTrain svm = new SVMTrain();
//        svm.svmTrain(true, false);
//
//        CapLocate capLocate = new CapLocate();
//        capLocate.capLocate(src);
        PolarDetect polarDetect = new PolarDetect();
        polarDetect.detect(src);
    }

    public static void process() {
        Mat src = Imgcodecs.imread("res/img/capacity/debug_crop_18.jpg");
        Mat src_blur = new Mat();
        Mat src_gray = new Mat();

        //高斯模糊
        Imgproc.GaussianBlur(src, src_blur, new Size(5, 5), 0, 0, 4);

        //灰度化
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);

        //二值化
        Mat img_threshold = new Mat();
        Imgproc.threshold(src_gray, img_threshold, 0, 255, Imgproc.THRESH_OTSU);

        Imgcodecs.imwrite("res/img/capacity/test/threshold.jpg", img_threshold);

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20));
        Imgproc.morphologyEx(img_threshold, img_threshold, Imgproc.MORPH_OPEN, element);

        Imgcodecs.imwrite("res/img/capacity/test/open.jpg", img_threshold);
    }

}
