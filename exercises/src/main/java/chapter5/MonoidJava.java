package chapter5;

import java.util.List;
import java.util.function.BiFunction;

public class MonoidJava<T> {
    private final T zero;
    private final BiFunction<T, T, T> combination;

    public MonoidJava(T zero, BiFunction<T, T, T> combination) {
        this.zero = zero;
        this.combination = combination;
    }

    public T fold(List<T> values) {
        T result = zero;
        for (T value : values) {
            result = combination.apply(result, value);
        }
        return result;
    }
}