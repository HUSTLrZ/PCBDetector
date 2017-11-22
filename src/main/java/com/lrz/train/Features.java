package com.lrz.train;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


/**
 * @Author HustLrz
 * @Date Created in 15:12 2017/11/6
 */
public class Features implements SVMCallback {

    enum Direction {
        VERTICAL, HORIZONTAL
    }

    public Mat getHistogramFeatures(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        Mat img_threshold = new Mat();
        Imgproc.threshold(grayImage, img_threshold, 0, 255, Imgproc.THRESH_OTSU);

        return features(img_threshold, 0);
    }

    /**
     * 获取垂直或水平方向直方图
     *
     * @param img
     * @param direction
     * @return
     */
    private float[] projectedHistogram(Mat img, Direction direction) {
        int sz = 0;
        switch (direction) {
            case HORIZONTAL:
                sz = img.rows();
                break;

            case VERTICAL:
                sz = img.cols();
                break;

            default:
                break;
        }

        // 统计这一行或一列中，非零元素的个数，并保存到nonZeroMat中
        float[] nonZeroMat = new float[sz];
        Core.extractChannel(img, img, 0);
        for (int j = 0; j < sz; j++) {
            Mat data = (direction == Direction.HORIZONTAL) ? img.row(j) : img.col(j);
            int count = Core.countNonZero(data);
            nonZeroMat[j] = count;
        }

        // Normalize histogram
        float max = 0;
        for (int j = 0; j < nonZeroMat.length; ++j) {
            max = Math.max(max, nonZeroMat[j]);
        }

        if (max > 0) {
            for (int j = 0; j < nonZeroMat.length; ++j) {
                nonZeroMat[j] /= max;
            }
        }

        return nonZeroMat;
    }

    /**
     * Assign values to feature
     * <p>
     * 样本特征为水平、垂直直方图和低分辨率图像所组成的矢量
     *
     * @param in
     * @param sizeData 低分辨率图像size = sizeData*sizeData, 可以为0
     * @return
     */
    private Mat features(Mat in, int sizeData) {
        float[] vhist = projectedHistogram(in, Direction.VERTICAL);
        float[] hhist = projectedHistogram(in, Direction.HORIZONTAL);

        Mat lowData = new Mat();
        if (sizeData > 0) {
            Imgproc.resize(in, lowData, new Size(sizeData, sizeData));
        }

        int numCols = vhist.length + hhist.length + lowData.cols() * lowData.rows();
        Mat out = new Mat();
        out.create(1, numCols, CvType.CV_32F);

        int j = 0;
        for (int i = 0; i < vhist.length; i++, j++) {
            out.put(0, j, vhist[i]);
        }

        for (int i = 0; i < hhist.length; i++, j++) {
            out.put(0, j, hhist[i]);
        }

        for (int x = 0; x < lowData.rows(); x++) {
            for (int y = 0; y < lowData.cols(); y++, j++) {
                float[] arr = new float[1];
                lowData.get(x, y, arr);
                out.put(0, j, arr[0]);
            }
        }
        return out;
    }
}
