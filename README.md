# a demo to detect defects on PCB

更新：
- 程序能够完成的功能：在PCB上定位疑似色环电阻的物体，并通过SVM分类得到真的电阻，
通过图像处理提取色环,分析色环颜色得到阻值；
定位电容，判断电容是否接反（分析极性），求出电容开口方向与正极的角度。
关于OpenCV-java的使用请参阅[个人博客](http://liruozhang.com)


运行环境：
- JDK8
- IntelliJ IDEA or Eclipse
- OpenCV 3.2

原图如下：</br>
![image](https://github.com/HUSTLrZ/PCBDetector/raw/master/screenshots/src.png)

定位出的电阻相似物：</br>
![image](https://github.com/HUSTLrZ/PCBDetector/raw/master/screenshots/result.png)

提取色环：</br>
![image](https://github.com/HUSTLrZ/PCBDetector/raw/master/screenshots/colorband.png)

定位电容：</br>
![image](https://github.com/HUSTLrZ/PCBDetector/raw/master/screenshots/capacity.jpg)

正极模板：</br>
![image](https://github.com/HUSTLrZ/PCBDetector/raw/master/screenshots/template.jpg)

电容极性判断：</br>
![image](https://github.com/HUSTLrZ/PCBDetector/raw/master/screenshots/result.jpg)
