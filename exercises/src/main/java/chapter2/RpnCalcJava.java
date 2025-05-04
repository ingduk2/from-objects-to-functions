package chapter2;

import java.util.Map;
import java.util.function.BiFunction;

public class RpnCalcJava {

    private static final Map<String, BiFunction<Double, Double, Double>> operationMap = Map.of(
            "+", (a, b) -> a + b,
            "-", (a, b) -> a - b,
            "*", (a, b) -> a * b,
            "/", (a, b) -> a / b
    );

    public static double calc(String expr) {
        var funStack = new FunStackJava<Double>();
        String[] tokens = expr.split(" ");

        for (String token : tokens) {
            funStack = reduce(funStack, token);
        }

        return funStack.pop().value();
    }

    private static FunStackJava<Double> reduce(FunStackJava<Double> stack, String token) {
        if (operationMap.containsKey(token)) {
            var bPair = stack.pop();
            var aPair = bPair.stack().pop();
            double result = operationMap.get(token).apply(aPair.value(), bPair.value());
            return aPair.stack().push(result);
        } else {
            return stack.push(Double.valueOf(token));
        }
    }
}
