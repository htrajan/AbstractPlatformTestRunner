package example.platform;

import example.caseobj.TestCase.TestAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IosPlatform implements Platform {

    private static final Map<String, String> NAME_TO_XPATH = new HashMap<>();

    static {
        NAME_TO_XPATH.put("getStarted", "//*[@name='Get Started']");
        NAME_TO_XPATH.put("signInWithEmail", "//*[@name='Sign In With Email']");
        NAME_TO_XPATH.put("username", "//*[@name='Email']");
        NAME_TO_XPATH.put("password", "//*[@name='Password']");
        NAME_TO_XPATH.put("username_input", "//*[@class='UIATextField']");
        NAME_TO_XPATH.put("password_input", "//*[@text='Password' and @class='UIAStaticText']");
        NAME_TO_XPATH.put("loginButton", "//XCUIElementTypeButton[@name=\"Sign In\"]");
    }

    private IOSDriver<IOSElement> driver;

    public IosPlatform(DeviceType deviceType) throws IOException {
        String url;

        switch (deviceType) {
            case SAUCE_LABS:
                url = "https://JiffJenkins:a3031573-3308-4838-980c-6f4c92b44622@ondemand.saucelabs.com:443/wd/hub";
                break;
            case SIMULATOR:
            default:
                url = "http://0.0.0.0:4723/wd/hub";
                break;
        }

        DesiredCapabilities capabilities = getCapabilities(deviceType);

        try {
            driver = new IOSDriver<>(new URL(url), capabilities);
        } catch (MalformedURLException e) {
            driver = new IOSDriver<>(capabilities);
        }
    }

    private DesiredCapabilities getCapabilities(DeviceType deviceType) throws IOException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
//        capabilities.setCapability("maxDuration", 5000);
//        capabilities.setCapability("automationName", "XCUITest");
//        capabilities.setCapability("newCommandTimeout", 180);
//        capabilities.setCapability("useNewWDA", true);
//        capabilities.setCapability("platformName", "iOS");
//        capabilities.setCapability("deviceOrientation", "portrait");
//        capabilities.setCapability("webkitResponseTimeout", 500);
//        capabilities.setCapability("wdaEventloopIdleDelay",3);
//        capabilities.setCapability("autoWebView", true);
//        capabilities.setCapability("startIWDP", true);
//        capabilities.setCapability("autoAcceptAlerts", true);
//        capabilities.setCapability("idleTimeout", 1000);
//
//        capabilities.setCapability("appiumVersion", "1.18.0");
//        capabilities.setCapability("browserName", "");
//        capabilities.setCapability("safariInitialUrl", "");
//        capabilities.setCapability("name", "login-test");
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("iOS-base.properties"));

        switch (deviceType) {
            case SAUCE_LABS:
//                capabilities.setCapability("app", "sauce-storage:" + "Castlight.zip");
//                capabilities.setCapability("deviceName", "iPhone 11");
//                capabilities.setCapability("platformVersion", "13.0");
                properties.load(getClass().getClassLoader().getResourceAsStream("iOS-sauce-labs.properties"));
                break;
            case SIMULATOR:
//                capabilities.setCapability("udid", "B6847845-B77A-4D5F-9054-3451898065AD");
//                capabilities.setCapability("app", "/Users/riyengar/Documents/Castlight.app");
//                capabilities.setCapability("deviceName", "iPhone 8 Plus");
//                capabilities.setCapability("platformVersion", "13.6");
                properties.load(getClass().getClassLoader().getResourceAsStream("iOS-simulator.properties"));
                break;
        }
        properties.stringPropertyNames().forEach(name -> capabilities.setCapability(name, properties.get(name)));
        return capabilities;
    }

    @Override
    public boolean executeTestAction(TestAction action) {
        switch (action.getKeyword()) {
            case GOTO:
                driver.get(action.getSecondParam().orElseThrow(IllegalArgumentException::new));
                break;
            case SET_TEXT:
                driver.getKeyboard().sendKeys(action.getSecondParam().orElseThrow(IllegalArgumentException::new));
                break;
            case CLICK:
                WebElement e = getElementByName(action.getFirstParam().orElseThrow(IllegalArgumentException::new));
                e.click();
                break;
            case CHECK_ELEMENT:
            case SCROLL_TO:
                break;
            case WAIT_FOR:
                String objectName = action.getFirstParam().orElseThrow(IllegalArgumentException::new);
                new WebDriverWait(driver, 30).until(ExpectedConditions.presenceOfElementLocated(By.xpath(NAME_TO_XPATH.get(objectName))));
                break;
            case VERIFY_EITHER:
                int waitDurationInMillis = (Integer) driver.getCapabilities().getCapability("maxDuration");
                try {
                    objectName = action.getFirstParam().orElseThrow(IllegalArgumentException::new);
                    new WebDriverWait(driver, waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(By.xpath(NAME_TO_XPATH.get(objectName))));
                } catch (Exception exception) {
                    objectName = action.getSecondParam().orElseThrow(IllegalArgumentException::new);
                    new WebDriverWait(driver, waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(By.xpath(NAME_TO_XPATH.get(objectName))));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action.getKeyword());
        }
        return true;
    }

    @Override
    public void cleanUp() throws InterruptedException {
        Thread.sleep(15_000L);
        driver.quit();
    }

    @Override
    public String getPlatformName() {
        return "iOS";
    }

    @Override
    public IOSElement getElementByName(String name) {
        return driver.findElement(By.xpath(NAME_TO_XPATH.get(name)));
    }
}
