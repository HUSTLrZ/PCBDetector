# a demo to detect defects on PCB(not finished yet)

更新：
- 程序能够完成的功能：在PCB上定位疑似色环电阻的物体，并通过SVM分类得到真的电阻，
通过图像处理提取色环;
定位电容，判断电容是否接反（分析极性）。

- 待完成：通过分析提取到的色环图像得到电阻阻值


运行环境：
- JDK8
- IntelliJ IDEA or Eclipse
- OpenCV 3.2

![image](https://github.com/HUSTLrZ/ResLocator/raw/master/screenshots/src.png)
![image](https://github.com/HUSTLrZ/ResLocator/raw/master/screenshots/result.png)
![image](https://github.com/HUSTLrZ/ResLocator/raw/master/screenshots/colorband.png)