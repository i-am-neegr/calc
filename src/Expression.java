public class Expression {
    private double firstOperand;
    private double secondOperand;
    private String operator;
    private double result;

    public Expression(double firstOperand, double secondOperand, String operator, double result) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.operator = operator;
        this.result = result;
    }

    public String getExpressionString() {
        return firstOperand + " " + operator + " " + secondOperand + " = " + result;
    }
}