package chapter5;

import chapter5.JsonCompactorJava.OutQuotesJava;

public class JsonUtilsJava {

    public static String compactJson(String json) {
        JsonCompactorJava result = json.chars()
                .mapToObj(c -> (char) c)
                .reduce(
                        new OutQuotesJava(""),
                        JsonUtilsJava::compactor,
                        (a, b) -> b
                );

        return result.getJsonCompacted();
    }

    private static JsonCompactorJava compactor(JsonCompactorJava prev, char c) {
        return prev.compact(c);
    }

    public static String compactJsonLoop(String json) {
        JsonCompactorJava compactor = new OutQuotesJava("");

        for (char c : json.toCharArray()) {
            compactor = compactor.compact(c);
        }

        return compactor.getJsonCompacted();
    }
}
