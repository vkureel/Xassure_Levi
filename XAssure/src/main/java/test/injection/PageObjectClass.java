package test.injection;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.xassure.framework.driver.Controls;

public class PageObjectClass {

	private Controls control;

	@Inject
	public PageObjectClass(@Named("web") Controls controls) {
		this.control = controls;
	}

	public void testmethod() {
		control.enterText("", "", "");
		control.quitDriver();
	}
}
