package com.lrz.capacity;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过面积和圆度来定位电容
 *
 * @Author HustLrz
 * @Date Created in 11:47 2017/11/17
 */
public class CapLocate {

    private static final String PATH = "res/img/capacity/";
    private static int threshold = 0;           //二值化阈值
    private static int morphOpenSizeX = 20;     //开操作size
    private static int morphOpenSizeY = 20;     //开操作size
    private static int morphDilateSizeX = 17;   //膨胀size
    private static int morphDilateSizeY = 17;   //膨胀size

    public List<Mat> capLocate(Mat src) {
        List<Mat> resultList = new ArrayList<Mat>();
        Mat src_blur = new Mat();
        Mat src_gray = new Mat();

        //高斯模糊
        Imgproc.GaussianBlur(src, src_blur, new Size(5, 5), 0, 0, 4);

        //灰度化
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);

        //二值化
        Mat img_threshold = new Mat();
        Imgproc.threshold(src_gray, img_threshold, threshold, 255, Imgproc.THRESH_OTSU);

        //开操作消掉部分白色线条和白色斑点
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(morphOpenSizeX, morphOpenSizeY));
        Imgproc.morphologyEx(img_threshold, img_threshold, Imgproc.MORPH_OPEN, element);

        //膨胀消除电阻间隔
        element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(morphDilateSizeX, morphDilateSizeY));
        Imgproc.morphologyEx(img_threshold, img_threshold, Imgproc.MORPH_DILATE, element);


        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(img_threshold, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        //通过圆度和面积两项指标来获取电容
        for (int i = 0; i < contours.size(); i++) {
            int S = (int) Imgproc.contourArea(contours.get(i));
            if (S < 15000 || S > 23000) {
                continue;
            }
            MatOfPoint2f mtx = new MatOfPoint2f(contours.get(i).toArray());
            int L = (int) Imgproc.arcLength(mtx, true);

            //平滑边缘曲线
            double epsilon = 0.01 * L;
            MatOfPoint2f result = new MatOfPoint2f();
            Imgproc.approxPolyDP(mtx, result, epsilon, true);

            //重新计算周长
            L = (int) Imgproc.arcLength(result, true);

            //圆度
            double roundness = 4 * Math.PI * S / (L * L);
            if (roundness < 0.7) {
                continue;
            }
            System.out.println(S + " " + L + " " + roundness);
            RotatedRect minRect = Imgproc.minAreaRect(mtx);
            double r = minRect.size.width / minRect.size.height;
            double angle = minRect.angle;
            Size size = new Size(minRect.size.width + 250, minRect.size.height + 100);
            if (r < 1) {
                angle = angle + 90;
                size = new Size(minRect.size.height + 250, minRect.size.width + 100);
            }
            Mat rotMat = Imgproc.getRotationMatrix2D(minRect.center, angle, 1);
            Mat img_rotated = new Mat();
            Imgproc.warpAffine(src, img_rotated, rotMat, src.size());
            Mat resultMat = showResultMat(img_rotated, size, minRect.center, i);
            resultList.add(resultMat);
        }

        return resultList;
    }

    private Mat showResultMat(Mat src, Size rect_size, Point center, int index) {
        Mat img_crop = new Mat();
        Imgproc.getRectSubPix(src, rect_size, center, img_crop);
        Imgcodecs.imwrite(PATH + "debug_crop_" + index + ".jpg", img_crop);
        return img_crop;
    }
}
