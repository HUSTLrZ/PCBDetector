package com.lrz.util;

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
}
