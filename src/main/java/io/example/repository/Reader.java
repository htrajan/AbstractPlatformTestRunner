package io.example.repository;

import org.openqa.selenium.By;

import java.util.Map;

public interface Reader {

  enum SelectorType {
    BASIC,
    CSS,
    XPATH
  }

  Map<String, By> getObjects();

}
