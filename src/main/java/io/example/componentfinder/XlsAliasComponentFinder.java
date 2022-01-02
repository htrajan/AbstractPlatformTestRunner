package io.example.componentfinder;

import org.openqa.selenium.By;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Preconditions.checkState;

public class XlsAliasComponentFinder {

    private final String filename;
    private final Map<String, By> aliasToComponentIdentifierMap;

    public XlsAliasComponentFinder(String filename) {
        this.filename = filename;
        this.aliasToComponentIdentifierMap = getAliasToComponentIdentifierMap();
    }

    private Map<String, By> getAliasToComponentIdentifierMap() {
        Map<String, By> toReturn = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            while (line != null) {
                if (!line.startsWith("#")) {
                    String[] columns = line.split("(?<!\\\\),");
                    checkState(columns.length == 3, "Error! Encountered non-triplet alias specification: %s", line);
                    String key = columns[0];
                    ComponentIdentifier identifier = ComponentIdentifier.fromDescription(columns[1]);
                    String value = columns[2].replace("\\,", ",");
                    toReturn.put(key, getByFromIdentifierAndValue(identifier, value));
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return toReturn;
    }

    private static By getByFromIdentifierAndValue(ComponentIdentifier identifier, String value) {
        switch (identifier) {
            case ID:
                return By.id(value);
            case NAME:
                return By.name(value);
            case TEXT:
                return By.xpath(String.format("//*[contains(text(), \"%s\")]", value));
            case CLASS:
                return By.className(value);
        }
        return null;
    }

    public Map<String, By> aliasToComponentIdentifierMap() {
        return aliasToComponentIdentifierMap;
    }
}
