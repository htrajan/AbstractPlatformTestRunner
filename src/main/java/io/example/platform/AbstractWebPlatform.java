package io.example.platform;

import io.example.caseobj.TestCase;
import io.example.componentfinder.HtmlComponentFinder;
import io.example.componentfinder.XlsAliasComponentFinder;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractWebPlatform implements Platform {

    abstract RemoteWebDriver getDriver();
    private WebElement currentElement = null;
    private String currentUrl = null;
    private final Map<String, Map<String, By>> urlToXpathLookup = new HashMap<>();

    @Override
    public WebElement getElementByName(String name, Optional<XlsAliasComponentFinder> componentFinder) {
        return getDriver().findElement(getBy(name, componentFinder));
    }

    @Override
    public boolean executeTestAction(TestCase.TestAction action, Optional<XlsAliasComponentFinder> componentFinder) {
        String firstValue = action.getParams().size() > 0 ? action.getParams().get(0) : "";
        switch (action.getKeyword()) {
            case GOTO:
                currentUrl = firstValue;
                urlToXpathLookup.put(firstValue, HtmlComponentFinder.getNameToComponentIdentifierMap(firstValue));
                getDriver().get(firstValue);
                break;
            case SET_TEXT:
                currentElement.sendKeys(firstValue);
                break;
            case HIT_ENTER_KEY:
                currentElement.sendKeys(Keys.ENTER);
                break;
            case CLICK:
                WebElement e = getElementByName(firstValue, componentFinder);
                e.click();
                currentElement = e;
                break;
            case CLICK_SEQUENTIALLY:
            case SCROLL_TO:
                break;
            case CHECK_ELEMENT:
            case WAIT_FOR:
                String objectName = firstValue;
                String currentDriverUrl = getDriver().getCurrentUrl();
                if (!currentDriverUrl.equals(currentUrl)) {
                    this.currentUrl = currentDriverUrl;
                    urlToXpathLookup.put(this.currentUrl, HtmlComponentFinder.getNameToComponentIdentifierMap(getDriver()));
                }
                try {
                    new WebDriverWait(getDriver(), 30).until(ExpectedConditions.presenceOfElementLocated(getBy(objectName, componentFinder)));
                } catch (Exception ex) {
                    try {
                        Thread.sleep(5_000);
                    } catch (InterruptedException exc) {
                        exc.printStackTrace();
                    }
                    currentDriverUrl = getDriver().getCurrentUrl();
                    if (!currentDriverUrl.equals(currentUrl)) {
                        this.currentUrl = currentDriverUrl;
                        urlToXpathLookup.put(this.currentUrl, HtmlComponentFinder.getNameToComponentIdentifierMap(getDriver()));
                    }
                    new WebDriverWait(getDriver(), 30).until(ExpectedConditions.presenceOfElementLocated(getBy(objectName, componentFinder)));
                }
                break;
            case VERIFY_EITHER:
                int waitDurationInMillis = (Integer) getDriver().getCapabilities().getCapability("maxDuration");
                try {
                    objectName = firstValue;
                    new WebDriverWait(getDriver(), waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(getBy(objectName, componentFinder)));
                } catch (Exception exception) {
                    objectName = action.getParams().get(1);
                    new WebDriverWait(getDriver(), waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(getBy(objectName, componentFinder)));
                }
                break;
            case VERIFY_TRIPLE:
                TestCase.TestAction verifyEither = new TestCase.TestAction(TestCase.TestAction.Keyword.VERIFY_EITHER, action.getParams());
                waitDurationInMillis = (Integer) getDriver().getCapabilities().getCapability("maxDuration");
                try {
                    executeTestAction(verifyEither, componentFinder);
                } catch (Exception exception) {
                    objectName = action.getParams().get(2);
                    new WebDriverWait(getDriver(), waitDurationInMillis / 1000).until(ExpectedConditions.presenceOfElementLocated(getBy(objectName, componentFinder)));
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

    private By getBy(String name, Optional<XlsAliasComponentFinder> componentFinder) {
        return componentFinder
            .map(finder -> finder.aliasToComponentIdentifierMap().get(name))
            .orElseGet(() -> urlToXpathLookup.get(currentUrl).get(name));
    }

}
