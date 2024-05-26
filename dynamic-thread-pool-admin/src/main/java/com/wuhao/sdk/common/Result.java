package com.wuhao.sdk.common;

import lombok.Data;

/**
 * @Author: wuhao
 * @Datetime: TODO
 * @Description: TODO
 */
@Data
public class Result {
    private Integer code;
    private Object data;
    private String message;

    public static Result success(){
        return new Result(0,null,null);
    }
    public static Result success(Object data){
        return new Result(0,data,null);
    }

    public static Result error(Integer code,String message){
        return new Result(code,null,message);
    }

    public Result(Integer code, Object data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }
}
