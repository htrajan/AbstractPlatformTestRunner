package io.example.platform;

import io.example.repository.Reader;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class MDotWebPlatform extends AbstractWebPlatform {

    private final RemoteWebDriver driver;

    public MDotWebPlatform(Reader reader) throws IOException {
        super(reader);
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("Platform Properties/iOS-base.properties"));
        properties.load(getClass().getClassLoader().getResourceAsStream("Platform Properties/iOS-mdot-simulator.properties"));
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", BrowserType.SAFARI);
        properties.stringPropertyNames().forEach(name -> capabilities.setCapability(name, properties.get(name)));
        this.driver = new RemoteWebDriver(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);
    }

    @Override
    RemoteWebDriver getDriver() {
        return driver;
    }

    @Override
    public String getPlatformName() {
        return "MDot";
    }
}
