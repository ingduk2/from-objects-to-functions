package chapter2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class E02_FunStackJavaTest {

    @Test
    void push_into_the_stack() {
        var stack1 = new FunStackJava<>();
        var stack2 = stack1.push('A');

        assertEquals(0, stack1.size());
        assertEquals(1, stack2.size());
    }

    @Test
    void push_pop() {
        var pair = new FunStackJava<>().push('Q').pop();

        assertEquals(0, pair.stack().size());
        assertEquals('Q', pair.value());
    }

    @Test
    void push_push_pop() {
        var pair = new FunStackJava<>()
                .push('A')
                .push('B')
                .pop();

        assertEquals(1, pair.stack().size());
        assertEquals('B', pair.value());
    }
}
