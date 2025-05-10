package chapter5;

import java.util.ArrayList;
import java.util.List;

public class CollatzJava {

    public static List<Integer> collatz(int n) {
        return collatzR(new ArrayList<>(), n);
    }

    private static List<Integer> collatzR(List<Integer> acc, int x) {
        acc.add(x);
        if (x == 1) {
            return acc;
        } else if (x % 2 == 0) {
            return collatzR(acc, x / 2);
        } else {
            return collatzR(acc, x * 3 + 1);
        }
    }
}
