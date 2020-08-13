package com.tengf.watermarktool.javacv;

import org.bytedeco.javacpp.Loader;

public class LoadFFmpeg {

    /**
     * 获取ffmpeg路径
     *
     * @return javacv库中的ffmpeg路径
     */
    public static String load() {
        return Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
    }
}
