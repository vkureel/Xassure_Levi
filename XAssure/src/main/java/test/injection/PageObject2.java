package test.injection;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.xassure.framework.driver.Controls;
import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Reporting;

public class PageObject2 {

	private Controls control;

	private String textbox_search = "linkText~Gmail|xpath~//a[@class='gb_d']|xpath~//a[contains(text(),'Gmail')]|id~gmail";

	@Inject
	public PageObject2(@Named("web") Controls controls) {
		this.control = controls;
	}

	public void testmethod() {
		control.launchApplication("https://www.google.com");
		Reporting.getLogger().log(LogStatus.PASS, "Test Description");
		control.click("textbox_search", textbox_search);
	}
}
