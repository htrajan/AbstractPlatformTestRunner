package example.platform;

import example.caseobj.TestCase;
import example.repository.Reader;
import example.repository.RepositoryReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Map;

import static example.caseobj.TestCase.TestAction.Keyword.VERIFY_EITHER;

public abstract class AbstractWebPlatform implements Platform {

    private Map<String, By> objectLocator;

    abstract RemoteWebDriver getDriver();
    private WebElement currentElement = null;

    public AbstractWebPlatform(Reader reader) {
        objectLocator = reader.getObjects();
    }

    @Override
    public WebElement getElementByName(String name) {
        return getDriver().findElement(objectLocator.get(name));
    }

    @Override
    public boolean executeTestAction(TestCase.TestAction action) {
        switch (action.getKeyword()) {
            case GOTO:
                getDriver().get(action.getParams().get(0));
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
                new WebDriverWait(getDriver(), 30).until(ExpectedConditions.presenceOfElementLocated(objectLocator.get(objectName)));
                break;
            case VERIFY_EITHER:
                int waitDurationInMillis = (Integer) getDriver().getCapabilities().getCapability("maxDuration");
                try {
                    objectName = action.getParams().get(0);
                    new WebDriverWait(getDriver(), waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(objectLocator.get(objectName)));
                } catch (Exception exception) {
                    objectName = action.getParams().get(1);
                    new WebDriverWait(getDriver(), waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(objectLocator.get(objectName)));
                }
                break;
            case VERIFY_TRIPLE:
                TestCase.TestAction verifyEither = new TestCase.TestAction(VERIFY_EITHER, action.getParams());
                waitDurationInMillis = (Integer) getDriver().getCapabilities().getCapability("maxDuration");
                try {
                    executeTestAction(verifyEither);
                } catch (Exception exception) {
                    objectName = action.getParams().get(2);
                    new WebDriverWait(getDriver(), waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(objectLocator.get(objectName)));
                }
                break;
        }
        return true;
    }

    @Override
    public void cleanUp() throws InterruptedException {
        Thread.sleep(5_000L);
        getDriver().quit();
    }

}
