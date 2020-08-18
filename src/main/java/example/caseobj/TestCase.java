package example.caseobj;

import java.util.List;
import java.util.Optional;

public class TestCase {

    private final String name;
    private final List<TestAction> actions;

    public TestCase(String name, List<TestAction> actions) {
        this.name = name;
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public List<TestAction> getActions() {
        return actions;
    }

    public static class TestAction {
        public enum Keyword {
            GOTO,
            SET_TEXT,
            CLICK,
            CHECK_ELEMENT,
            WAIT_FOR,
            SCROLL_TO,
            VERIFY_EITHER
        }

        private final Keyword keyword;
        private final String firstParam;
        private final String secondParam;

        public TestAction(Keyword keyword, String firstParam, String secondParam) {
            this.keyword = keyword;
            this.firstParam = firstParam;
            this.secondParam = secondParam;
        }

        public Keyword getKeyword() {
            return keyword;
        }

        public Optional<String> getFirstParam() {
            return Optional.ofNullable(firstParam);
        }

        public Optional<String> getSecondParam() {
            return Optional.ofNullable(secondParam);
        }

        @Override
        public String toString() {
            return "TestAction{" +
                    "keyword=" + keyword +
                    ", firstParam='" + firstParam + '\'' +
                    ", secondParam='" + secondParam + '\'' +
                    '}';
        }
    }

}
