package io.example.platform;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class WebPlatform extends AbstractWebPlatform {

    private final ChromeDriver driver;

    public WebPlatform() {
        this.driver = new ChromeDriver();
    }

    public WebPlatform(ChromeOptions options) {
        this.driver = new ChromeDriver(options);
    }

    @Override
    RemoteWebDriver getDriver() {
        return driver;
    }

    @Override
    public String getPlatformName() {
        return "Web";
    }
}
