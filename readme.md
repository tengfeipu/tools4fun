### 本仓库用于存放一些小工具以及源码



1、**视频水印工具类 **[WaterMarkTool](https://github.com/tengfeipu/tools4fun/tree/master/watermark)

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

 **下载Jar包：**  [Click here](https://github.com/tengfeipu/tools4fun/releases/tag/untagged-d31a2c81f442e8e17969)

2、**数据迁移工具kettle**    [kettleWithIndex](https://github.com/tengfeipu/tools4fun/tree/master/kettleWithIndex)

kettle是可以用来迁移数据库的工具，它的功能挺多，有兴趣的可以自行去了解下。

但在本人尝试从oracle迁移到mysql的过程中，发现它仅仅迁移了表结构和数据，没有迁移索引和主键信息。因此[参考帖子](https://blog.csdn.net/j1231230/article/details/80584050?utm_medium=distribute.pc_relevant.none-task-blog-2~default~BlogCommendFromMachineLearnPai2~default-2.control)进行了修改。

需要注意：使用本工具[迁移](https://my.oschina.net/wangshengzhuang/blog/785116)后，对于索引仍然需要人工核对，使用它仅仅是减少工作量而已。

**使用**：将项目导入IDE后，运行ui/org/pentaho/di/ui/spoon/Spoon.java即可。

[kettle7.1 源仓库](https://github.com/pentaho/pentaho-kettle/tree/7.1)





等碰到新的问题，会有更多工具......



