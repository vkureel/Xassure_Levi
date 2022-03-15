package com.xassure.reporting.utilities;

import com.xassure.reporting.logger.Reporting;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class ScreenshotHandler {

    public static synchronized String captureScreenShot(WebDriver driver) {
        try {
            String screenShotsLocation = Reporting.getScreenshotLoc();

            String fileName = new Reporting().generateUniqueRunId() + ".png";
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(source, new File(screenShotsLocation + "/" + fileName));
            return fileName;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }
}
