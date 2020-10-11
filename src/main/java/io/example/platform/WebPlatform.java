package io.example.platform;

import io.example.repository.Reader;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class WebPlatform extends AbstractWebPlatform {

    private final ChromeDriver driver;

    public WebPlatform(Reader reader) {
        super(reader);
        this.driver = new ChromeDriver();
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
