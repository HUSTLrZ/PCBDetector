package com.lrz.resistor;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class ResLocate {

    private static final String PATH = "res/img/";
    private static int threshold = 0;           //二值化阈值
    private static int morphOpenSizeX = 20;     //开操作size
    private static int morphOpenSizeY = 20;     //开操作size
    private static int morphDilateSizeX = 17;   //膨胀size
    private static int morphDilateSizeY = 17;   //膨胀size
    private static int verifyArea = 8000;       //符合要求的矩形块最小面积


    /**
     * 经过高斯模糊、灰度化、二值化、开操作、膨胀等预处理
     * 获取所有轮廓，对轮廓尺寸进行验证，得到需要的轮廓，描出矩形，剪取小图
     *
     * @param src
     * @return
     */
    public List<Mat> resLocate(Mat src) {
        List<Mat> resultList = new ArrayList<Mat>();

        Mat src_blur = new Mat();
        Mat src_gray = new Mat();

        //高斯模糊
        Imgproc.GaussianBlur(src, src_blur, new Size(5, 5), 0, 0, 4);

        Imgcodecs.imwrite(PATH + "src_blur.jpg", src_blur);

        //灰度化
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);

        Imgcodecs.imwrite(PATH + "src_gray.jpg", src_gray);

        //二值化
        Mat img_threshold = new Mat();
        Imgproc.threshold(src_gray, img_threshold, threshold, 255, Imgproc.THRESH_OTSU);

        Imgcodecs.imwrite(PATH + "img_threshold.jpg", img_threshold);

        //开操作消掉部分白色线条和白色斑点
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(morphOpenSizeX, morphOpenSizeY));
        Imgproc.morphologyEx(img_threshold, img_threshold, Imgproc.MORPH_OPEN, element);

        Imgcodecs.imwrite(PATH + "morphology_open.jpg", img_threshold);

        //膨胀消除电阻间隔
        element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(morphDilateSizeX, morphDilateSizeY));
        Imgproc.morphologyEx(img_threshold, img_threshold, Imgproc.MORPH_DILATE, element);

        Imgcodecs.imwrite(PATH + "morphology_dilate.jpg", img_threshold);

        //求所有轮廓
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(img_threshold, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        //画出轮廓
        Mat result = new Mat();
        src.copyTo(result);
        Imgproc.drawContours(img_threshold, contours, -1, new Scalar(255, 0, 255, 255));
        Imgcodecs.imwrite(PATH + "draw_Contours.jpg", img_threshold);

        //画出轮廓的最小外接矩形，并排除不符合要求的矩形
        List<RotatedRect> rects = new ArrayList<RotatedRect>();
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f mtx = new MatOfPoint2f(contours.get(i).toArray());
            RotatedRect mr = Imgproc.minAreaRect(mtx);
            if (verifySize(mr))
                rects.add(mr);
        }

        for (int i = 0; i < rects.size(); i++) {
            RotatedRect minRect = rects.get(i);
            Point[] rect_points = new Point[4];
            minRect.points(rect_points);

            //描边
            for (int j = 0; j < 4; j++) {
                Point pt1 = new Point(rect_points[j].x, rect_points[j].y);
                Point pt2 = new Point(rect_points[(j + 1) % 4].x, rect_points[(j + 1) % 4].y);

                Imgproc.line(img_threshold, pt1, pt2, new Scalar(255, 0, 255, 255), 4, 8, 0);
            }

            //旋转垂直方向的电阻
            double r = minRect.size.width / minRect.size.height;
            double angle = minRect.angle;
            Size size = new Size(minRect.size.width, minRect.size.height);
            if (r < 1) {
                angle = angle + 90;
                size = new Size(minRect.size.height, minRect.size.width);
            }
            Mat rotMat = Imgproc.getRotationMatrix2D(minRect.center, angle, 1);
            Mat img_rotated = new Mat();
            Imgproc.warpAffine(src, img_rotated, rotMat, src.size());
            Mat resultMat = showResultMat(img_rotated, size, minRect.center, i);
            resultList.add(resultMat);

        }

        Imgcodecs.imwrite(PATH + "result.jpg", img_threshold);

        return resultList;
    }

    /**
     * 验证矩形尺寸是否符合要求
     *
     * @param mr
     * @return
     */
    public boolean verifySize(RotatedRect mr) {
        int area = (int) (mr.size.height * mr.size.width);
        int ratio;
        if (mr.size.height > mr.size.width) {
            ratio = (int) (mr.size.height / mr.size.width);
        } else {
            ratio = (int) (mr.size.width / mr.size.height);
        }
        if (ratio >= 2 && ratio <= 3 && area >= verifyArea)
            return true;
        return false;
    }

    /**
     * 获取疑似电阻的小图
     *
     * @param src
     * @param rect_size
     * @param center
     * @param index
     * @return
     */
    private Mat showResultMat(Mat src, Size rect_size, Point center, int index) {
        Mat img_crop = new Mat();
        Imgproc.getRectSubPix(src, rect_size, center, img_crop);
        Imgcodecs.imwrite(PATH + "debug_crop_" + index + ".jpg", img_crop);
        return img_crop;
    }
}
