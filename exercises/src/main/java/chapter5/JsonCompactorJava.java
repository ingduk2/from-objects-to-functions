package chapter5;

import static chapter5.JsonCompactorJava.*;

public sealed abstract class JsonCompactorJava permits InQuotesJava, OutQuotesJava, EscapedJava {
    protected final String jsonCompacted;

    protected JsonCompactorJava(String jsonCompacted) {
        this.jsonCompacted = jsonCompacted;
    }

    public String getJsonCompacted() {
        return jsonCompacted;
    }

    public abstract JsonCompactorJava compact(char c);

    public static final class InQuotesJava extends JsonCompactorJava {
        public InQuotesJava(String jsonCompacted) {
            super(jsonCompacted);
        }

        @Override
        public JsonCompactorJava compact(char c) {
            System.out.println("[InQuotes.compact] c: " + c + ", jsonCompacted: " + jsonCompacted);
            return switch (c) {
                case '\\' -> new EscapedJava(jsonCompacted + c);
                case '"' -> new OutQuotesJava(jsonCompacted + c);
                default -> new InQuotesJava(jsonCompacted + c);
            };
        }
    }

    public static final class OutQuotesJava extends JsonCompactorJava {
        public OutQuotesJava(String jsonCompacted) {
            super(jsonCompacted);
        }

        @Override
        public JsonCompactorJava compact(char c) {
            System.out.println("[OutQuotes.compact] c: " + c + ", jsonCompacted: " + jsonCompacted);
            if (Character.isWhitespace(c)) {
                return this;
            } else if (c == '"') {
                return new InQuotesJava(jsonCompacted + c);
            } else {
                return new OutQuotesJava(jsonCompacted + c);
            }
        }
    }

    public static final class EscapedJava extends JsonCompactorJava {
        public EscapedJava(String jsonCompacted) {
            super(jsonCompacted);
        }

        @Override
        public JsonCompactorJava compact(char c) {
            System.out.println("[Escaped.compact] c: " + c + ", jsonCompacted: " + jsonCompacted);
            return new InQuotesJava(jsonCompacted + c);
        }
    }
}
