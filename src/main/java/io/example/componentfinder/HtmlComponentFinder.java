package io.example.componentfinder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

public class HtmlComponentFinder {

// To generalize beyond 'input' tags:

//    enum Type {
//        INPUT,
//        SPAN,
//        H1,H2,H3
//    }

// public static Map<Type, Map<String, By>> getNameToXPathMap(String url)

    public static Map<String, By> getNameToComponentIdentifierMap(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.selectXpath("//input").stream()
                .filter(not(HtmlComponentFinder::isElementHidden))
                .collect(toMap(HtmlComponentFinder::getNameForMap, HtmlComponentFinder::getXPathForMap));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void outputComponentInfo(String url) {
        AtomicInteger counter = new AtomicInteger();
        try {
            RemoteWebDriver driver = new ChromeDriver();
            driver.get(url);
            driver
                .findElementsByTagName("a")
                .forEach(element -> visitElement(counter, element));
            driver
                .findElementsByTagName("input")
                .forEach(element -> visitElement(counter, element));
            driver
                .findElementsByTagName("label")
                .forEach(element -> visitElement(counter, element));
            driver
                .findElementsByTagName("button")
                .forEach(element -> visitElement(counter, element));
            driver
                .findElementsByTagName("select")
                .forEach(element -> visitElement(counter, element));
            driver.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void visitElement(AtomicInteger counter, WebElement element) {
        String componentInfo = getComponentInfo(element);
        if (!componentInfo.isEmpty()) {
            System.out.printf("%d. (%s) %s\n",
                counter.incrementAndGet(),
                element.getTagName(),
                componentInfo);
        }
    }

    private static String getComponentInfo(WebElement element) {
        StringBuilder sb = new StringBuilder();
        String name = element.getAttribute("name");
        String key = null;
        if (name != null && !name.isEmpty()) {
            sb.append("name: ").append(name).append(' ');
            key = name;
        }
        String text = element.getText();
        if (text != null && !text.isEmpty()) {
            sb.append("text: ").append(text.trim()).append(' ');
            if (key == null) {
                key = text.trim();
            }
        }
        String id = element.getAttribute("id");
        if (id != null && !id.isEmpty()) {
            sb.append("id: ").append(id).append(' ');
            if (key == null) {
                key = id;
            }
        }
        String cssClass = element.getAttribute("class");
        if (cssClass != null && !cssClass.isEmpty()) {
            sb.append("class: ").append(cssClass).append(' ');
            if (key == null) {
                key = cssClass;
            }
        }
        if (key != null) {
            String keyString = "\"key: " + key + "\" ";
            sb.insert(0, keyString);
        }
        return sb.toString();
    }

    // CL submit button //button[@type='submit']

    public static Map<String, By> getNameToComponentIdentifierMap(RemoteWebDriver driver) {
        List<WebElement> inputs = driver.findElementsByTagName("input");
        Map<String, By> toReturn = new HashMap<>(inputs.stream()
            .filter(webElement -> webElement.isDisplayed() && webElement.isEnabled())
            .collect(toMap(
                webElement -> !webElement.getAttribute("name").isEmpty() ?
                    webElement.getAttribute("name") : webElement.getAttribute("id"),
                webElement -> By.id(webElement.getAttribute("id")))));
        toReturn.put("submit", By.xpath("//button[@type='submit']"));
        return toReturn;
    }

    private static String getNameForMap(Element element) {
        return element.hasAttr("name") ? element.attr("name") : element.attr("id");
    }

    private static By getXPathForMap(Element element) {
        return By.xpath(String.format("//input[@id='%s']", element.attr("id")));
    }

    private static boolean isElementHidden(Element element) {
        return element.hasAttr("type") && element.attr("type").equals("hidden");
    }

    public static void main(String[] args) {
        outputComponentInfo(args[0]);
    }

}
