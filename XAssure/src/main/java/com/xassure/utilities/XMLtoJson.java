package com.xassure.utilities;

import org.json.JSONObject;
import org.json.XML;

/**
 * Created by Prabhat Arya on  Jun,11 2019
 **/
public class XMLtoJson {

    public String convert(String xml_data) {
        JSONObject obj = XML.toJSONObject(xml_data);
        return obj.toString();
    }
}
