package io.sealigths.plugins.sealightsjenkins.integration;

import java.io.*;

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

    public static String loadJarAndSaveAsTempFile(String jarNameWithoutExtension)
            throws IOException {
        String jarNameWithExtension =  jarNameWithoutExtension + ".jar";
        InputStream jarStream = JarsHelper.class.getResourceAsStream("/" + jarNameWithExtension);
        File file = null;
        try{
            if (jarStream == null) {
                throw new FileNotFoundException(jarNameWithExtension);
            }

            file = File.createTempFile(jarNameWithoutExtension, ".jar");
            file.deleteOnExit();

            copyInputStreamToFile(jarStream, file);
        }
        finally {
            if (jarStream != null)
                jarStream.close();
        }

        return file.getAbsolutePath();
    }
}
