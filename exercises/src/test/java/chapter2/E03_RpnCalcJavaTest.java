package chapter2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class E03_RpnCalcJavaTest {

    @Test
    void a_simple_sum() {
        assertEquals(9.0, RpnCalcJava.calc("4 5 +"));
        assertEquals(9.0, RpnCalc.calc("4 5 +"));
    }

    @Test
    void a_double_operation() {
        assertEquals(4.0, RpnCalcJava.calc("3 2 1 - +"));
        assertEquals(4.0, RpnCalc.calc("3 2 1 - +"));
    }

    @Test
    void a_division() {
        assertEquals(3.0, RpnCalcJava.calc("6 2 /"));
        assertEquals(3.0, RpnCalc.calc("6 2 /"));
    }

    @Test
    void a_more_complicated_operation() {
        assertEquals(2.0, RpnCalcJava.calc("6 2 1 + /"));
        assertEquals(2.0, RpnCalc.calc("6 2 1 + /"));

        assertEquals(10.0, RpnCalcJava.calc("5 6 2 1 + / *"));
        assertEquals(10.0, RpnCalc.calc("5 6 2 1 + / *"));
    }

    @Test
    void a_bit_of_everything() {
        assertEquals(2.0, RpnCalcJava.calc("2 5 * 4 + 3 2 * 1 + /"));
    }
}
