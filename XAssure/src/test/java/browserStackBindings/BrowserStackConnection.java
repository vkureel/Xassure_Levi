package browserStackBindings;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.browserstack.local.Local;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;


public class BrowserStackConnection {

    public static void main(String[] args) throws Exception {

        BrowserStackConnection browserStackConnection = new BrowserStackConnection();
        WebDriver driver = browserStackConnection.setBrowserStackBrowserCapability();
        driver.get("https://sprint-400.levi-site.com/US/en_US?nextgen=true");
    }

    public void startConnection() throws Exception {

//    #creates an instance of Local
        Local bsLocal = new Local();

//#replace<browserstack -accesskey > with your key.You can also set an environment variable - "BROWSERSTACK_ACCESS_KEY".
        HashMap<String, String> bsLocalArgs = new HashMap<String, String>();
        bsLocalArgs.put("key", "XTx16Kxt7ai21bViueh4");

//#starts the Local instance with the required arguments
        bsLocal.start(bsLocalArgs);

//#check if BrowserStack local instance is running
        System.out.println(bsLocal.isRunning());

//#stop the Local instance
        bsLocal.stop();
    }

    public WebDriver setBrowserStackBrowserCapability() {
        final String USERNAME = "kapilchawla2";
        final String AUTOMATE_KEY = "XTx16Kxt7ai21bViueh4";
        final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
        WebDriver driver;
        try {
            driver = new RemoteWebDriver(new URL(URL), setDesiredBSCapabilities());
            driver.get("https://levi.com");
            return driver;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private DesiredCapabilities setDesiredBSCapabilities() {
        JSONParser parser = new JSONParser();
//        DesiredCapabilities capabilities = new DesiredCapabilities();
        JSONObject config;
        try {
//            config = (JSONObject) parser.parse(
//                    new FileReader("src/test/resources/storefront/config/browserstack/" + System.getProperty("bsConfigFile")));
//            Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
//            Iterator it = commonCapabilities.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry pair = (Map.Entry) it.next();
//                if (capabilities.getCapability(pair.getKey().toString()) == null) {
//                    capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
//                }
//            }
//            return capabilities;

//            DesiredCapabilities caps = new DesiredCapabilities();
//            caps.setCapability("os", "Windows");
//            caps.setCapability("os_version", "10");
//            caps.setCapability("browser", "Chrome");
//            caps.setCapability("browser_version", "83.0");
//            caps.setCapability("browserstack.local", "true");
//            caps.setCapability("browserstack.selenium_version", "3.5.2");
            DesiredCapabilities caps = new DesiredCapabilities();
//            caps.setCapability("browser", "Chrome");
//            caps.setCapability("browser_version", "83.0");
//            caps.setCapability("os", "OS X");
//            caps.setCapability("os_version", "Catalina");
//            caps.setCapability("resolution", "1024x768");
//            caps.setCapability("name", "Bstack-[Java] Sample Test");
//            caps.setCapability("browserstack.local", "true");
//            caps.setCapability("forcelocal", "true");

            caps.setCapability("os_version", "12");
            caps.setCapability("device", "iPhone XS");
            caps.setCapability("browserstack.appium_version", "1.17.0");
            caps.setCapability("browser", "Chrome");
            caps.setCapability("browser_version", "83.0");
            caps.setCapability("name", "Bstack-[Java] Sample Test");
            caps.setCapability("browserstack.local", "true");
            caps.setCapability("forcelocal", "true");

            return caps;
        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
