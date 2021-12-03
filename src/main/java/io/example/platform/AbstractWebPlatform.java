package io.example.platform;

import io.example.caseobj.TestCase;
import io.example.xpath.HtmlXPathParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWebPlatform implements Platform {

    abstract RemoteWebDriver getDriver();
    private WebElement currentElement = null;
    private String currentUrl = null;
    private final Map<String, Map<String, By>> urlToXpathLookup = new HashMap<>();

    @Override
    public WebElement getElementByName(String name) {
        return getDriver().findElement(getBy(name));
    }

    @Override
    public boolean executeTestAction(TestCase.TestAction action) {
        String firstValue = action.getParams().get(0);
        switch (action.getKeyword()) {
            case GOTO:
                currentUrl = firstValue;
                urlToXpathLookup.put(firstValue, HtmlXPathParser.getNameToXPathMap(firstValue));
                getDriver().get(firstValue);
                break;
            case SET_TEXT:
                currentElement.sendKeys(firstValue);
                break;
            case CLICK:
                WebElement e = getElementByName(firstValue);
                e.click();
                currentElement = e;
                break;
            case CHECK_ELEMENT:
            case SCROLL_TO:
                break;
            case WAIT_FOR:
                String objectName = firstValue;
                String currentDriverUrl = getDriver().getCurrentUrl();
                if (!currentDriverUrl.equals(currentUrl)) {
                    this.currentUrl = currentDriverUrl;
                    urlToXpathLookup.put(this.currentUrl, HtmlXPathParser.getNameToXPathMap(getDriver()));
                }
                new WebDriverWait(getDriver(), 30).until(ExpectedConditions.presenceOfElementLocated(getBy(objectName)));
                break;
            case VERIFY_EITHER:
                int waitDurationInMillis = (Integer) getDriver().getCapabilities().getCapability("maxDuration");
                try {
                    objectName = firstValue;
                    new WebDriverWait(getDriver(), waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(getBy(objectName)));
                } catch (Exception exception) {
                    objectName = action.getParams().get(1);
                    new WebDriverWait(getDriver(), waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(getBy(objectName)));
                }
                break;
            case VERIFY_TRIPLE:
                TestCase.TestAction verifyEither = new TestCase.TestAction(TestCase.TestAction.Keyword.VERIFY_EITHER, action.getParams());
                waitDurationInMillis = (Integer) getDriver().getCapabilities().getCapability("maxDuration");
                try {
                    executeTestAction(verifyEither);
                } catch (Exception exception) {
                    objectName = action.getParams().get(2);
                    new WebDriverWait(getDriver(), waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(getBy(objectName)));
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

    private By getBy(String name) {
        return urlToXpathLookup.get(currentUrl).get(name);
    }

}
