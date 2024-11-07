package nc.gw.entity;


public class Result<T> implements java.io.Serializable {

    private static final long serialVersionUID = 3423642603195626288L;

    
  //0表示正常
    public static final int SUCCESS_CODE = 0;

    public static final int ERROR_CODE = 1;

    /**
     * 成功
     */
    public static final Result<?> SUCCESS = new Result(SUCCESS_CODE, "SUCCESS");
    /**
     * 失败
     */
    public static final Result<?> ERROR = new Result(ERROR_CODE, "ERROR");

    //返回状态码
    private int code;

    //提示信息
    private String msg;

    //数据类型
    private T data;

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result() {

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

	@Override
	public String toString() {
		return "Result [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}
    

}
