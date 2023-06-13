package com.prs.hub.utils;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * @author fanshupeng
 * @create 2023/6/12 10:36
 */
public class FolderDownloaderUtils {

    public static void downloadFolder(String folderPath, OutputStream outputStream) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outputStream));
        Path folder = Paths.get(folderPath);

        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Provided path is not a folder.");
        }

        zipFolder(folder, folder.getFileName().toString(), zipOut);
        zipOut.close();
    }

    private static void zipFolder(Path folderPath, String folderName, ZipOutputStream zipOut) throws IOException {
        File folder = folderPath.toFile();
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipFolder(file.toPath(), folderName + "/" + file.getName(), zipOut);
                } else {
                    addFileToZip(file, folderName, zipOut);
                }
            }
        }
    }

    private static void addFileToZip(File file, String folderName, ZipOutputStream zipOut) throws IOException {
        byte[] buffer = new byte[4096];
        FileInputStream fileIn = new FileInputStream(file);
        zipOut.putNextEntry(new ZipEntry(folderName + "/" + file.getName()));

        int bytesRead;
        while ((bytesRead = fileIn.read(buffer)) != -1) {
            zipOut.write(buffer, 0, bytesRead);
        }

        fileIn.close();
    }
}
