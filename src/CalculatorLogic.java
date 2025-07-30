import java.util.ArrayList;
import java.util.List;

public class CalculatorLogic {
    private List<Expression> history = new ArrayList<>();

    public double evaluateExpression(double a, double b, String operator) {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operator");
        }
    }

    public void addToHistory(Expression expr) {
        history.add(expr);
    }

    public List<Expression> getHistory() {
        return history;
    }
}