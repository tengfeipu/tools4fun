package com.tengf.watermarktool;

import com.tengf.watermarktool.ffmpeg.FFmpegUtil;
import com.tengf.watermarktool.javacv.LoadFFmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WaterMarkTool {

    public static void main(String[] args) {
        if (args.length != 3) {
            printVersion();
            printUsage();
            return;
        }

        BufferedReader reader;
        try {
            ProcessBuilder processBuilder = FFmpegUtil.generateFFmpegProcess(LoadFFmpeg.load(),
                    args[0],  //source_file_path
                    args[1],  //watermark_pic_path
                    args[2]); //target_file_path
            Process process = processBuilder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            //读取缓冲区避免缓冲区满后调用程序阻塞
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


    private static final String VERSION = "V0.1";

    private static void printUsage() {
        System.out.println("Usage: java -jar watermark.jar source_file_path watermark_pic_path target_file_path");
    }

    private static void printVersion() {
        System.out.println("WaterMarkTool Version: " + VERSION + " By:javacv+ffmpeg");
        System.out.println("Author：tengfeipu@outlook.com");
    }

}
