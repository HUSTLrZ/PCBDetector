package com.lrz.train;

import org.opencv.core.Mat;

/**
 * @Author HustLrz
 * @Date Created in 15:09 2017/11/6
 */
public interface SVMCallback {

    /**
     * 获取垂直和水平的直方图图值
     * @param image
     * @return
     */
    Mat getHistogramFeatures(Mat image);

}
