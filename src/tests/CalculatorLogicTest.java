package tests;

import logic.CalculatorLogic;
import models.Expression;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CalculatorLogicTest {
    private CalculatorLogic logic;

    @Before
    public void setUp() {
        logic = new CalculatorLogic();
    }

    @Test
    public void testAddition() {
        double result = logic.evaluateExpression(5.0, 3.0, "+");
        assertEquals(8.0, result, 0.001);
    }

    @Test
    public void testSubtraction() {
        double result = logic.evaluateExpression(5.0, 3.0, "-");
        assertEquals(2.0, result, 0.001);
    }

    @Test
    public void testMultiplication() {
        double result = logic.evaluateExpression(5.0, 3.0, "*");
        assertEquals(15.0, result, 0.001);
    }

    @Test
    public void testDivision() {
        double result = logic.evaluateExpression(6.0, 2.0, "/");
        assertEquals(3.0, result, 0.001);
    }

    @Test(expected = ArithmeticException.class)
    public void testDivisionByZero() {
        logic.evaluateExpression(5.0, 0.0, "/");
    }

    @Test
    public void testHistory() {
        logic.evaluateExpression(5.0, 3.0, "+");
        logic.addToHistory(new Expression(5.0, 3.0, "+", 8.0));
        Assert.assertEquals(1, logic.getHistory().size());
        Assert.assertEquals("5.0 + 3.0 = 8.0", logic.getHistory().get(0).getExpressionString());
    }
}