package com.xassure.reporting.csvHandlers;

import java.io.FileReader;
import java.io.IOException;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.xassure.reporting.beans.DetailSteps;

public class CsvReader {

	static final String CSV_FILENAME = "C:/Users/harshp/Documents/sample.csv";

	public static void main(String[] args) throws IOException {
		ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(CSV_FILENAME), CsvPreference.STANDARD_PREFERENCE);
		// the header elements are used to map the values to the bean
		final String[] headers = beanReader.getHeader(true);
		final CellProcessor[] processors = getProcessors();
		DetailSteps detailStep;
		while ((detailStep = beanReader.read(DetailSteps.class, headers, processors)) != null) {
			System.out.println(detailStep.getPageName());

		}
		beanReader.close();
	}

	/**
	 * Sets up the processors used for the examples.
	 */
	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new NotNull(new ParseLong()), // runId
				new NotNull(), // testcasename
				new Optional(), // pagename
				new NotNull(), // testStep
				new NotNull() // status
		};
		return processors;
	}

}
