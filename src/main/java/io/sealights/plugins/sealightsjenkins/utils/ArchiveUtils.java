package io.sealights.plugins.sealightsjenkins.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ArchiveUtils {
    private Logger logger;

    public ArchiveUtils(Logger logger) {
        this.logger = logger;
    }

    public void unzip(String zipName, String destFolder){

        if (StringUtils.isNullOrEmpty(zipName)) {
            throw new IllegalArgumentException("Unable to unzip because the zip file name is null or empty.");
        }
        if (StringUtils.isNullOrEmpty(destFolder)) {
            throw new IllegalArgumentException("Unable to unzip because the destination folder is null or empty.");
        }
        ZipInputStream zis = null;
        try {

            byte[] buffer = new byte[1024];

            //create output directory is not exists
            FileAndFolderUtils.verifyFolderExists(destFolder);

            //get the zip file content
            zis = new ZipInputStream(new FileInputStream(zipName));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String entryFileName = ze.getName();
                try {
                    String filePath = PathUtils.join(destFolder, entryFileName);
                    File currentZippedFile = new File(filePath);
                    logger.debug("File unzip : " + currentZippedFile.getAbsoluteFile());

                    //create all non exists folders
                    //else you will hit FileNotFoundException for compressed folder
                    new File(currentZippedFile.getParent()).mkdirs();

                    FileOutputStream fos = new FileOutputStream(currentZippedFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                    ze = zis.getNextEntry();
                } catch (Exception e) {
                    logger.error("Failed to process the current zip entry. entryFileName: '" + entryFileName + "'. Error", e);
                }
            }
            zis.closeEntry();
        } catch (Exception e) {
            logger.error("Failed to unzip the current file. zipName: '" + zipName + "', destFolder: '" + destFolder + "'. Error", e);
        } finally {
            tryCloseStreamSafe(zis);
        }
    }

    private void tryCloseStreamSafe(ZipInputStream zis) {
        if (zis != null) {
            try{
                zis.close();
            }catch (Exception e)
            {
                logger.error("Failed closing the stream. Error:", e);
            }

        }
    }
}
