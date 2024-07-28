package com.ld.poetry.config;

import com.ld.poetry.utils.CodeMsg;
import lombok.Data;

import java.io.Serializable;

@Data
public class UResult<T> implements Serializable {

    private static final long serialVersionUI = 1L;

    private int code;
    private String message;
    private T data;
    private long currentTimeMillis = System.currentTimeMillis();

    public UResult() {
        this.code = 200;
    }

    public UResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public UResult(T data) {
        this.code = 200;
        this.data = data;
    }

    public UResult(String message) {
        this.code = 500;
        this.message = message;
    }

    public static <T> UResult<T> fail(String message) {
        return new UResult(message);
    }

    public static <T> UResult<T> fail(CodeMsg codeMsg) {
        return new UResult(codeMsg.getCode(), codeMsg.getMsg());
    }

    public static <T> UResult<T> fail(Integer code, String message) {
        return new UResult(code, message);
    }

    public static <T> UResult<T> success(T data) {
        return new UResult(data);
    }

    public static <T> UResult<T> success() {
        return new UResult();
    }
}
