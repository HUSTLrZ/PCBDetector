package com.lrz.train;

import com.lrz.util.Util;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.*;

/**
 * not finished yet
 * @Author HustLrz
 * @Date Created in 15:08 2017/11/6
 */
public class SVMTrain {

    private SVMCallback callback = new Features();
    private static final String hasResistor = "HasResistor";
    private static final String noResistor = "NoResistor";

    public SVMTrain(SVMCallback callback) {
        this.callback = callback;
    }

    public SVMTrain() {

    }

    private void divideFiles(float factor, String name) {
        String filePath = "res/data/learn/" + name;
        List<String> files = new ArrayList<String>();
        Util.getFiles(filePath, files);
        int size = files.size();
        if (0 == size) {
            System.out.println("File not found in " + filePath);
            return;
        }
        Collections.shuffle(files, new Random(new Date().getTime()));

        //随机选取70%作为训练数据，30%作为测试数据
        int boundry = (int) (factor * size);

        Util.recreateDir("res/data/train/" + name);
        Util.recreateDir("res/data/test/" + name);

        for (int i = 0; i < boundry; i++) {
            Mat img = Imgcodecs.imread(files.get(i));
            String str = "res/data/train/" + name + "/" + name + "_" + Integer.valueOf(i).toString() + ".jpg";
            Imgcodecs.imwrite(str, img);
        }

        for (int i = boundry; i < size; i++) {
            Mat img = Imgcodecs.imread(files.get(i));
            String str = "res/data/test/" + name + "/" + name + "_" + Integer.valueOf(i).toString() + ".jpg";
            Imgcodecs.imwrite(str, img);
        }
    }

    private void getResistorTrain(Mat trainingImages, List<Integer> trainingLabels, String name) {
        int label = 1;
        String filePath = "res/data/train/" + name;
        List<String> files = new ArrayList<String>();

        ////获取该路径下的所有文件
        Util.getFiles(filePath, files);

        int size = files.size();
        if (0 == size) {
            System.out.println("File not found in " + filePath);
            return;
        }

        for (int i = 0; i < size; i++) {
            Mat img = Imgcodecs.imread(files.get(i));

            //调用回调函数决定特征
            Mat features = this.callback.getHistogramFeatures(img);
            features = features.reshape(1, 1);
            trainingImages.push_back(features);
            trainingLabels.add(label);
        }
    }

    private void getResistorTest(List<Mat> trainingImages, List<Integer> trainingLabels, String name) {
        int label = 1;
        String filePath = "res/data/test/" + name;
        List<String> files = new ArrayList<String>();

        ////获取该路径下的所有文件
        Util.getFiles(filePath, files);

        int size = files.size();
        if (0 == size) {
            System.out.println("File not found in " + filePath);
            return;
        }

        for (int i = 0; i < size; i++) {
            Mat img = Imgcodecs.imread(files.get(i));
            trainingImages.add(img);
            trainingLabels.add(label);
        }
    }

    public void learn2HasResistor(){
        learn2HasResistor(0.7f);
    }

    public void learn2HasResistor(float factor){
        divideFiles(factor,hasResistor);
    }

    public void learn2NoResistor(){
        learn2NoResistor(0.7f);
    }

    public void learn2NoResistor(float factor){
        divideFiles(factor,noResistor);
    }

    public void getNoResistorTrain(Mat trainingImages, List<Integer> trainingLabels) {
        getResistorTrain(trainingImages, trainingLabels, noResistor);
    }

    public void getHasResistorTrain(Mat trainingImages, List<Integer> trainingLabels) {
        getResistorTrain(trainingImages, trainingLabels, hasResistor);
    }


    public void getHasResistorTest(List<Mat> testingImages,List<Integer> testingLabels)
    {
        getResistorTest(testingImages,testingLabels,hasResistor);
    }

    public void getNoResistorTest(List<Mat> testingImages,List<Integer> testingLabels)
    {
        getResistorTest(testingImages,testingLabels,noResistor);
    }
}
