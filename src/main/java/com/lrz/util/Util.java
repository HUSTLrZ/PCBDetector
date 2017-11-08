package com.lrz.util;

import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.List;

/**
 * @Author HustLrz
 * @Date Created in 15:31 2017/11/6
 */
public class Util {

    /**
     * get all files under the directory path
     *
     * @param path
     * @param files
     */
    public static void getFiles(String path, List<String> files) {
        getFiles(new File(path), files);
    }

    /**
     * delete and create a new directory with the same name
     *
     * @param dir
     */
    public static void recreateDir(String dir) {
        new File(dir).delete();
        new File(dir).mkdir();
    }

    private static void getFiles(File dir, List<String> files) {
        File[] filelist = dir.listFiles();
        for (File file : filelist) {
            if (file.isDirectory()) {
                getFiles(file, files);
            } else {
                files.add(file.getAbsolutePath());
            }
        }
    }

    /**
     * show image in window
     *
     * @param image
     * @param windowName
     */
    public static void imshow(Mat image, String windowName) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame jFrame = new JFrame(windowName);
        JLabel imageView = new JLabel();
        final JScrollPane imageScrollPane = new JScrollPane(imageView);
        imageScrollPane.setPreferredSize(new Dimension(500, 500)); // set window
        // size
        jFrame.add(imageScrollPane, BorderLayout.CENTER);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Image loadedImage = toBufferedImage(image);
        imageView.setIcon(new ImageIcon(loadedImage));
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    private static Image toBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer); // 获取所有的像素点
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }
}
