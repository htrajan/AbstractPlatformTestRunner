package example.platform;

import example.caseobj.TestCase.TestAction;
import example.repository.Reader;
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
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static example.caseobj.TestCase.TestAction.Keyword.CLICK;
import static example.caseobj.TestCase.TestAction.Keyword.VERIFY_EITHER;

public class IosPlatform implements Platform {

    private final Map<String, By> objectLocator;

    private IOSDriver<IOSElement> driver;
    private IOSElement foundElement;

    public IosPlatform(DeviceType deviceType, Reader reader) throws IOException {
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
        objectLocator = reader.getObjects();

        try {
            driver = new IOSDriver<>(new URL(url), capabilities);
        } catch (MalformedURLException e) {
            driver = new IOSDriver<>(capabilities);
        }
    }

    private DesiredCapabilities getCapabilities(DeviceType deviceType) throws IOException {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("Platform Properties/iOS-base.properties"));

        switch (deviceType) {
            case SAUCE_LABS:
                properties.load(getClass().getClassLoader().getResourceAsStream("Platform Properties/iOS-sauce-labs.properties"));
                break;
            case SIMULATOR:
                properties.load(getClass().getClassLoader().getResourceAsStream("Platform Properties/iOS-simulator.properties"));
                break;
        }
        properties.stringPropertyNames().forEach(name -> capabilities.setCapability(name, properties.get(name)));
        return capabilities;
    }

    @Override
    public boolean executeTestAction(TestAction action) {
        switch (action.getKeyword()) {
            case GOTO:
                driver.get(action.getParams().get(0));
                break;
            case SET_TEXT:
                driver.getKeyboard().sendKeys(action.getParams().get(0));
                break;
            case CLICK:
                String name = action.getParams().get(0);
                WebElement e = name.equals("foundElement") ? getFoundElement().orElseThrow(IllegalArgumentException::new) : getElementByName(name);
                e.click();
                break;
            case CLICK_SEQUENTIALLY:
                try {
                    executeTestAction(new TestAction(CLICK, action.getParams()));
                } catch (Exception ignored) { }
                try {
                    executeTestAction(new TestAction(CLICK, action.getParams().subList(1, action.getParams().size())));
                } catch (Exception ignored) { }
                break;
            case CHECK_ELEMENT:
            case SCROLL_TO:
                break;
            case WAIT_FOR:
                String objectName = action.getParams().get(0);
                new WebDriverWait(driver, 30).until(ExpectedConditions.presenceOfElementLocated(objectLocator.get(objectName)));
                break;
            case VERIFY_EITHER:
                int waitDurationInMillis = (Integer) driver.getCapabilities().getCapability("maxDuration");
                try {
                    objectName = action.getParams().get(0);
                    new WebDriverWait(driver, waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(objectLocator.get(objectName)));
                    foundElement = getElementByName(objectName);
                } catch (Exception exception) {
                    objectName = action.getParams().get(1);
                    new WebDriverWait(driver, waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(objectLocator.get(objectName)));
                    foundElement = getElementByName(objectName);
                }
                break;
            case VERIFY_TRIPLE:
                TestAction verifyEither = new TestAction(VERIFY_EITHER, action.getParams());
                waitDurationInMillis = Integer.parseInt(driver.getCapabilities().getCapability("maxDuration").toString());
                try {
                    executeTestAction(verifyEither);
                } catch (Exception exception) {
                    objectName = action.getParams().get(2);
                    new WebDriverWait(driver, waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(objectLocator.get(objectName)));
                    foundElement = getElementByName(objectName);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action.getKeyword());
        }
        return true;
    }

    @Override
    public void cleanUp() throws InterruptedException {
        Thread.sleep(5_000L);
        driver.quit();
    }

    @Override
    public String getPlatformName() {
        return "iOS";
    }

    @Override
    public IOSElement getElementByName(String name) {
        return driver.findElement(objectLocator.get(name));
    }

    public Optional<IOSElement> getFoundElement() {
        return Optional.ofNullable(foundElement);
    }
}
