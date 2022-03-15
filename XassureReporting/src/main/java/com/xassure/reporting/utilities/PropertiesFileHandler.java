package com.xassure.reporting.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFileHandler {

	static Properties properties;

	public PropertiesFileHandler() {
		properties = new Properties();
	}

	public  String readProperty(String fileName, String key) {
		String value = "";
		try {

			String file = "src/main/resources/setup/" + fileName + ".properties";
			if (new File(file).exists()) {
				InputStream inPropFile = null;
				inPropFile = new FileInputStream(file);
				properties.load(inPropFile);

				value = properties.getProperty(key);
				inPropFile.close();
			} else {
				System.out.println("File '" + file + "' not found");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

}
