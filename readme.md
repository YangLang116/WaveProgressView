# WaveProgressView的简介

##介绍
> 
> ####控件自定义属性及格式
* 圆形背景图片 name="backgroundDrawable" format="reference"
* 圆形背景颜色 name="backgroundColor" format="color|reference"
* 外圆环宽度 name="circleRingWidth" format="dimension|reference"
* 外圆环颜色 name="circleRingColor" format="color|reference"
* 水波颜色 name="waveColor" format="color|reference"
* 水波起伏的最大数量 name="waveMaxNum" format="integer"
* 水波起伏数量的波动幅度 name="waveNumRange" format="integer"
* 水波波峰的高度 name="waveHeight" format = "dimension|reference"
* 水波波峰高度落差幅度 name="waveHeightRange" format="dimension|reference"
* 水波显示的当前进度 name="waveProgress" format="float"
* 显示的文本内容 name="contentText" format="string|reference"
* 显示文本内容的字体大小 name="contentTextSize" format="dimension|reference"
* 显示文本字体颜色 name="contentTextColor" format="color|reference"
* 显示文本与圆环的间隔 name="contentTextPaddinng" format="dimension|reference"
>

##控件应用示例
> ####样例中，有关颜色的调整，只做了RGB其中一个通道修改的演示。
![image](http://119.29.119.42/android/waveprogressview.gif)

##使用指南
>#### WaveProgressView的属性修改和获取的格式为：
* setmXXX()
* getmXXX()
* 提供播放进度动画时，进度改变回调接口 setonAnimatorProgressListener

##引用指南
>本项目为示例源码，其中引入了waveprogressLib（核心包括WaveProgressView.java和WaveProgressView.xml）,如果需要在Eclipse使用只需要拷贝这两个文件到src目录和values目录。










