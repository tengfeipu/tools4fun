#### 本仓库用于存放一些小工具以及源码



1、**视频水印工具类**（WaterMarkTool）

本工具通过ffmpeg给视频添加水印，有两个版本，区别如下：

> v0.01通过命令行参数输入ffmpeg路径、源文件路径、水印图片路径、生成文件路径使用，需要运行环境有ffmpeg；
>
> - eg：java -jar watermarktool.jar ffmpeg test.mp4 water.png out.mp4
>
> v0.1通过javacv项目直接引入了ffmpeg，通过命令行参数输入源文件路径、水印图片路径、生成文件路径可以跨平台使用；
>
> - eg：java -jar watermarktool.jar  test.mp4 water.png out.mp4

效果图如下：

![avatar](images/watermark.gif) 

 **下载Jar包：**Click here



等碰到新的问题，会产生更多工具......



