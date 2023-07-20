package dev.tenacity.utils.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    public static void unzip(InputStream zipFile, String desDirectory) throws Exception {
        File desDir = new File(desDirectory);
        boolean ignored = desDir.mkdir();
        // 读入流
        ZipInputStream zipInputStream = new ZipInputStream(zipFile);
        // 遍历每一个文件
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
            System.out.println("Unzip: " + unzipFilePath);
            if (zipEntry.isDirectory()) { // 文件夹
                // 直接创建
                mkdir(new File(unzipFilePath));
            } else { // 文件
                File file = new File(unzipFilePath);
                // 创建父目录
                mkdir(file.getParentFile());
                // 写出文件流
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(Files.newOutputStream(Paths.get(unzipFilePath)));
                byte[] bytes = new byte[1024];
                int readLen;
                while ((readLen = zipInputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, readLen);
                }
                bufferedOutputStream.close();
            }
            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    private static void mkdir(File file) {
        if (null == file || file.exists()) {
            return;
        }
        mkdir(file.getParentFile());
        boolean ignored = file.mkdir();
    }
}
