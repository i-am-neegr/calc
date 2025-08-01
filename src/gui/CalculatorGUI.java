package gui;

import logic.CalculatorLogic;
import models.Expression;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CalculatorGUI extends JFrame {
    private List<CalculatorPanel> panels;
    private CalculatorPanel activePanel;

    public CalculatorGUI() {
        panels = new ArrayList<>();
        setTitle("Calc");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLayout(new BorderLayout());

        try {
            Image icon = Toolkit.getDefaultToolkit().getImage("data/calculator.png");
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Icon not found: " + e.getMessage());
        }

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "C", "+",
                "+/-", "=", "Split", ""
        };

        for (String btnText : buttons) {
            if (btnText.isEmpty()) {
                buttonPanel.add(new JLabel(""));
            } else {
                JButton button = new JButton(btnText);
                button.addActionListener(new ButtonListener());
                buttonPanel.add(button);
            }
        }
        add(buttonPanel, BorderLayout.SOUTH);

        CalculatorPanel initialPanel = new CalculatorPanel();
        panels.add(initialPanel);
        activePanel = initialPanel;
        add(initialPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private class CalculatorPanel extends JPanel {
        private CalculatorLogic logic;
        private JTextField displayField;
        private JTextArea historyArea;
        private double firstOperand = 0;
        private String operator = "";
        private boolean isNewNumber = true;
        private StringBuilder currentExpression = new StringBuilder();

        public CalculatorPanel() {
            logic = new CalculatorLogic();
            setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new BorderLayout());
            displayField = new JTextField("0");
            displayField.setEditable(true);
            displayField.setHorizontalAlignment(JTextField.RIGHT);
            displayField.setFont(new Font("Arial", Font.PLAIN, 20));
            topPanel.add(displayField, BorderLayout.CENTER);

            JButton closeButton = new JButton("X");
            closeButton.setFont(new Font("Arial", Font.PLAIN, 12));
            closeButton.setPreferredSize(new Dimension(20, 20));
            closeButton.addActionListener(e -> close());
            topPanel.add(closeButton, BorderLayout.EAST);

            add(topPanel, BorderLayout.NORTH);

            historyArea = new JTextArea();
            historyArea.setEditable(false);
            historyArea.setFont(new Font("Arial", Font.PLAIN, 16));
            JScrollPane scrollPane = new JScrollPane(historyArea);
            add(scrollPane, BorderLayout.CENTER);

            FocusAdapter focusAdapter = new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    activePanel = CalculatorPanel.this;
                }
            };
            displayField.addFocusListener(focusAdapter);
            historyArea.addFocusListener(focusAdapter);

            displayField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char keyChar = e.getKeyChar();
                    if (Character.isDigit(keyChar)) {
                        processInput(String.valueOf(keyChar));
                        e.consume();
                    } else if (keyChar == '.') {
                        processInput(".");
                        e.consume();
                    } else if (keyChar == '+' || keyChar == '-' || keyChar == '*' || keyChar == '/') {
                        processInput(String.valueOf(keyChar));
                        e.consume();
                    } else if (keyChar == '=') {
                        processInput("=");
                        e.consume();
                    } else if (keyChar == 'c' || keyChar == 'C') {
                        processInput("C");
                        e.consume();
                    } else if (keyChar == 'n' || keyChar == 'N') {
                        processInput("+/-");
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        processInput("=");
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        processInput("C");
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        processBackspace();
                        e.consume();
                    }
                }
            });

            displayField.requestFocusInWindow();
        }

        public CalculatorPanel(List<Expression> history, double lastResult) {
            this();
            for (Expression expr : history) {
                logic.addToHistory(expr);
            }
            updateHistoryDisplay();
            if (!Double.isNaN(lastResult)) {
                firstOperand = lastResult;
                currentExpression = new StringBuilder(formatResult(lastResult));
                displayField.setText(currentExpression.toString());
            }
        }

        private String formatResult(double result) {
            String resultStr = String.valueOf(result);
            if (resultStr.contains(".")) {
                String fractionalPart = resultStr.substring(resultStr.indexOf(".") + 1);
                int zeroCount = 0;
                for (int i = 0; i < fractionalPart.length(); i++) {
                    if (fractionalPart.charAt(i) == '0') {
                        zeroCount++;
                    } else if (fractionalPart.charAt(i) != '0' && zeroCount > 7) {
                        return String.format("%.2f", result);
                    } else {
                        zeroCount = 0;
                    }
                }
            }
            return resultStr;
        }

        public void processInput(String command) {
            if (Character.isDigit(command.charAt(0))) {
                if (isNewNumber) {
                    currentExpression = new StringBuilder(operator.isEmpty() ? "" : formatResult(firstOperand) + " " + operator + " ");
                    currentExpression.append(command);
                    isNewNumber = false;
                } else {
                    currentExpression.append(command);
                }
                displayField.setText(currentExpression.toString());
            } else if (command.equals(".")) {
                String currentNumber = isNewNumber ? "" :
                        currentExpression.toString().substring(
                                operator.isEmpty() ? 0 : currentExpression.lastIndexOf(operator) + 2
                        ).trim();
                if (!currentNumber.contains(".")) {
                    if (isNewNumber) {
                        currentExpression = new StringBuilder(operator.isEmpty() ? "0" : formatResult(firstOperand) + " " + operator + " 0");
                        isNewNumber = false;
                    }
                    currentExpression.append(".");
                    displayField.setText(currentExpression.toString());
                }
            } else if (command.equals("+/-")) {
                String currentNumber = isNewNumber ?
                        (operator.isEmpty() ? currentExpression.toString() : formatResult(firstOperand)) :
                        currentExpression.toString().substring(
                                operator.isEmpty() ? 0 : currentExpression.lastIndexOf(operator) + 2
                        ).trim();
                if (currentNumber.isEmpty() || currentNumber.equals("0")) {
                    return;
                }
                if (currentNumber.startsWith("-")) {
                    currentNumber = currentNumber.substring(1);
                } else {
                    currentNumber = "-" + currentNumber;
                }
                if (operator.isEmpty()) {
                    currentExpression = new StringBuilder(currentNumber);
                } else {
                    currentExpression = new StringBuilder(formatResult(firstOperand) + " " + operator + " " + currentNumber);
                }
                if (isNewNumber && operator.isEmpty()) {
                    firstOperand = Double.parseDouble(currentNumber);
                }
                displayField.setText(currentExpression.toString());
            } else if (command.equals("C")) {
                displayField.setText("0");
                firstOperand = 0;
                operator = "";
                isNewNumber = true;
                currentExpression = new StringBuilder();
            } else if (command.equals("=")) {
                if (!operator.isEmpty()) {
                    String secondOperandStr = currentExpression.toString().substring(
                            currentExpression.lastIndexOf(operator) + 2
                    ).trim();
                    if (secondOperandStr.isEmpty()) {
                        return;
                    }
                    double secondOperand = Double.parseDouble(secondOperandStr);
                    double result = logic.evaluateExpression(firstOperand, secondOperand, operator);
                    String formattedResult = formatResult(result);
                    displayField.setText(formattedResult);
                    Expression expr = new Expression(firstOperand, secondOperand, operator, result);
                    logic.addToHistory(expr);
                    updateHistoryDisplay();
                    isNewNumber = true;
                    operator = "";
                    currentExpression = new StringBuilder(formattedResult);
                    firstOperand = result;
                }
            } else {
                if (!operator.isEmpty()) {
                    String secondOperandStr = currentExpression.toString().substring(
                            currentExpression.lastIndexOf(operator) + 2
                    ).trim();
                    if (!secondOperandStr.isEmpty()) {
                        double secondOperand = Double.parseDouble(secondOperandStr);
                        double result = logic.evaluateExpression(firstOperand, secondOperand, operator);
                        String formattedResult = formatResult(result);
                        Expression expr = new Expression(firstOperand, secondOperand, operator, result);
                        logic.addToHistory(expr);
                        updateHistoryDisplay();
                        firstOperand = result;
                        currentExpression = new StringBuilder(formattedResult);
                    }
                } else if (currentExpression.length() > 0) {
                    firstOperand = Double.parseDouble(currentExpression.toString().trim());
                }
                operator = command;
                currentExpression = new StringBuilder(formatResult(firstOperand) + " " + command + " ");
                displayField.setText(currentExpression.toString());
                isNewNumber = true;
            }
        }

        private void processBackspace() {
            if (currentExpression.length() == 0) {
                return;
            }
            String currentText = currentExpression.toString();
            if (operator.isEmpty()) {
                currentExpression = new StringBuilder(currentText.substring(0, currentText.length() - 1));
                if (currentExpression.length() == 0 || currentExpression.toString().equals("-")) {
                    currentExpression = new StringBuilder();
                    firstOperand = 0;
                    displayField.setText("0");
                } else {
                    try {
                        firstOperand = Double.parseDouble(currentExpression.toString());
                        displayField.setText(currentExpression.toString());
                    } catch (NumberFormatException e) {
                        currentExpression = new StringBuilder();
                        firstOperand = 0;
                        displayField.setText("0");
                    }
                }
            } else {
                String secondPart = currentText.substring(currentText.lastIndexOf(operator) + 2).trim();
                if (secondPart.isEmpty()) {
                    currentExpression = new StringBuilder(formatResult(firstOperand));
                    operator = "";
                    isNewNumber = false;
                } else {
                    secondPart = secondPart.substring(0, secondPart.length() - 1);
                    currentExpression = new StringBuilder(formatResult(firstOperand) + " " + operator + " " + secondPart);
                    if (secondPart.isEmpty()) {
                        isNewNumber = true;
                    }
                }
                displayField.setText(currentExpression.toString());
            }
        }

        private void updateHistoryDisplay() {
            historyArea.setText("");
            for (Expression expr : logic.getHistory()) {
                String exprStr = String.format("%s %s %s = %s",
                        formatResult(expr.getFirstOperand()),
                        expr.getOperator(),
                        formatResult(expr.getSecondOperand()),
                        formatResult(expr.getResult()));
                historyArea.append(exprStr + "\n");
            }
        }

        public double getLastResult() {
            try {
                String displayText = displayField.getText().trim();
                if (!displayText.contains(" ")) {
                    return Double.parseDouble(displayText);
                }
            } catch (NumberFormatException e) {
                return Double.NaN;
            }
            return Double.NaN;
        }

        public void split() {
            Container parent = getParent();
            int orientation = (panels.size() % 2 == 0) ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT;

            double lastResult = getLastResult();
            CalculatorPanel panel1 = new CalculatorPanel(logic.getHistory(), lastResult);
            CalculatorPanel panel2 = new CalculatorPanel(logic.getHistory(), lastResult);

            int index = panels.indexOf(this);
            panels.remove(this);
            panels.add(index, panel1);
            panels.add(index + 1, panel2);

            JSplitPane splitPane = new JSplitPane(orientation, panel1, panel2);
            splitPane.setResizeWeight(0.5);
            splitPane.setOneTouchExpandable(true);

            if (parent instanceof JSplitPane) {
                JSplitPane parentSplit = (JSplitPane) parent;
                if (parentSplit.getLeftComponent() == this) {
                    parentSplit.setLeftComponent(splitPane);
                } else {
                    parentSplit.setRightComponent(splitPane);
                }
            } else {
                parent.remove(this);
                parent.add(splitPane, BorderLayout.CENTER);
            }

            activePanel = panel1;
            panel1.displayField.requestFocusInWindow();
            parent.revalidate();
            parent.repaint();
        }

        private void close() {
            if (panels.size() <= 1) {
                return;
            }

            Container parent = getParent();
            if (!(parent instanceof JSplitPane)) {
                return;
            }

            JSplitPane parentSplit = (JSplitPane) parent;
            Component otherComponent = (parentSplit.getLeftComponent() == this) ? parentSplit.getRightComponent() : parentSplit.getLeftComponent();

            panels.remove(this);

            if (activePanel == this) {
                activePanel = panels.isEmpty() ? null : panels.get(0);
                if (activePanel != null) {
                    activePanel.displayField.requestFocusInWindow();
                }
            }

            Container grandParent = parentSplit.getParent();
            if (grandParent instanceof JSplitPane) {
                JSplitPane grandParentSplit = (JSplitPane) grandParent;
                if (grandParentSplit.getLeftComponent() == parentSplit) {
                    grandParentSplit.setLeftComponent(otherComponent);
                } else {
                    grandParentSplit.setRightComponent(otherComponent);
                }
            } else {
                grandParent.remove(parentSplit);
                grandParent.add(otherComponent, BorderLayout.CENTER);
            }

            grandParent.revalidate();
            grandParent.repaint();
        }
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("Split")) {
                if (activePanel != null) {
                    activePanel.split();
                }
            } else if (activePanel != null) {
                activePanel.processInput(command);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorGUI::new);
    }
}