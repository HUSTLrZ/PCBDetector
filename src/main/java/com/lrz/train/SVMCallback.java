package com.lrz.train;

import org.opencv.core.Mat;

/**
 * @Author HustLrz
 * @Date Created in 15:09 2017/11/6
 */
public interface SVMCallback {

    /**
     * 生成直方图均衡特征的回调函数
     * @param image
     * @return
     */
    public Mat getHistogramFeatures(Mat image);

}
