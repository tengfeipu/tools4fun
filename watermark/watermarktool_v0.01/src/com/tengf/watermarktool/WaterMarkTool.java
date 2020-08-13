package com.tengf.watermarktool;

import com.tengf.watermarktool.ffmpeg.FFmpegUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WaterMarkTool {

    private static final String VERSION = "V0.01";

    public static void main(String[] args) {
        if (args.length != 4) {
            printVersion();
            printUsage();
            return;
        }

        BufferedReader reader = null;
        try {
            ProcessBuilder processBuilder = FFmpegUtil.generateFFmpegProcess(args[0], args[1], args[2], args[3]);
            Process process = processBuilder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            //读取缓冲区输出避免缓冲区满后调用程序阻塞
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            System.out.println("\n----- Over ------\n");
        } catch (IOException e) {
            System.out.println("Error:" + e);
        }

    }

    private static void printUsage() {
        System.out.println("Usage: java -jar WaterMarkTool.jar ffmpeg_path source_file_path watermark_pic_path target_file_path");
    }

    private static void printVersion() {
        System.out.println("WaterMarkTool Version: " + VERSION);
        System.out.println("Author：tengfeipu@outlook.com");
    }
}
