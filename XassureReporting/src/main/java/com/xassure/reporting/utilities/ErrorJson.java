package com.xassure.reporting.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.xassure.reporting.logger.Reporting;

public class ErrorJson {

	private static boolean checkErrorJsonFile() {
		boolean checkExistence = false;
		try {
			File errorFile = new File(Reporting.getErrorJson());
			if (!errorFile.exists()) {
				errorFile.createNewFile();
			} else
				checkExistence = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return checkExistence;
	}

	@SuppressWarnings("unchecked")
	public static synchronized void appendError(String failureId, Exception exc) {
		JSONParser parser = new JSONParser();
		JSONObject errorData = new JSONObject();
		JSONObject error = new JSONObject();
		JSONArray errors = new JSONArray();
		try {
			if (checkErrorJsonFile()) {

				Object obj;

				obj = parser.parse(new FileReader(Reporting.getErrorJson()));
				JSONObject jsonObject = (JSONObject) obj;
				errors = (JSONArray) jsonObject.get("failures");

			}

			error.put("failureId", failureId);
			error.put("stackTrace", getExceptionDetails(exc));
			errors.add(error);

			errorData.put("failures", errors);

			FileWriter file = new FileWriter(Reporting.getErrorJson());
			file.write(errorData.toJSONString());
			file.flush();
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Get the exception details:-
	 * 
	 * @param e
	 * @return
	 */
	private static String getExceptionDetails(Exception e) {

		String stackTrace = "";
		for (StackTraceElement str : e.getStackTrace()) {

			stackTrace = stackTrace + str.toString() + "\n";
		}

		return e.fillInStackTrace().toString() + stackTrace;

	}

}
