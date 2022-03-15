package com.xassure.utilities;


import com.opencsv.CSVWriter;
import io.restassured.response.Response;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Prabhat Arya on  May,31 2019
 **/


public class CSVWrite {
    CSVWriter writer;


    public void writeDataLineByLine(String filePath, Map<Future<Response>, Long> l, String validate, int thread, String label) throws ExecutionException, InterruptedException {
        File file = new File(filePath);
        Boolean success = false;
        try {
            FileWriter outputfile = new FileWriter(file);
            writer = new CSVWriter(outputfile);
            String[] header = {"TimeStamp", "Label Name", "ResponseTime(In MiliSeconds)", "Success", "Response Code", "Response Message", "Thread Count", "Hostname"};
            writer.writeNext(header);
            List<String[]> data = new ArrayList<String[]>();
            for (Map.Entry<Future<Response>, Long> m : l.entrySet()) {
                if (m.getKey().get().asString().contains(validate)) {
                    success = true;
                }
                data.add(new String[]{miliSecondsToDate(m.getValue()), label, String.valueOf(m.getKey().get().getTime()), String.valueOf(success), String.valueOf(m.getKey().get().getStatusCode()), String.valueOf(m.getKey().get().getStatusLine()), String.valueOf(thread), String.valueOf(InetAddress.getLocalHost().getHostName())});
            }
            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stopWriting(CSVWriter writer) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String miliSecondsToDate(Long timeInMiliSeconds) {
        DateFormat simple = new SimpleDateFormat("MMM dd yyyy HH:mm:ss:SSS");
        Date result = new Date(timeInMiliSeconds);
        return simple.format(result);
    }
}
