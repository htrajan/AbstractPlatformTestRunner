package example.repository;

import org.openqa.selenium.By;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RepositoryReader implements Reader {

  public Map<String, By> getObjects() {
    Map<String, By> toReturn = new HashMap<>();
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      File repository = new File(Objects.requireNonNull(
          RepositoryReader.class.getClassLoader().getResource("Object Repository")).toURI());
      for (File subDir : Objects.requireNonNull(repository.listFiles())) {
        if (!subDir.isDirectory()) continue;
        for (File rsFile : Objects.requireNonNull(subDir.listFiles())) {
          if (!rsFile.getName().endsWith(".rs")) {
            continue;
          }
          Document doc = dBuilder.parse(rsFile);
          Element documentElement = doc.getDocumentElement();
          documentElement.normalize();
          String name = documentElement.getElementsByTagName("name").item(0).getTextContent();
          NodeList selectors;
          try {
            selectors = documentElement.getElementsByTagName("selectorCollection")
                .item(0)
                .getOwnerDocument()
                .getElementsByTagName("entry");
          } catch (Exception e) {
            continue;
          }
          for (int i = 0; i < selectors.getLength(); i++) {
            Element selector = (Element) selectors.item(i);
            String key;
            String value;
            try {
              key = selector.getElementsByTagName("key").item(0).getTextContent();
              value = selector.getElementsByTagName("value").item(0).getTextContent();
            } catch (Exception e) {
              continue;
            }
            if (key != null && value != null) {
              Reader.SelectorType selectorType = Reader.SelectorType.valueOf(key);
              Optional<By> maybeBy = Optional.empty();
              switch (selectorType) {
                case BASIC:
                  break;
                case CSS:
                  maybeBy = Optional.of(By.cssSelector(value));
                  break;
                case XPATH:
                  maybeBy = Optional.of(By.xpath(value));
                  break;
              }
              maybeBy.ifPresent(by -> toReturn.put(subDir.getName() + "/" + name, by));
              if (maybeBy.isPresent()) {
                break;
              }
            }
          }
        }
      }
      return toReturn;
    } catch (Exception e) {
      e.printStackTrace();
      return toReturn;
    }
  }

}
