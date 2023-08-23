package downloaders;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MultiThreadedDownloader {
    private static final String fileUrl = "https://onesoft.com.vn/cdn/jdk.zip";
    private static final int NUM_THREADS = 4;
    private static final String savePath = "downloaded_file2.zip";

    private static final Object fileWriteLock = new Object();

    public static void main(String[] args) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int fileSize = connection.getContentLength();
            connection.disconnect();

            RandomAccessFile file = new RandomAccessFile(savePath, "rw");
            file.setLength(fileSize);
            file.close();

            int chunkSize = fileSize / NUM_THREADS;

            Thread[] threads = new Thread[NUM_THREADS];
            for (int i = 0; i < NUM_THREADS; i++) {
                int startByte = i * chunkSize;
                int endByte = (i == NUM_THREADS - 1) ? fileSize - 1 : (i + 1) * chunkSize - 1;
                threads[i] = new DownloadThread(startByte, endByte);
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            System.out.println("Download completed.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class DownloadThread extends Thread {
        private final int startByte;
        private final int endByte;

        public DownloadThread(int startByte, int endByte) {
            this.startByte = startByte;
            this.endByte = endByte;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
                InputStream inputStream = connection.getInputStream();

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }

                byte[] chunk = buffer.toByteArray();
                inputStream.close();
                connection.disconnect();

                synchronized (fileWriteLock) {
                    RandomAccessFile file = new RandomAccessFile(savePath, "rw");
                    file.seek(startByte);
                    file.write(chunk);
                    file.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
