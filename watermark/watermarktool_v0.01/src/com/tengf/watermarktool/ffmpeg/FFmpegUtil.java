package com.tengf.watermarktool.ffmpeg;

public class FFmpegUtil {

    /**
     * 通过@@分割
     * ffmpeg -i /Users/tengf/Downloads/video_test/trailer.mp4 -i /Users/tengf/Downloads/video_test/water.png -y -filter_complex 'overlay=main_w-overlay_w-10:10' output.mp4
     */
    private static final String WARTERMARK_COMMAND = "%s@@-i@@%s@@-i@@%s@@-y@@-filter_complex@@overlay=main_w-overlay_w-10:10@@%s";

    /**
     * 构造一个调用命令行的ProcessBuilder
     *
     * @param ffmpeg ffmpeg路径
     * @param source 视频源文件地址
     * @param pic    水印图片地址
     * @param target 生成视频地址
     * @return
     */
    public static ProcessBuilder generateFFmpegProcess(String ffmpeg, String source, String pic, String target) {
        String command = String.format(WARTERMARK_COMMAND, ffmpeg, source, pic, target);
        ProcessBuilder processBuilder = new ProcessBuilder(command.split("@@"));
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

}
