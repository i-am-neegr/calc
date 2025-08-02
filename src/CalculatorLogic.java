import java.util.ArrayList;
import java.util.List;

public class CalculatorLogic {
    private List<Expression> history;

    public CalculatorLogic() {
        history = new ArrayList<>();
    }

    public double evaluateExpression(double firstOperand, double secondOperand, String operator) {
        switch (operator) {
            case "+":
                return firstOperand + secondOperand;
            case "-":
                return firstOperand - secondOperand;
            case "*":
                return firstOperand * secondOperand;
            case "/":
                if (secondOperand == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return firstOperand / secondOperand;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    public void addToHistory(Expression expression) {
        history.add(expression);
    }

    public List<Expression> getHistory() {
        return new ArrayList<>(history);
    }
}