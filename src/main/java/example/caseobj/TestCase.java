package example.caseobj;

import java.util.List;

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
            CLICK_SEQUENTIALLY,
            CHECK_ELEMENT,
            WAIT_FOR,
            SCROLL_TO,
            VERIFY_EITHER,
            VERIFY_TRIPLE
        }

        private final Keyword keyword;
        private final List<String> params;

        public TestAction(Keyword keyword, List<String> params) {
            this.keyword = keyword;
            this.params = params;
        }

        public Keyword getKeyword() {
            return keyword;
        }

        public List<String> getParams() {
            return params;
        }

        @Override
        public String toString() {
            return "TestAction{" +
                "keyword=" + keyword +
                ", params=" + params +
                '}';
        }
    }

}
