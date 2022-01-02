package io.example.platform;

import io.example.caseobj.TestCase.TestAction;
import io.example.componentfinder.XlsAliasComponentFinder;
import org.openqa.selenium.WebElement;

import java.util.Optional;

public interface Platform {

    enum DeviceType {
        SAUCE_LABS,
        SIMULATOR
    }

    String getPlatformName();

    WebElement getElementByName(String name, Optional<XlsAliasComponentFinder> componentFinder);

    boolean executeTestAction(TestAction action, Optional<XlsAliasComponentFinder> componentFinder);

    void cleanUp() throws InterruptedException;

}
