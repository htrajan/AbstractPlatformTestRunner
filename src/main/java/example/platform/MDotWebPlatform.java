package example.platform;

import example.caseobj.TestCase;
import example.repository.RepositoryReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import static example.caseobj.TestCase.TestAction.Keyword.VERIFY_EITHER;
import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;

public class MDotWebPlatform implements Platform {

    private static final Map<String, By> objectLocator = RepositoryReader.readObjectRepository();

    private final RemoteWebDriver driver;
    private WebElement currentElement = null;

    public MDotWebPlatform() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("iOS-base.properties"));
        properties.load(getClass().getClassLoader().getResourceAsStream("iOS-mdot-simulator.properties"));
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", BrowserType.SAFARI);
        //capabilities.setCapability(PLATFORM_NAME, "iOS");
        //capabilities.setCapability("", "/Applications/Google Chrome.app/Contents/MacOS/");
        //capabilities.setCapability("chromedriverExecutableDir", "/usr/local/bin");
        properties.stringPropertyNames().forEach(name -> capabilities.setCapability(name, properties.get(name)));
        this.driver = new RemoteWebDriver(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);
        //this.driver = new RemoteWebDriver(new URL("https://m.us.castlighthealth.com"), capabilities);
    }

    @Override
    public String getPlatformName() {
        return "Web";
    }

    @Override
    public WebElement getElementByName(String name) {
        return driver.findElement(objectLocator.get(name));
    }

    @Override
    public boolean executeTestAction(TestCase.TestAction action) {
        switch (action.getKeyword()) {
            case GOTO:
                driver.get(action.getParams().get(0));
                break;
            case SET_TEXT:
                currentElement.sendKeys(action.getParams().get(0));
                break;
            case CLICK:
                WebElement e = getElementByName(action.getParams().get(0));
                e.click();
                currentElement = e;
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
                } catch (Exception exception) {
                    objectName = action.getParams().get(1);
                    new WebDriverWait(driver, waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(objectLocator.get(objectName)));
                }
                break;
            case VERIFY_TRIPLE:
                TestCase.TestAction verifyEither = new TestCase.TestAction(VERIFY_EITHER, action.getParams());
                waitDurationInMillis = (Integer) driver.getCapabilities().getCapability("maxDuration");
                try {
                    executeTestAction(verifyEither);
                } catch (Exception exception) {
                    objectName = action.getParams().get(2);
                    new WebDriverWait(driver, waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(objectLocator.get(objectName)));
                }
                break;
        }
        return true;
    }

    @Override
    public void cleanUp() throws InterruptedException {
        Thread.sleep(15_000L);
        driver.quit();
    }
}
