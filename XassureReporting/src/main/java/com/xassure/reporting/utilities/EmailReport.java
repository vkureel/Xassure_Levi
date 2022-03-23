package com.xassure.reporting.utilities;

import com.xassure.reporting.logger.Logger;
import com.xassure.reporting.logger.Reporting;
import com.xassure.reporting.testCaseDetails.TestCaseData;
import com.xassure.reporting.testcasesummary.LocaleLanguage;
import com.xassure.reporting.testcasesummary.Module;
import org.apache.commons.lang3.text.WordUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EmailReport {

    public static int totalTestCases;
    static int skippedTestCases;
    String testCaseName;
    String browser;
    String testCaseStatus;
    String testCaseType;
    String overAllStatus;
    int passedTestCases = 0;
    int failedTestCases = 0;
    PropertiesFileHandler propFileHandle = new PropertiesFileHandler();
    String receiver_to = propFileHandle.readProperty("emailConfiguration", "to");
    String isLocaleEnabled = propFileHandle.readProperty("emailConfiguration", "isLocaleEnabled");
    String cc_to = propFileHandle.readProperty("emailConfiguration", "cc");
    String locale = propFileHandle.readProperty("emailConfiguration", "locale");

    Map<String, List<Integer>> resultCache = new HashMap<>();
    List<Module> moduleList;

    /**
     * Get current system date.
     *
     * @return String currentDate
     */
    public static String getCurrentDate() {
        String currentDate = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        currentDate = dateFormat.format(date).trim();
        return currentDate;
    }

    private static void sendOutlookEmail() throws MessagingException {

        PropertiesFileHandler propertiesFileHandler = new PropertiesFileHandler();
        String username = propertiesFileHandler.readProperty("emailConfiguration", "username");
        String password = propertiesFileHandler.readProperty("emailConfiguration", "password");
        Properties props;
        Session session;
        props = new Properties();
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.port", "587");
//        props.put("mail.smtp.host", "smtp.office365.com");
//        props.put("mail.smtp.auth", "true");
//        session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username,
//                        password);
//            }
//        });

        //Get the session object 
        String host = "smtphost.levi.com";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        session = Session.getDefaultInstance(properties);


        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        PropertiesFileHandler propFileHandle = new PropertiesFileHandler();
        String receiver_to = propFileHandle.readProperty("emailConfiguration", "to");

        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(receiver_to, false));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM-dd");
        LocalDateTime now = LocalDateTime.now();

        /* 15032021-added browser in the status for levi*/
        message.setSubject("Automation Execution Report - " + System.getProperty("environment") + " " + dtf.format(now));


        // Create the message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        // Fill the message
        messageBodyPart.setContent("sample test email", "text/plain");
        Multipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(messageBodyPart);


        message.setContent(multiPart);
        Transport.send(message);
        System.out.println("Email Sent");
    }

    public static void main(String[] args) throws MessagingException {
        sendOutlookEmail();
    }

    public int getSkippedTestCaseCount() {
        return skippedTestCases;
    }

    private void setData() {

        for (Logger logger : Reporting.getLoggerList()) {
            if (resultCache.containsKey(logger.getTestCaseData().getTestCaseModule())) {
                String status = logger.getTestCaseData().getTestCaseStatus();
                Integer totalCount = resultCache.get(logger.getTestCaseData().getTestCaseModule()).get(0);
                resultCache.get(logger.getTestCaseData().getTestCaseModule()).set(0, totalCount + 1);
                if (status.equalsIgnoreCase("pass")) {
                    Integer passValue = resultCache.get(logger.getTestCaseData().getTestCaseModule()).get(1);
                    resultCache.get(logger.getTestCaseData().getTestCaseModule()).set(1, passValue + 1);
                    passedTestCases++;
                } else if ((status.equalsIgnoreCase("fail"))) {
                    Integer failValue = resultCache.get(logger.getTestCaseData().getTestCaseModule()).get(2);
                    resultCache.get(logger.getTestCaseData().getTestCaseModule()).set(2, failValue + 1);
                    failedTestCases++;
                }

            } else {
                String status = logger.getTestCaseData().getTestCaseStatus();
                resultCache.put(logger.getTestCaseData().getTestCaseModule(), new ArrayList<Integer>() {
                    {
                        if (status.equalsIgnoreCase("pass")) {
                            add(1);
                            add(1);
                            add(0);
                            passedTestCases++;
                        } else {
                            add(1);
                            add(0);
                            add(1);
                            failedTestCases++;
                        }
                    }
                });
            }

        }

        if (failedTestCases > 0) {
            overAllStatus = "FAIL";
        } else if (failedTestCases == 0 && skippedTestCases > 0) {
            overAllStatus = "SKIPPED";
        } else if (failedTestCases == 0 && skippedTestCases == 0 && passedTestCases != 0) {
            overAllStatus = "PASS";
        } else {
            overAllStatus = "SKIPPED";
        }
        int testcaseExecuted = Reporting.getLoggerList().size();
        skippedTestCases = totalTestCases - testcaseExecuted;
        System.out.println("Total TC triggered = " + totalTestCases);
        System.out.println("Total TC executed = " + testcaseExecuted);

        resultCache.forEach((moduleName, resultList) -> {
            System.out.println("Module Name = " + moduleName + "****************");
            System.out.println("Total TC executed = " + resultList.get(0));
            System.out.println("Total TC passed = " + resultList.get(1));
            System.out.println("Total TC failed = " + resultList.get(2));
            System.out.println("Total TC skipped = " + (resultList.get(0).intValue() - resultList.get(1).intValue() - resultList.get(2).intValue()));
        });

        moduleList = getModuleMapping();
        moduleList.forEach(module -> System.out.println(module.toString()));
    }

    public void sendEmail() {
        try {
            if (receiver_to != null &&
                    !(receiver_to.isEmpty() || receiver_to.equalsIgnoreCase(""))) {
                PropertiesFileHandler propertiesFileHandler = new PropertiesFileHandler();
                String username = propertiesFileHandler.readProperty("emailConfiguration", "username");
                String password = propertiesFileHandler.readProperty("emailConfiguration", "password");
                String host = propertiesFileHandler.readProperty("emailConfiguration", "host");


                setData();
//                Properties props = System.getProperties();
//                props.put("mail.smtp.starttls.enable", "true");
//                props.put("mail.smtp.port", "587");
//                props.put("mail.smtp.host", "smtp.office365.com");
//                props.put("mail.smtp.auth", "true");
//                Session session = Session.getInstance(props, new Authenticator() {
//                    @Override
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(username,
//                                password);
//                    }
//                });
                Properties properties = System.getProperties();
                properties.setProperty("mail.smtp.host", host);
                Session session = Session.getDefaultInstance(properties);

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver_to, false));
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc_to, false));
//                message.setSubject("Execution Report for Run - " + Reporting.getRunId());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM-dd");
                LocalDateTime now = LocalDateTime.now();
                message.setSubject("Automation Execution Report - " + System.getProperty("environment").toUpperCase() + " / " + dtf.format(now));
                Multipart multipart = setAttachement();
                message.setContent(multipart);

                String isOrder = System.getProperty("IS_ORDER");
                if (isOrder != null && isOrder.equalsIgnoreCase("true")) {
                    MimeBodyPart attachPart = new MimeBodyPart();
                    String attachFile = "src/test/resources/testdata/app/orderCompletion/OrderDetails.xlsx";
                    attachPart.attachFile(attachFile);
                    multipart.addBodyPart(attachPart);
                }
//email attachment-START
                MimeBodyPart attachPart = new MimeBodyPart();
                String attachFile = "reports/"+Reporting.getRunId()+"/DetailedReport.csv";
                attachPart.attachFile(attachFile);
                multipart.addBodyPart(attachPart);
//email attachment-END
                Transport.send(message);
                System.out.println("Email Sent");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Multipart setAttachement() throws Exception {
        // Create the message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        // Fill the message
        messageBodyPart.setContent(setHtmlBody(), "text/html");
        Multipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(messageBodyPart);
        return multiPart;
    }

    private String getUserName() {
        String user = "";
        try {
            // user = "harsh.prasad@xebia.com";
            user = receiver_to;
            user = (user.replaceAll("^*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", "")).trim();

            if (user.contains(".")) {
                user = user.replace(".", " ");
                user = user.replaceAll("[0-9]*", "");
                user = WordUtils.capitalize(user);
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return user;
        }
    }

    private String setHtmlBody() {
        try {
            StringBuffer htmlReport = new StringBuffer();

            htmlReport.append("<!DOCTYPE html>");
            htmlReport.append("<html lang=\"en\">");
            htmlReport.append("<head>");
            htmlReport.append("<meta charset=\"UTF-8\" />");
            htmlReport.append("<title>Test Automation Result</title>");
            htmlReport.append("<style>");
            htmlReport.append("body {font-family:calibri; font-size:100%}");
            htmlReport.append(
                    "h1 {font-family:calibri; font-size:130%; margin-top:30px; padding-top:4px; border-top:4px solid #134664}");
            htmlReport.append("h2 {font-family:calibri; font-size: 130%; margin-top: 20px; padding-top: 4px}");
            htmlReport.append("h3 {font-family:calibri; font-size: 130%; margin-top: 20px; padding-top: 4px}");
            htmlReport.append(
                    "table {font-family:calibri; font-size:100%; border: 2px solid #134664; border-collapse: collapse; -moz-box-shadow: 3px 3px 4px #83D5E8; -webkit-box-shadow: 3px 3px 4px #83D5E8; box-shadow: 3px 3px 4px #83D5E8}");
            htmlReport.append(
                    "td, th {font-family:calibri; font-size:100%; text-align: left; border: 1px solid #134664; padding: 4px 20px 4px 20px}");
            htmlReport.append("th {font-family:calibri; font-size:100%; text-shadow: 2px 2px 2px white}");
            htmlReport.append(
                    "th {font-family:calibri; font-size:100%; border-bottom: 1px solid #134664; background-color: #AECFDC;}");
            htmlReport.append(".total {font-weight: bold; text-align: left; color: #00126A;}");
            htmlReport.append(".pass {font-weight: bold; text-align: center; color: #006600;}");
            htmlReport.append(".fail {font-weight: bold; text-align: center; color: #990000;}");
            htmlReport.append(".skipped {font-weight: bold; text-align: center; color: #DBA901;}");
            htmlReport.append("</style>");
            htmlReport.append("</head>");
            htmlReport.append("<body text='#00126A'>");
            // htmlReport.append("Dear " + getUserName() + ",");
            htmlReport.append("<br/><br/>");
            htmlReport.append("Greetings !");
            htmlReport.append("<br/><br/>");
            htmlReport.append("Automated execution run initiated on " + getCurrentDate() + " at "
                    + Reporting.getExecutionStartTime() + " has been completed.");


            htmlReport.append("<br/><br/>");
            htmlReport.append("<b>Run Id: </b>" + Reporting.getRunId() + "<br/><br/>");

            /* 15032021-removed overall status for levi

            htmlReport.append("<b>Overall Status - " + overAllStatus + "</b>");

            */
            htmlReport.append("<h1>Environment details:</h1>");
            htmlReport.append("<table>");
            htmlReport.append("<tr class='environmentDetails'>");

//            OLD IMPLEMENTATION:-
            // htmlReport.append("<th>Browser</th>");
//            htmlReport.append("<th>Operating System</th>");
            // htmlReport.append("<th>Execution Environment</th>");
//            htmlReport.append("<th>Total time taken in execution</th>");
//            htmlReport.append("<th>ExecutionEnv</th>");

//            NEW IMPLEMENTATION:-
            htmlReport.append("<th>Brand</th>");
            htmlReport.append("<th>Environment</th>");
            htmlReport.append("<th>Suite</th>");
            htmlReport.append("<th>Date</th>");
            htmlReport.append("<th>Total Execution Time</th>");
            htmlReport.append("<th>Time Zone</th>");
            htmlReport.append("</tr>");
            htmlReport.append("<tr>");

            htmlReport.append("<td class='brand name'>" + System.getProperty("app") + "</td>");
            htmlReport.append("<td class='executionEnv'>" + System.getProperty("environment") + "</td>");
            htmlReport.append("<td class='suite'>" + System.getProperty("testConfig") + "</td>");
            htmlReport.append("<td class='date'>" + getCurrentDate() + "</td>");
            htmlReport.append("<td class='timeTakenInExecution'>" + Reporting.getExecutionTime() + "</td>");
            htmlReport.append("<td class='timezone'>PDT</td>");
            htmlReport.append("</tr>");
            htmlReport.append("</table>");


//            htmlReport.append("<h2>Execution summary:</h2>");
//            htmlReport.append("<table>");
//            htmlReport.append("<tr class='runDetails'>");
//            htmlReport.append("<th>Host Name</th>");
//            htmlReport.append("<th>Execution Start Date</th>");
//            htmlReport.append("<th>Execution Start Time</th>");
//            htmlReport.append("<th>Time Zone</th>");
//            htmlReport.append("</tr>");
//            htmlReport.append("<tr>");
//            String hostName = "";
//            try {
//                hostName = InetAddress.getLocalHost().getHostName().trim();
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
//            htmlReport.append("<td class='executedBy'>" + hostName + "</td>");
//            htmlReport.append("<td class='executionInitiatedDate'>" + getCurrentDate() + "</td>");
//            htmlReport.append("<td class='executionInitiatedTime'>" + Reporting.getExecutionStartTime() + "</td>");
//            String timeZone = Calendar.getInstance().getTimeZone().getDisplayName().toString();
//            htmlReport.append("<td class='executionInitiatedTime'>" + timeZone + "</td>");
//            htmlReport.append("</tr>");
            htmlReport.append("</table>");

//            htmlReport.append("<h3>Summary:</h3>");
//            htmlReport.append("<h3>Resu /lt:</h3>");
            htmlReport.append("<h3>Execution Summary (Overall):</h3>");
            htmlReport.append("<table>");
            htmlReport.append("<tbody>");
            htmlReport.append("<tr class='resultDetails'>");
            htmlReport.append("<th>Module</th>");
            htmlReport.append("<th>Total Business Scenarios</th>");
            htmlReport.append("<th># PASS</th>");
            htmlReport.append("<th># FAIL</th>");
            htmlReport.append("<th># Skipped</th>");
            htmlReport.append("</tr>");
            AtomicInteger totalCount = new AtomicInteger();
            AtomicInteger totalCountSkipped = new AtomicInteger();
            AtomicInteger totalCountPass = new AtomicInteger();
            AtomicInteger totalCountFail = new AtomicInteger();
            resultCache.forEach((moduleName, resultList) -> {
                totalCount.addAndGet(resultList.get(0));
                totalCountSkipped.addAndGet((resultList.get(0).intValue() - resultList.get(1).intValue() - resultList.get(2).intValue()));
                totalCountPass.addAndGet(resultList.get(1));
                totalCountFail.addAndGet(resultList.get(2));
                htmlReport.append("<tr>");
                htmlReport.append("<td class='moduleName'>" + moduleName + " ( " + resultList.get(0) + " ) " + "</td>");
                htmlReport.append("<td class='total'>" + resultList.get(0) + "</td>");
                htmlReport.append("<td class='pass'>" + resultList.get(1) + "</td>");
                htmlReport.append("<td class='fail'>" + resultList.get(2) + "</td>");
                htmlReport.append("<td class='skipped'>" + (resultList.get(0).intValue() - resultList.get(1).intValue() - resultList.get(2).intValue()) + "</td>");
                htmlReport.append("</tr>");
            });

            totalTestCases = totalCount.intValue();
            skippedTestCases = totalCountSkipped.intValue();
            passedTestCases = totalCountPass.intValue();
            failedTestCases = totalCountFail.intValue();

            if (failedTestCases > 0) {
                overAllStatus = "FAIL";
            }
            htmlReport.append("<tr>");
            htmlReport.append("<td class='moduleName'>Total</td>");
            htmlReport.append("<td class='total'>" + totalCount + "</td>");
            htmlReport.append("<td class='pass'>" + totalCountPass + "</td>");
            htmlReport.append("<td class='fail'>" + totalCountFail + "</td>");
            htmlReport.append("<td class='skipped'>" + totalCountSkipped + "</td>");
            htmlReport.append("</tr>");
            htmlReport.append("</tbody>");
            htmlReport.append("</table>");
//            htmlReport.append("<br></br> ");
            htmlReport.append("<h3>Execution Summary(Platforms):</h3>");

//            htmlReport.append("<table align='left'>");
            htmlReport.append("<table>");
            htmlReport.append("<tbody>");
            htmlReport.append("<tr class='summaryDetails'>");
            htmlReport.append("<th>Module</th>");
            if (isLocaleEnabled == null ||
                    isLocaleEnabled.equalsIgnoreCase("true")) {
                htmlReport.append("<th>Language-Locale</th>");
            }
            htmlReport.append("<th>Platform</th>");
            htmlReport.append("<th># PASS</th>");
            htmlReport.append("<th># FAIL</th>");
            htmlReport.append("<th># SKIPPED</th>");
            htmlReport.append("</tr>");

            moduleList.forEach((module) -> {
                List<LocaleLanguage> localeLanguageList = module.getLocaleLanguageList();
                int count = 0;
                for (LocaleLanguage localeLanguage : localeLanguageList) {
                    htmlReport.append("<tr>");
                    if (count == 0) {
                        htmlReport.append("<td class='moduleName'>" + module.getModuleName() + "</td>");
                    } else {
                        htmlReport.append("<td class='moduleName'></td>");
                    }

                    if (isLocaleEnabled == null ||
                            isLocaleEnabled.equalsIgnoreCase("true")) {
                        htmlReport.append("<td class='summarylocale'>" + localeLanguage.getLocaleLanguage() + "</td>");

                    }
                    htmlReport.append("<td class='summaryBrowser'>" + localeLanguage.getBrowser() + "</td>");
                    htmlReport.append("<td class='pass'>" + localeLanguage.getPass() + "</td>");
                    htmlReport.append("<td class='fail'>" + localeLanguage.getFail() + "</td>");
                    htmlReport.append("<td class='skipped'>" + localeLanguage.getSkipped() + "</td>");
                    htmlReport.append("</tr>");
                    count++;
                }

            });


            htmlReport.append("</tbody>");
            htmlReport.append("</table>");
//            htmlReport.append("<br></br>");
            htmlReport.append("<h3>Execution Details:</h3>");

            htmlReport.append("<table align='left'>");
            htmlReport.append("<tbody>");
            htmlReport.append("<tr>");
            htmlReport.append("<td><b>Module</b></td>");
            htmlReport.append("<td><b>Test Cases</b></td>");
            htmlReport.append("<td><b>Status</b></td>");
            htmlReport.append("<td><b>Platform</b></td>");
            if (isLocaleEnabled == null ||
                    isLocaleEnabled.equalsIgnoreCase("true")) {
                htmlReport.append("<td><b>Locale</b></td>");
            }

            htmlReport.append("<td><b>Description</b></td>");
            htmlReport.append("</tr>");

            htmlReport.append(getFailedTestCases());
            htmlReport.append(getSkippedTestCases());
            htmlReport.append(getPassedTestCases());

            htmlReport.append("</tbody>");
            htmlReport.append("</table>");
            htmlReport.append("<br/>");

            String dynamicBrTags = getBrHtmlTags();
            htmlReport.append(dynamicBrTags);
            htmlReport.append("Regards,");
            htmlReport.append("<br/>");
            htmlReport.append("Automation Team");
            htmlReport.append("<br/><br/>");
            htmlReport.append("<font color='grey'; size='2'>");
            htmlReport.append(
                    "Note: This is a system generated e-mail. Please do not reply.");
            //htmlReport.append("<a href='mailto:rdalmia@levi.com?subject=RE: Automation run'>Rohit Dalmia</a>");
            htmlReport.append("</font>");
            htmlReport.append("</body>");
            htmlReport.append("</html>");
            return htmlReport.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String getPassedTestCases() {
        String passedTestCase = "";
        List<Logger> loggerList = Reporting.getLoggerList();
        if (loggerList.size() > 0) {

            for (Logger logger : loggerList) {
                TestCaseData testCaseData = logger.getTestCaseData();
                if (testCaseData.getTestCaseStatus().equalsIgnoreCase("PASS")) {

                    if (isLocaleEnabled == null ||
                            isLocaleEnabled.equalsIgnoreCase("true")) {
                        passedTestCase += "<tr>"
                                + "<td>" + testCaseData.getTestCaseModule() + "</td>"
                                + "<td>" + testCaseData.getTestCaseName() + "</td>"
                                + "<td> PASS </td>"
                                + "<td> " + testCaseData.getBrowser() + " </td>"
                                + "<td> "
                                + testCaseData.getLocale()
                                + " </td>"
                                + "<td> "
                                + testCaseData.getDescription()
                                + " </td>" +
                                "</tr>";
                    } else {
                        passedTestCase += "<tr>"
                                + "<td>" + testCaseData.getTestCaseModule() + "</td>"
                                + "<td>" + testCaseData.getTestCaseName() + "</td>"
                                + "<td> PASS </td>"
                                + "<td> " + testCaseData.getBrowser() + " </td>"
                                + "<td> "
                                + testCaseData.getDescription()
                                + " </td>" +
                                "</tr>";
                    }


                }
            }

        } else {
            passedTestCase = "";
        }
        return passedTestCase;

    }


    private String getFailedTestCases() {
        String failedTestCase = "";
        List<Logger> loggerList = Reporting.getLoggerList();
        if (loggerList.size() > 0) {

            for (Logger logger : loggerList) {
                TestCaseData testCaseData = logger.getTestCaseData();
                if (testCaseData.getTestCaseStatus().equalsIgnoreCase("FAIL")) {
                    if (isLocaleEnabled == null ||
                            isLocaleEnabled.equalsIgnoreCase("true")) {
                        failedTestCase += "<tr>"
                                + "<td>" + testCaseData.getTestCaseModule() + "</td>"
                                + "<td>" + testCaseData.getTestCaseName() + "</td>"
                                + "<td> FAIL </td>"
                                + "<td> " + testCaseData.getBrowser() + " </td>"
                                + "<td> "
                                + testCaseData.getLocale()
                                + " </td>"
                                + "<td> "
                                + testCaseData.getDescription()
                                + " </td>"
                                + "</tr>";
                    } else {
                        failedTestCase += "<tr>"
                                + "<td>" + testCaseData.getTestCaseModule() + "</td>"
                                + "<td>" + testCaseData.getTestCaseName() + "</td>"
                                + "<td> FAIL </td>"
                                + "<td> " + testCaseData.getBrowser() + " </td>"
                                + "<td> "
                                + testCaseData.getDescription()
                                + " </td>"
                                + "</tr>";
                    }

                }
            }

        } else {
            failedTestCase = "";
        }
        return failedTestCase;

    }

    private String getSkippedTestCases() {
        String skippedTestCase = "";
        List<Logger> loggerList = Reporting.getLoggerList();
        if (loggerList.size() > 0) {

            for (Logger logger : loggerList) {
                TestCaseData testCaseData = logger.getTestCaseData();
                if (!(testCaseData.getTestCaseStatus().equalsIgnoreCase("PASS"))
                        && !(testCaseData.getTestCaseStatus().equalsIgnoreCase("FAIL"))) {

                    if (isLocaleEnabled == null ||
                            isLocaleEnabled.equalsIgnoreCase("true")) {
                        skippedTestCase += "<tr>"
                                + "<td>" + testCaseData.getTestCaseModule() + "</td>"
                                + "<td>" + testCaseData.getTestCaseName() + "</td>"
                                + "<td> SKIPPED </td>"
                                + "<td> " + testCaseData.getBrowser() + " </td>"
                                + "<td> "
                                + testCaseData.getLocale()
                                + " </td>"
                                + "<td> "
                                + testCaseData.getDescription()
                                + " </td>"
                                + "</tr>";
                    } else {
                        skippedTestCase += "<tr>"
                                + "<td>" + testCaseData.getTestCaseModule() + "</td>"
                                + "<td>" + testCaseData.getTestCaseName() + "</td>"
                                + "<td> SKIPPED </td>"
                                + "<td> " + testCaseData.getBrowser() + " </td>"
                                + "<td> "
                                + testCaseData.getDescription()
                                + " </td>"
                                + "</tr>";
                    }

                }
            }

        } else {
            skippedTestCase = "";
        }
        return skippedTestCase;

    }

    public void setSkippedTestCases(int skippedTestCases) {
        EmailReport.skippedTestCases = skippedTestCases;
    }

    /**
     * Generates "<br/>
     * " tags as per the count of total business workflow.
     *
     * @return tags
     */
    private String getBrHtmlTags() {
        int i;
        String tags = "";

        if (totalTestCases == 0) {
            for (i = 0; i < 3; i++) {
                tags += "<br/>";
            }
        } else if (totalTestCases <= 2) {
            for (i = 0; i < (totalTestCases * 3.3); i++) {
                tags += "<br/>";
            }
        } else if (totalTestCases > 2 && totalTestCases <= 5) {
            for (i = 0; i < (totalTestCases * 2.3); i++) {
                tags += "<br/>";
            }
        } else if (totalTestCases > 5 && totalTestCases <= 10) {
            for (i = 0; i < (totalTestCases * 2); i++) {
                tags += "<br/>";
            }
        } else if (totalTestCases > 10 && totalTestCases <= 15) {
            for (i = 0; i < (totalTestCases * 1.6); i++) {
                tags += "<br/>";
            }
        } else if (totalTestCases > 15) {
            for (i = 0; i < (totalTestCases * 1.5); i++) {
                tags += "<br/>";
            }
        }
        return tags;
    }

    public List<Module> getModuleMapping() {

        List<Logger> loggerList = Reporting.getLoggerList();
        List<Module> modules = new ArrayList<>();

        for (Logger logger : loggerList) {
            TestCaseData testCaseData = logger.getTestCaseData();
            String moduleName = testCaseData.getTestCaseModule();
            Module module = new Module(moduleName);
            if (modules.contains(module)) {
                module = modules.get(modules.indexOf(module));
            } else {


                modules.add(module);
            }

            List<LocaleLanguage> localeLanguageList = module.getLocaleLanguageList();
            String locale = testCaseData.getLocale();
            String browser = testCaseData.getBrowser();
            LocaleLanguage localeLang = new LocaleLanguage(locale, browser);
            if (localeLanguageList.contains(localeLang)) {
                localeLang = localeLanguageList.get(localeLanguageList.indexOf(localeLang));
            } else {
                localeLanguageList.add(localeLang);
            }


            if (testCaseData.getTestCaseStatus().equalsIgnoreCase("pass")) {
                localeLang.setPass(localeLang.getPass() + 1);
            } else if (testCaseData.getTestCaseStatus().equalsIgnoreCase("fail")) {
                localeLang.setFail(localeLang.getFail() + 1);

            } else if (testCaseData.getTestCaseStatus().equalsIgnoreCase("skipped")) {
                localeLang.setSkipped(localeLang.getSkipped() + 1);

            }
        }

        return modules;
    }
}
