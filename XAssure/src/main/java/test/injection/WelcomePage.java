package test.injection;

import com.galenframework.reports.GalenTestInfo;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.xassure.framework.driver.Controls;
import com.xassure.galen.GalenDriver;
import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Reporting;

public class WelcomePage {
	Controls control;
	GalenDriver _galenDriver = new GalenDriver();

	private String btn_login = "xpath~//button[contains(text(),'Login')]";
	private String header_login = "xpath~//h2[contains(text(),'Login')]";

	@Inject
	public WelcomePage(@Named("web") Controls control) {
		this.control = control;
	}

	public GalenTestInfo launchApp(String url, String filename) {
		GalenTestInfo test = null;
		try {
			control.launchApplication(url);
			String pageTitle = control.getPageTitle();

			if (pageTitle.equalsIgnoreCase("Sample Website for Galen Framework")) {
				Reporting.getLogger().log(LogStatus.PASS, "Successfully launched application");
				test = _galenDriver.verifyUI(control.getDriver(), filename);
			} else {
				Reporting.getLogger().log(LogStatus.FAIL, "Failed to load application");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return test;
	}

	public GalenTestInfo navigateToLoginPage(String filename) {
		GalenTestInfo loginPageTest = null;
		try {
//			control.click("btn_login", btn_login);
			System.out.println();
			if (control.waitUntilElementIsVisible("header_login",header_login, 100)) {
				Reporting.getLogger().log(LogStatus.PASS, "Landed on login page");
				loginPageTest = _galenDriver.verifyUI(control.getDriver(), filename);
			} else {
				Reporting.getLogger().log(LogStatus.FAIL, "Login page not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loginPageTest;
	}

}
