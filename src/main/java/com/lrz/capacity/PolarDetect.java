package com.lrz.capacity;

import com.lrz.util.TemplateMatch;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

/**
 * 检测电容极性是否错误,输出角度
 *
 * @Author HustLrz
 * @Date Created in 8:51 2017/11/21
 */
public class PolarDetect {

    private static String templatePath = "res/img/capacity/template.jpg";  //加号模板
    private Point circle_center;   //圆心
    private Point gravity_center;   //质心

    /**
     * 通过求圆心-质心与圆心-加号的角度来检测极性与偏移
     * @param src
     */
    public void detect(Mat src) {
        Mat template = Imgcodecs.imread(templatePath);
        Point markPoint = TemplateMatch.match(src, template);
        findCenter(src);
        System.out.println("markpoint: " + markPoint.x + " " + markPoint.y);
        System.out.println("circle: " + circle_center.x + " " + circle_center.y);
        System.out.println("gravity: " + gravity_center.x + " " + gravity_center.y);
        double tan1 = (gravity_center.y - circle_center.y) / (circle_center.x - gravity_center.x);
        double angle1 = Math.atan(tan1) / Math.PI * 180;
        System.out.println(tan1 + " " + angle1);
        double tan2 = (markPoint.y - circle_center.y) / (circle_center.x - markPoint.x);
        double angle2 = Math.atan(tan2) / Math.PI * 180;
        System.out.println(tan2 + " " + angle2);

        double angle = 180 + angle1 - angle2;
        if (angle > 180) {
            angle = 360 - angle;
        }
        System.out.println(angle);

        Imgproc.circle(src, markPoint, 1, new Scalar(0, 0, 255));
        Imgproc.circle(src, gravity_center, 1, new Scalar(0, 0, 255));
        Imgproc.circle(src, circle_center, 1, new Scalar(0, 0, 255));

        Imgcodecs.imwrite("res/img/capacity/result.jpg", src);
    }

    private void findCenter(Mat src) {
        Mat src_blur = new Mat();
        Mat src_gray = new Mat();

        //高斯模糊
        Imgproc.GaussianBlur(src, src_blur, new Size(5, 5), 0, 0, 4);

        //灰度化
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);

        //二值化
        Mat img_threshold = new Mat();
        Imgproc.threshold(src_gray, img_threshold, 0, 255, Imgproc.THRESH_OTSU);

        //开操作
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20));
        Imgproc.morphologyEx(img_threshold, img_threshold, Imgproc.MORPH_OPEN, element);

        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(img_threshold, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        //求最大轮廓
        double maxArea = 0;
        int index = -1;
        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > maxArea) {
                index = i;
                maxArea = area;
            }
        }

        //最大轮廓质心
        Moments m = Imgproc.moments(contours.get(index));
        int x = (int) (m.get_m10() / m.get_m00());
        int y = (int) (m.get_m01() / m.get_m00());
        gravity_center = new Point(x, y);

        //最大内切圆圆心
        MatOfPoint2f mtx = new MatOfPoint2f(contours.get(index).toArray());
        double dist = 0;
        double maxDist = 0;
        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {
                dist = Imgproc.pointPolygonTest(mtx, new Point(i, j), true);
                if (dist > maxDist) {
                    maxDist = dist;
                    circle_center = new Point(i, j);
                }
            }
        }
    }
}
