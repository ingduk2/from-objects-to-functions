package chapter2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunStackJava<T> {
    private final List<T> elements;

    public FunStackJava() {
        this.elements = Collections.emptyList();
    }

    private FunStackJava(List<T> elements) {
        this.elements = elements;
    }

    public FunStackJava<T> push(T element) {
        List<T> newElements = List.of(element);
        newElements = concat(newElements, this.elements);
        return new FunStackJava<>(newElements);
    }

    public Pair<T, FunStackJava<T>> pop() {
        T top = elements.getFirst();
        FunStackJava<T> rest = new FunStackJava<>(elements.subList(1, elements.size()));
        return new Pair<>(top, rest);
    }

    public int size() {
        return elements.size();
    }

    private List<T> concat(List<T> head, List<T> tail) {
        return List.copyOf(new ArrayList<>() {{
            addAll(head);
            addAll(tail);
        }});
    }

    public record Pair<A, B>(
            A value,
            B stack
    ) {
    }
}
