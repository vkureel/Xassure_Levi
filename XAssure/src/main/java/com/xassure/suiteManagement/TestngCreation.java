package com.xassure.suiteManagement;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

public class TestngCreation {
	public List<TestCaseDetails> readExcel(String file, String sheetname) {
		List<TestCaseDetails> testDetails = new ArrayList<>();
		Connection connection = null;
		Recordset record = null;

		try {
			Fillo fillo = new Fillo();
			connection = fillo.getConnection(file);
			record = connection.executeQuery("select * from " + sheetname);
			List<String> fields = record.getFieldNames();
			while (record.next()) {
				if (record.getField("Execute").equalsIgnoreCase("yes")) {
					TestCaseDetails testDetail = new TestCaseDetails();

					testDetail.setModuleName(record.getField("ModuleName"));
					testDetail.setTestcaseName(record.getField("TestcaseName"));
					testDetail.setTestcaseType(record.getField("ApplicationType"));
					testDetail.setTestcaseBrowser(record.getField("Browser"));
					testDetails.add(testDetail);
				}
			}

			return testDetails;
		} catch (Exception e) {
			System.out.println("Exception in reading the excel");
			e.printStackTrace();
			return null;
		} finally {
			record.close();
			connection.close();
		}
	}

	public Map<String, String> readConfiguration(String file, String sheetname) {
		Map<String, String> configurations = new HashMap<>();
		Connection connection = null;
		Recordset record = null;

		try {
			Fillo fillo = new Fillo();
			connection = fillo.getConnection(file);
			record = connection.executeQuery("select * from " + sheetname);

			while (record.next()) {
				configurations.put(record.getField("property"), record.getField("value"));
			}
			return configurations;
		} catch (Exception e) {

			return null;
		} finally {
			record.close();
			connection.close();
		}
	}

	public static void createXmlFile(XmlSuite mSuite, String filePath) {
		try {
			FileWriter writer = new FileWriter(new File("testng.xml"));
			writer.write(mSuite.toXml());
			writer.flush();
			writer.close();
			System.out.println((new File("testng.xml")).getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TestNG createTestng(String xmlFilePath, List<TestCaseDetails> testDetails,
			Map<String, String> configurations) {
		TestNG testNg = new TestNG();

		try {
			testNg = new TestNG();
			List<XmlSuite> suites = getSuite(configurations, testDetails);
			testNg.setXmlSuites(suites);

			createXmlFile(suites.get(0), xmlFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testNg;
	}

	public List<XmlSuite> getSuite(Map<String, String> configurations, List<TestCaseDetails> testDetails) {
		List<XmlSuite> suites = new ArrayList<>();
		XmlSuite suite = new XmlSuite();

		try {
			suite.setName(configurations.get("Suite Name"));
			suite.setVerbose(Integer.valueOf(Integer.parseInt(configurations.get("Verbose"))));
			XmlSuite.ParallelMode mode = XmlSuite.ParallelMode.FALSE;

			if (((String) configurations.get("Parallel")).equalsIgnoreCase("false")) {
				mode = XmlSuite.ParallelMode.FALSE;
			} else if (((String) configurations.get("parallel")).equalsIgnoreCase("mathods")) {
				mode = XmlSuite.ParallelMode.METHODS;
			} else if (((String) configurations.get("parallel")).equalsIgnoreCase("tests")) {
				mode = XmlSuite.ParallelMode.TESTS;
			} else if (((String) configurations.get("parallel")).equalsIgnoreCase("classes")) {
				mode = XmlSuite.ParallelMode.CLASSES;
			}

			suite.setParallel(mode);
			suite.setThreadCount(Integer.parseInt(configurations.get("Threads")));

			suite.setTests(createTests(suite, testDetails));
			suites.add(suite);
		} catch (Exception exception) {
		}

		return suites;
	}

	public List<XmlTest> createTests(XmlSuite suite, List<TestCaseDetails> testDetails) {
		List<XmlTest> tests = new ArrayList<>();
		int count = 0;

		try {
			for (TestCaseDetails testDetail : testDetails) {
				XmlTest test = new XmlTest(suite);
				test.setName("test " + count++);
				Map<String, String> parameter = new HashMap<>();
				parameter.put("platform", testDetail.getTestcaseType());
				parameter.put("browser", testDetail.getTestcaseBrowser());
				test.setParameters(parameter);
				test.setClasses(createClass(testDetail));

				tests.add(test);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tests;
	}

	public List<XmlClass> createClass(TestCaseDetails testDetail) {
		List<XmlClass> xmlClasses = new ArrayList<>();

		try {
			XmlClass xmlClass = new XmlClass();
			xmlClass.setName(
					getBusinessObjectClassNamesForTestNg(System.getProperty("user.dir") + "/src/test/java", testDetail

							.getModuleName()));
			XmlInclude include = new XmlInclude(testDetail.getTestcaseName());

			List<XmlInclude> includes = new ArrayList<>();
			includes.add(include);
			xmlClass.setIncludedMethods(includes);

			xmlClasses.add(xmlClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlClasses;
	}

	private void separateJavaPackage(String directoryName, String className) {
		File directory = new File(directoryName);

		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isDirectory()) {
				separateJavaPackage(file.toString(), className);
			} else if (file.isFile() && file.getName().split(".java")[0].equalsIgnoreCase(className)) {

				businessObjectClassName = "com"
						+ file.toString().split("com")[1].replace("\\", ".").replace("/", ".").replace(".java", "");
			}
		}
	}

	private String businessObjectClassName = "";

	private String getBusinessObjectClassNamesForTestNg(String directoryName, String className) {
		businessObjectClassName = "";

		if (className.contains(".java")) {
			className = className.split(".java")[0];
		}
		separateJavaPackage(directoryName, className);

		if (!this.businessObjectClassName.split("\\.")[(this.businessObjectClassName.split("\\.")).length - 1]
				.equalsIgnoreCase(className.split(".java")[0])) {
			System.out.println("Cannot find '" + className + ".java' \n in " + directoryName);
		}
		return businessObjectClassName;
	}

	public static void main(String[] args) {
		TestngCreation testCreation = new TestngCreation();

		String directory = System.getProperty("user.dir");
		String fileName = directory + "/src/main/resources/ExcelSuite.xlsx";

		System.out.println(fileName);
		List<TestCaseDetails> testDetails = testCreation.readExcel(fileName, "SuiteDetails");
		Map<String, String> config = testCreation.readConfiguration(fileName, "Configuration");

		testCreation.createTestng(directory, testDetails, config);
	}
}
