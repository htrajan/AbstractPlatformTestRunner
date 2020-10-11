package io.example.platform;

import io.example.caseobj.TestCase.TestAction;
import org.openqa.selenium.WebElement;

public interface Platform {

    enum DeviceType {
        SAUCE_LABS,
        SIMULATOR
    }

    String getPlatformName();

    WebElement getElementByName(String name);

    boolean executeTestAction(TestAction action);

    void cleanUp() throws InterruptedException;

}
