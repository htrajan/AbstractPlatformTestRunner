package example.platform;

import example.caseobj.TestCase;
import example.repository.Reader;
import example.repository.RepositoryReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static example.caseobj.TestCase.TestAction.Keyword.VERIFY_EITHER;

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
