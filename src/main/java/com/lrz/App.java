package com.lrz;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Hello world!
 */
public class App {
    static {
        //加载opencv动态链接库
        String path = "G:/opencv/build/java/x64/opencv_java320.dll";
        System.load(path);
    }

    public static void main(String[] args) {
        ResLocate resLocate = new ResLocate();
        Mat src = Imgcodecs.imread("G:/123.png"); //读取原始电路板图片
        resLocate.resLocate(src);
    }
}
