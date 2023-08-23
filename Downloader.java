package downloaders;

import java.io.*;
import java.net.URL;

public class Downloader {
    public static void main(String[] args) {
        String fileUrl = "https://onesoft.com.vn/cdn/jdk.zip";
        String savePath = "downloaded_file.zip";

        try {
            URL url = new URL(fileUrl);
            InputStream inputStream = url.openStream();
            FileOutputStream outputStream = new FileOutputStream(savePath);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("File downloaded successfully!");

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}