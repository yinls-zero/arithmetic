import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Stack;

//标准计算器
public class Calculator {

    private BigDecimal preValue = null; // 前面累计计算值
    private BigDecimal newValue = null; // 新计算值
    private Stack<BigDecimal> valueStack = new Stack<>(); // 计算数值
    private Stack<String> operatorStack = new Stack<>(); // 操作符
    private Stack<BigDecimal> resultStack = new Stack<>(); // 计算总值
    private String curOperator = null; // 当前操作符
    private int scale = 2; // 默认精度2位小数

    public void setNewNum(BigDecimal newValue) {
        if (preValue == null) { // 未计算过,累计总值为第一个输入值
            preValue = newValue;
        } else {
            this.newValue = newValue;
        }
    }

    public void setCurOperator(String curOperator) {
        this.curOperator = curOperator;
    }

    //计算（=号触发）:标准计算器，简单的加减乘除
    private CalcResult calc() {
        //没有按照常规输入，设置默认值
        if (preValue == null) preValue = BigDecimal.ZERO;
        if (curOperator == null) curOperator = "+";
        if (newValue == null) newValue = BigDecimal.ZERO;

        try {
            return calcTwoValue(preValue, curOperator, newValue);
        } catch (Exception e) {
            CalcResult calcResult = new CalcResult(-1, e.getMessage());
            return calcResult;
        }
    }

    //undo
    public CalcResult undo() {
        BigDecimal undoPreValue = BigDecimal.ZERO;
        BigDecimal value1 = BigDecimal.ZERO;
        BigDecimal value2 = BigDecimal.ZERO;
        if (!resultStack.isEmpty() && resultStack.size() > 1) {
            //取第2个结果值
            resultStack.pop();
            undoPreValue = resultStack.peek();
            //取第2个操作符
            operatorStack.pop();
            curOperator = operatorStack.peek();
            //取第3、4个计算值后重新放回
            valueStack.pop();
            valueStack.pop();
            value2 = valueStack.pop();
            value1 = valueStack.pop();
            valueStack.add(value1);
            valueStack.add(value2);
        }

        preValue = undoPreValue;//设置新值

        CalcResult calcResult = new CalcResult(0, "");
        calcResult.setResult(preValue);
        calcResult.setCurOperator(curOperator);
        calcResult.setValue1(value1);
        calcResult.setValue2(value2);
        return calcResult;
    }

    //redo
    public CalcResult redo() {
        if (valueStack.isEmpty() || operatorStack.isEmpty()) {
            System.out.println("valueStack或operatorStack为空！");
            CalcResult calcResult = new CalcResult(-1, "无法redo操作！");
            return calcResult;
        }

        BigDecimal redoPreValue = resultStack.peek();
        String redoOperator = operatorStack.peek();
        try {
            return calcTwoValue(redoPreValue, redoOperator, newValue);
        } catch (Exception e) {
            CalcResult calcResult = new CalcResult(-1, e.getMessage());
            return calcResult;
        }
    }

    /**
     * 进行计算
     *
     * @param preValue    前面已累计值
     * @param curOperator 当前操作
     * @param newValue    新输入值
     * @return 计算结果
     */
    private CalcResult calcTwoValue(BigDecimal preValue, String curOperator, BigDecimal newValue) throws Exception {
        BigDecimal ret = BigDecimal.ZERO;
        if (preValue == null) {
            preValue = this.preValue;
        }
        curOperator = curOperator == null ? "+" : curOperator;
        switch (curOperator) {
            case "+":
                ret = preValue.add(newValue);
                break;
            case "-":
                ret = preValue.subtract(newValue).setScale(scale, RoundingMode.HALF_UP);
                break;
            case "*":
                ret = preValue.multiply(newValue).setScale(scale, RoundingMode.HALF_UP);
                break;
            case "/":
                if (newValue == BigDecimal.ZERO) {
                    throw new Exception("被除数不能为0！");
                }
                ret = preValue.divide(newValue, RoundingMode.HALF_UP);
                break;
        }

        //操作信息
        this.preValue = ret;
        resultStack.add(ret);
        operatorStack.add(curOperator);
        valueStack.add(preValue);
        valueStack.add(newValue);

        //响应信息
        CalcResult calcResult = new CalcResult(0, "");
        calcResult.setResult(ret);
        calcResult.setCurOperator(curOperator);
        calcResult.setValue1(preValue);
        calcResult.setValue2(newValue);
        return calcResult;
    }

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        //直接等号
        CalcResult calcResult = calculator.calc();
        System.out.println("直接等号:" + calcResult.toString());
        System.out.println("----------------------------------");

        //输入一个计算值
        calculator = new Calculator();
        calculator.setNewNum(new BigDecimal(10));
        calcResult = calculator.calc();
        System.out.println("输入一个计算值:" + calcResult.toString());
        System.out.println("----------------------------------");

        //输入计算值、计算符
        calculator = new Calculator();
        calculator.setNewNum(new BigDecimal(10));
        calculator.setNewNum(new BigDecimal(5));
        calculator.setCurOperator("-");
        calcResult = calculator.calc();
        System.out.println("输入计算值、计算符:" + calcResult.toString());
        System.out.println("----------------------------------");

        //redo +
        calculator = new Calculator();
        calculator.setNewNum(new BigDecimal(5));
        calculator.setNewNum(new BigDecimal(5));
        calculator.setCurOperator("+");
        calcResult = calculator.calc();
        System.out.println("redo + calc:" + calcResult.toString());
        for (int i = 0; i < 2; i++) {
            calcResult = calculator.redo();
            System.out.println("redo +:" + calcResult.toString());
        }
        System.out.println("----------------------------------");

        //redo -
        calculator = new Calculator();
        calculator.setNewNum(new BigDecimal(5));
        calculator.setNewNum(new BigDecimal(5));
        calculator.setCurOperator("-");
        calcResult = calculator.calc();
        System.out.println("redo - calc:" + calcResult.toString());
        for (int i = 0; i < 2; i++) {
            calcResult = calculator.redo();
            System.out.println("redo -:" + calcResult.toString());
        }
        System.out.println("----------------------------------");

        //undo
        calculator = new Calculator();
        calculator.setNewNum(new BigDecimal(5));
        calculator.setNewNum(new BigDecimal(5));
        calculator.setCurOperator("+");
        calcResult = calculator.calc();
        System.out.println("undo +:" + calcResult.toString());

        calculator.setNewNum(new BigDecimal(5));
        calculator.setCurOperator("-");
        calcResult = calculator.calc();
        System.out.println("undo -:" + calcResult.toString());

        calculator.setNewNum(new BigDecimal(2));
        calculator.setCurOperator("-");
        calcResult = calculator.calc();
        System.out.println("undo -:" + calcResult.toString());

        for (int i = 0; i < 3; i++) {
            calcResult = calculator.undo();
            System.out.println("undo:" + calcResult.toString());
        }
    }
}