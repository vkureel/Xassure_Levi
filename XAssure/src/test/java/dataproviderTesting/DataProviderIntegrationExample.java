package dataproviderTesting;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataProviderIntegrationExample {

    @BeforeMethod
    public void beforeMethod() {
        long threadID = Thread.currentThread().getId();
        System.out.println("I'm in before method " + threadID);

    }

    @Test(dataProvider = "TestType",
            dataProviderClass = DataProviderTest.class)
    public void integrationTest(String data) {

        System.out.println("Integration testing: Data(" + data + ")");
        long threadID = Thread.currentThread().getId();
        System.out.println("In method " + threadID);
    }

    @AfterMethod
    public void afterMethod() {


    }
}
