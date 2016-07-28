package io.sealights.plugins.sealightsjenkins.integration;

import io.sealights.plugins.sealightsjenkins.utils.StringUtils;


import java.io.*;
import java.util.UUID;

/**
 * Created by Nadav on 4/20/2016.
 */
public class JarsHelper {
    private static void copyInputStreamToFile(InputStream inputStream, File targetFile) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null)
                outputStream.close();
            if (inputStream != null)
                inputStream.close();
        }
    }

    public static String loadJarAndSaveAsTempFile(String jarNameWithoutExtension, String overrideJarLocation) throws IOException {
        String jarNameWithExtension = jarNameWithoutExtension + ".jar";
        InputStream jarStream = JarsHelper.class.getResourceAsStream("/" + jarNameWithExtension);
        if (jarStream == null) {
            String message = "Failed to read embedded jar '" + jarNameWithExtension + "'.";
            throw new FileNotFoundException(message);
        }
        File file;
        if (!StringUtils.isNullOrEmpty(overrideJarLocation)) {
            String tempFileName = jarNameWithoutExtension + "_" + UUID.randomUUID() + ".jar";
            file = new File(overrideJarLocation, tempFileName);
            file.createNewFile();
        } else {
            file = File.createTempFile(jarNameWithoutExtension, ".jar");
        }

        copyInputStreamToFile(jarStream, file);
        return file.getAbsolutePath();
    }
}
