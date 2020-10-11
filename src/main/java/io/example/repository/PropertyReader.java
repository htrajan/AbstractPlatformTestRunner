package io.example.repository;

import org.openqa.selenium.By;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyReader implements Reader {

  private final List<String> objects;

  public PropertyReader(List<String> objects) {
    this.objects = objects;
  }

  @Override
  public Map<String, By> getObjects() {
    Map<String, By> toReturn = new HashMap<>();
    Properties properties = new Properties();
    objects.forEach(object -> {
      try {
        properties.load(getClass().getClassLoader().getResourceAsStream("Objects/" + object + ".properties"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    properties.stringPropertyNames().forEach(property -> {
      String by = properties.getProperty(property);
      int dotIndex = property.indexOf('.');
      String propertyName = property.substring(0, dotIndex);
      SelectorType selectorType = SelectorType.valueOf(property.substring(dotIndex + 1).toUpperCase());
      switch (selectorType) {
        case BASIC:
          break;
        case CSS:
          toReturn.put(propertyName, By.cssSelector(by));
          break;
        case XPATH:
          toReturn.put(propertyName, By.xpath(by));
          break;
      }
    });
    return toReturn;
  }

  public static void main(String[] args) {
    System.out.println(new PropertyReader(Collections.singletonList("login")).getObjects());
  }

}
