package com.lrz;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author HustLrz   (not finished)
 * @Date Created in 8:24 2017/11/8
 */
public class ResIdentify {

    private static final String PATH = "res/img/identify/";
    private static int threshold = 170;   //二值化阈值，根据具体情况调整
    private static int thresholdType = 8; //二值化类型
    private static int erodeSizeX = 3;
    private static int erodeSizeY = 60;   //纵向腐蚀

    //
    private static Scalar[] colorCode = {
    };


    public int resIdentify(Mat src) {

        //去掉边缘，取中间
        src = src.submat(src.rows() / 4, 3 * src.rows() / 4, src.cols() / 6, 5 * src.cols() / 6);

        // 高斯模糊
        Mat src_blur = new Mat();
        Imgproc.GaussianBlur(src, src_blur, new Size(5, 5), 0, 0, 4);

        // 灰度化
        Mat src_gray = new Mat();
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);

        // 二值化
        Mat img_threshold = new Mat();
        Imgproc.threshold(src_gray, img_threshold, threshold, 255, thresholdType);

        // 纵向腐蚀，连接色环反光断点
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erodeSizeX, erodeSizeY));
        Imgproc.erode(img_threshold, img_threshold, element, new Point(-1, -1), 1);

        //反色
        Core.bitwise_not(img_threshold, img_threshold);

        // 求所有轮廓
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(img_threshold, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        // 画出轮廓
        Imgproc.drawContours(img_threshold, contours, -1, new Scalar(255, 0, 255, 255));

        // 画出轮廓的最小外接矩形
        List<RotatedRect> rects = new ArrayList<RotatedRect>();
        for (int i = 0; i < contours.size(); i++) {
            System.out.println(Imgproc.contourArea(contours.get(i)));
            MatOfPoint2f mtx = new MatOfPoint2f(contours.get(i).toArray());
            RotatedRect mr = Imgproc.minAreaRect(mtx);
            rects.add(mr);
        }

        for (int i = 0; i < contours.size(); i++) {
            RotatedRect minRect = rects.get(i);
            Point[] rect_points = new Point[4];
            minRect.points(rect_points);

            // 描边
            for (int j = 0; j < 4; j++) {
                Point pt1 = new Point(rect_points[j].x, rect_points[j].y);
                Point pt2 = new Point(rect_points[(j + 1) % 4].x, rect_points[(j + 1) % 4].y);

                Imgproc.line(img_threshold, pt1, pt2, new Scalar(255, 0, 255, 255), 4, 8, 0);
            }

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

            //色环图像
            Mat colorBandMat = showResultMat(img_rotated, size, minRect.center, i);
        }
        return 0;
    }

    private Mat showResultMat(Mat src, Size rect_size, Point center, int index) {
        Mat img_crop = new Mat();
        Imgproc.getRectSubPix(src, rect_size, center, img_crop);
        Imgcodecs.imwrite(PATH + "debug_crop_" + index + ".jpg", img_crop);
        return img_crop;
    }
}
