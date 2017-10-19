package bzu.edu.cn.river.utlis;

/**
 * @FileName：wenmingdanwei
 * @description：
 * @author：mac-likh
 * @date：2017/5/2 11:13
 */

public class JsonResult {
    public boolean success;//执行状态
    public String message;//返回信息
    public Object data;//结果数据

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
