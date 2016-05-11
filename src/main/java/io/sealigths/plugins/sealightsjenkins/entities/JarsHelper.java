package io.sealigths.plugins.sealightsjenkins.entities;

import java.io.*;

/**
 * Created by shahar on 5/11/2016.
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

    public static String loadJarAndSaveAsTempFile(String jarNameWithoutExtension)
            throws IOException {
        String jarNameWithExtension =  jarNameWithoutExtension + ".jar";
        InputStream jarStream = JarsHelper.class.getResourceAsStream("/" + jarNameWithExtension);
        if (jarStream == null) {
            String message = "Failed to read embedded jar '" + jarNameWithExtension + "'.";
            throw new FileNotFoundException(message);
        }
        File file = File.createTempFile(jarNameWithoutExtension, ".jar");
        file.deleteOnExit();

        copyInputStreamToFile(jarStream, file);
        return file.getAbsolutePath();
    }
}
