package com.lrz;

import com.lrz.train.SVMTrain;


public class App {
    static {
        //加载opencv动态链接库
        String path = "G:/opencv/build/java/x64/opencv_java320.dll";
        System.load(path);
    }

    public static void main(String[] args) {
//        Mat src = Imgcodecs.imread("res/img/debug_crop_2.jpg"); //读取原始电路板图片
//        ResLocate resLocate = new ResLocate();
//        resLocate.resLocate(src);
//        ResIdentify resIdentify = new ResIdentify();
//        resIdentify.resIdentify(src);
        SVMTrain svm = new SVMTrain();
        svm.svmTrain(true, false);
    }
}
