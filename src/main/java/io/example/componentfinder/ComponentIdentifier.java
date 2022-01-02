package io.example.componentfinder;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public enum ComponentIdentifier {
    ID("id"),
    NAME("name"),
    TEXT("text"),
    CLASS("class");

    private static final Map<String, ComponentIdentifier> descriptionToSelf =
        Arrays.stream(ComponentIdentifier.values())
            .collect(toMap(ComponentIdentifier::getDescription, Function.identity()));

    private final String description;

    ComponentIdentifier(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ComponentIdentifier fromDescription(String description) {
        return descriptionToSelf.getOrDefault(description, ID);
    }
}
