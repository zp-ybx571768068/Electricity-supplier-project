package com.pinyougou.vo;

import java.io.Serializable;

public class Result implements Serializable {


    /**
     * 返回结果成功还是失败
     */
    private Boolean success;
    /**
     *  错误信息
     */
    private String message;

    public static Result success(String message){
        return new Result(true,message);
    }
    public static Result error(String message){
        return new Result(false,message);
    }

    public Result() {
    }

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
