package com.xassure.utilities;

import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class YamlReader {
    @SuppressWarnings("rawtypes")
    public static String getYamlValue(String fileName, String yamlKeys) {
        File file = null;
        Reader reader = null;
        int tokenCount = 0;
        int i = 0;
        Map testdata = null;

        StringTokenizer stToken = new StringTokenizer(yamlKeys, ".");

        try {
            String val = null;
            file = new File("src/main/resources/config/" + fileName + ".yaml");
            if (file.exists()) {
                reader = new FileReader(file);

                testdata = (Map) Yaml.load(reader);
                tokenCount = stToken.countTokens();

                for (i = 1; i < tokenCount; i++) {
                    String key = stToken.nextToken();
                    testdata = (Map) testdata.get(key);
                }
                val = testdata.get(stToken.nextToken()).toString();
            } else {
                System.out.println("File '" + fileName + "' not found");
            }
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public static String[][] getYamlValues(String fileName, String yamlKeys) {
        File file = null;
        Reader reader = null;
        int tokenCount = 0;
        int i = 0;
        Map testdata = null;

        StringTokenizer stToken = new StringTokenizer(yamlKeys, ".");

        try {
            String[][] values = null;
            file = new File("src/main/resources/config/" + fileName + ".yaml");
            if (file.exists()) {
                reader = new FileReader(file);
                testdata = (Map) Yaml.load(reader);
                tokenCount = stToken.countTokens();

                for (i = 1; i < tokenCount; i++) {
                    String key = stToken.nextToken();
                    testdata = (Map) testdata.get(key);
                }

                int indexi = 0;
                int indexj = 0;

                Map temp = null;
                temp = (Map) testdata.get(stToken.nextToken());

                values = new String[temp.size()][];
                Iterator<Map.Entry> entries = temp.entrySet().iterator();

                while (entries.hasNext()) {
                    Map.Entry thisEntry = entries.next();

                    Iterator<Map.Entry> entries1 = ((Map) thisEntry.getValue()).entrySet().iterator();
                    values[indexi] = new String[((Map) thisEntry.getValue()).size()];
                    indexj = 0;

                    while (entries1.hasNext()) {
                        Map.Entry thisEntry1 = entries1.next();
                        values[indexi][indexj] = thisEntry1.getValue().toString().trim();
                        indexj++;
                    }
                    indexi++;
                }
            } else {
                System.out.println("File '" + fileName + "' not found");
            }
            return values;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
