package io.sealights.plugins.sealightsjenkins.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TextFileUtils {

    public String getContent(String filePath) throws IOException {
        String newLine = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(filePath));
            String str = in.readLine();
            while (str != null) {
                sb.append(str);
                str = in.readLine();
                if (str != null)
                    sb.append(newLine);
            }
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
