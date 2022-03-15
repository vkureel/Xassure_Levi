package test.injection;

import org.openqa.selenium.interactions.Actions;

import com.galenframework.reports.GalenTestInfo;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.xassure.framework.driver.Controls;
import com.xassure.galen.GalenDriver;
import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Reporting;

public class LoginPage {
	private Controls control;
	GalenDriver _galenDriver = new GalenDriver();

	@Inject
	public LoginPage(@Named("web") Controls control) {
		this.control = control;
	}

	private String txtBox_username = "xpath~//input[@name='login.username']";
	private String txtBox_password = "xpath~//input[@name='login.password']";
	private String btn_login = "xpath~//button[contains(text(),'Login')]";
	private String header_myNotes = "xpath~//h2[contains(text(),'My Notes')]";

	public GalenTestInfo login(String username, String password, String filename) {
		GalenTestInfo myNotePageTest = null;
		try {
//			control.enterText("txtBox_username", txtBox_username, username);
//			control.enterText("txtBox_password", txtBox_password, password);
//
//			control.click("Clicking on btn_login", btn_login);

			if (control.waitUntilElementIsVisible("header_myNotes",header_myNotes, 100)) {
				Reporting.getLogger().log(LogStatus.PASS, "Successfully logged into the application");

				myNotePageTest = _galenDriver.verifyUI(control.getDriver(), filename);
			} else {
				Reporting.getLogger().log(LogStatus.FAIL, "Login failed");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return myNotePageTest;
	}

}
