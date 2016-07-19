package io.sealights.plugins.sealightsjenkins.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {
	public static String toString(InputStream stream)
	{
		if (stream == null)
			return null;

		StringBuilder sb = new StringBuilder();
		BufferedReader bufferedReader = null;
		try{
			InputStreamReader streamReader =  new InputStreamReader(stream);
			bufferedReader = new BufferedReader(streamReader);
			
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
				
			bufferedReader.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to read from InputStream. Error:", e);
		}
		
		return sb.toString();
	}
}
