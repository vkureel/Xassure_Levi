//package test.injection;
//
//import org.junit.Assert;
//import org.testng.annotations.Test;
//import com.xassure.api.model.users.UsersParent;
//import com.xassure.api.services.UsersService;
//import com.xassure.framework.driver.EnvironmentSetup;
//import com.xassure.reporting.logger.LogStatus;
//import com.xassure.reporting.logger.Reporting;
//
//public class ApiTest extends EnvironmentSetup {
//
//	UsersService users;
//	UsersParent userData;
//	
//	@Test
//	public void UsersApiSampleTest() {
//
//		users = injector.getInstance(UsersService.class);
//		userData = users.retrieveParentAccount("https://jsonplaceholder.typicode.com/users/1");
//		Assert.assertEquals(userData.getName(),"Leanne Graham");
//		Reporting.getLogger().log(LogStatus.PASS, "ASSERTION PASSED: Users API is successfully validated against user name value");
//
//	}
//
//}
