import java.math.BigDecimal;
import java.util.Stack;
public class CalcResult {

    private int code;//0为成功
    private String msg;

    private BigDecimal result; // 前面累计计算值
    private String curOperator; // 计算符
    private BigDecimal value1; // 计算值1
    private BigDecimal value2; // 计算值2

    public CalcResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public String getCurOperator() {
        return curOperator;
    }

    public void setCurOperator(String curOperator) {
        this.curOperator = curOperator;
    }

    public BigDecimal getValue1() {
        return value1;
    }

    public void setValue1(BigDecimal value1) {
        this.value1 = value1;
    }

    public BigDecimal getValue2() {
        return value2;
    }

    public void setValue2(BigDecimal value2) {
        this.value2 = value2;
    }

    public String toString(){
        StringBuffer strb = new StringBuffer();
        strb.append("result=").append(result).append(",curOperator=").append(curOperator)
                .append(",value1=").append(value1).append(",value2=").append(value2);
        return strb.toString();
    }
}
