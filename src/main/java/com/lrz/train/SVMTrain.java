package com.lrz.train;

import com.lrz.util.Util;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
import org.opencv.ml.TrainData;

import java.util.*;

/**
 *
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
        int label;
        if (name.equals("HasResistor")) {
            label = 1;
        } else {
            label = 0;
        }
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
            Mat features = callback.getHistogramFeatures(img);
            features = features.reshape(1, 1);

            trainingImages.push_back(features);
            trainingLabels.add(label);
        }
    }

    private void getResistorTest(List<Mat> trainingImages, List<Integer> trainingLabels, String name) {
        int label;
        if (name.equals("HasResistor")) {
            label = 1;
        } else {
            label = 0;
        }
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

    public void learn2HasResistor() {
        learn2HasResistor(0.7f);
    }

    public void learn2HasResistor(float factor) {
        divideFiles(factor, hasResistor);
    }

    public void learn2NoResistor() {
        learn2NoResistor(0.7f);
    }

    public void learn2NoResistor(float factor) {
        divideFiles(factor, noResistor);
    }

    public void getNoResistorTrain(Mat trainingImages, List<Integer> trainingLabels) {
        getResistorTrain(trainingImages, trainingLabels, noResistor);
    }

    public void getHasResistorTrain(Mat trainingImages, List<Integer> trainingLabels) {
        getResistorTrain(trainingImages, trainingLabels, hasResistor);
    }


    public void getHasResistorTest(List<Mat> testingImages, List<Integer> testingLabels) {
        getResistorTest(testingImages, testingLabels, hasResistor);
    }

    public void getNoResistorTest(List<Mat> testingImages, List<Integer> testingLabels) {
        getResistorTest(testingImages, testingLabels, noResistor);
    }

    public int svmTrain(boolean dividePrepared, boolean trainPrepared) {
        Mat dataMat = new Mat();    //训练数据
        Mat labelMat = new Mat();   //标签


        Mat trainingImages = new Mat();
        List<Integer> trainingLabels = new ArrayList<Integer>();

        //分散样本
        if (!dividePrepared) {
            System.out.println("devide learn to train and test");
            learn2HasResistor();
            learn2NoResistor();
        }

        //将样本载入内存
        if (!trainPrepared) {
            System.out.println("begin to get train data to memory");
            getHasResistorTrain(trainingImages, trainingLabels);
            getNoResistorTrain(trainingImages, trainingLabels);

            trainingImages.copyTo(dataMat);
            dataMat.convertTo(dataMat, CvType.CV_32FC1);

            labelMat.create(trainingLabels.size(), 1, CvType.CV_32SC1);
            for (int i = 0; i < trainingLabels.size(); i++) {
                labelMat.put(i, 0, trainingLabels.get(i));
            }

            trainingImages.release();
            trainingLabels = null;
        }

        SVM svm = SVM.create();
        svm.setType(SVM.C_SVC);
        svm.setKernel(SVM.RBF);
        svm.setDegree(0.1);
        svm.setGamma(0.1);
        svm.setCoef0(0.1);
        svm.setNu(0.1);
        svm.setP(0.1);
        svm.setC(1);
        svm.setTermCriteria(new TermCriteria(1, 20000, 0.0001));

        TrainData trainData = TrainData.create(dataMat, Ml.ROW_SAMPLE, labelMat);
        svm.train(trainData.getSamples(), Ml.ROW_SAMPLE, trainData.getResponses());

//        svm.save("res/model/svm.xml");   //出现问题
        dataMat.release();
        labelMat.release();

        List<Mat> testingImages = new ArrayList<Mat>();
        List<Integer> testingLabels = new ArrayList<Integer>();
        System.out.println("begin to get test data to memory");

        getHasResistorTest(testingImages, testingLabels);
        getNoResistorTest(testingImages, testingLabels);

        double ptrue_rtrue = 0;
        double ptrue_rfalse = 0;
        double pfalse_rtrue = 0;
        double pfalse_rfalse = 0;

        int size = testingImages.size();
        for (int i = 0; i < size; i++) {
            Mat p = testingImages.get(i);

            //调用回调函数决定特征
            Mat features = callback.getHistogramFeatures(p);
            features = features.reshape(1, 1);
            features.convertTo(features, CvType.CV_32FC1);

            int predict = (int) svm.predict(features);
            int real = testingLabels.get(i);

            if (predict == 1 && real == 1)
                ptrue_rtrue++;
            if (predict == 1 && real == 0)
                ptrue_rfalse++;
            if (predict == 0 && real == 1)
                pfalse_rtrue++;
            if (predict == 0 && real == 0)
                pfalse_rfalse++;
        }

        System.out.println("ptrue_rtrue: " + Double.valueOf(ptrue_rtrue).toString());
        System.out.println("ptrue_rfalse: " + Double.valueOf(ptrue_rfalse).toString());
        System.out.println("pfalse_rtrue: " + Double.valueOf(pfalse_rtrue).toString());
        System.out.println("pfalse_rfalse: " + Double.valueOf(pfalse_rfalse).toString());
        return 0;
    }
}
