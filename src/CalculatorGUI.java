import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorGUI extends JFrame {
    private CalculatorLogic logic;
    private JTextField displayField;
    private JTextArea historyArea;
    private double firstOperand = 0;
    private String operator = "";
    private boolean isNewNumber = true;

    public CalculatorGUI() {
        logic = new CalculatorLogic();
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLayout(new BorderLayout());

        // Display field
        displayField = new JTextField("0");
        displayField.setEditable(false);
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setFont(new Font("Arial", Font.PLAIN, 20));
        add(displayField, BorderLayout.NORTH);

        // History area
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", "C", "=", "+"
        };

        for (String btnText : buttons) {
            JButton button = new JButton(btnText);
            button.addActionListener(new ButtonListener());
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (Character.isDigit(command.charAt(0))) {
                if (isNewNumber) {
                    displayField.setText(command);
                    isNewNumber = false;
                } else {
                    displayField.setText(displayField.getText() + command);
                }
            } else if (command.equals("C")) {
                displayField.setText("0");
                firstOperand = 0;
                operator = "";
                isNewNumber = true;
            } else if (command.equals("=")) {
                if (!operator.isEmpty()) {
                    double secondOperand = Double.parseDouble(displayField.getText());
                    double result = logic.evaluateExpression(firstOperand, secondOperand, operator);
                    displayField.setText(String.valueOf(result));
                    Expression expr = new Expression(firstOperand, secondOperand, operator, result);
                    logic.addToHistory(expr);
                    updateHistoryDisplay();
                    isNewNumber = true;
                    operator = "";
                }
            } else {
                if (!operator.isEmpty()) {
                    double secondOperand = Double.parseDouble(displayField.getText());
                    double result = logic.evaluateExpression(firstOperand, secondOperand, operator);
                    displayField.setText(String.valueOf(result));
                    Expression expr = new Expression(firstOperand, secondOperand, operator, result);
                    logic.addToHistory(expr);
                    updateHistoryDisplay();
                }
                firstOperand = Double.parseDouble(displayField.getText());
                operator = command;
                isNewNumber = true;
            }
        }
    }

    private void updateHistoryDisplay() {
        historyArea.setText("");
        for (Expression expr : logic.getHistory()) {
            historyArea.append(expr.getExpressionString() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorGUI::new);
    }
}