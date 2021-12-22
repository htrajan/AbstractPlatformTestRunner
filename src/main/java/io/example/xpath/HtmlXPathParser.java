package io.example.xpath;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

public class HtmlXPathParser {

// To generalize beyond 'input' tags:

//    enum Type {
//        INPUT,
//        SPAN,
//        H1,H2,H3
//    }

// public static Map<Type, Map<String, By>> getNameToXPathMap(String url)

    public static Map<String, By> getNameToXPathMap(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.selectXpath("//input").stream()
                .filter(not(HtmlXPathParser::isElementHidden))
                .collect(toMap(HtmlXPathParser::getNameForMap, HtmlXPathParser::getXPathForMap));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // CL submit button //button[@type='submit']

    public static Map<String, By> getNameToXPathMap(RemoteWebDriver driver) {
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

}
