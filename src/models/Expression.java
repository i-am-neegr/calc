package models;

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

    public double getFirstOperand() {
        return firstOperand;
    }

    public double getSecondOperand() {
        return secondOperand;
    }

    public String getOperator() {
        return operator;
    }

    public double getResult() {
        return result;
    }

    public String getExpressionString() {
        return firstOperand + " " + operator + " " + secondOperand + " = " + result;
    }
}