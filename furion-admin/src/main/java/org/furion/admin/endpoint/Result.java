package org.furion.admin.endpoint;


import org.furion.admin.FurionException;
import org.springframework.util.StringUtils;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private static final long serialVersionUID = -3032015199552656978L;

    public static String EXCEPTION_ERROR = "500";
    public static String ERROR = "1";
    public static String SUCCESS = "0";

    private String code;
    private String message;
    private T data;

    public static <T> Result<T> create(String code, String message) {
        return new Result(code, message, (Object) null);
    }

    public static <T> Result<T> create(String code, String message, T data) {
        return new Result(code, message, data);
    }

    public static <T> Result<T> successData(String message, T data) {
        return create(SUCCESS, message, data);
    }

    public static <T> Result<T> successMessage(String message) {
        return successData(message, null);
    }

    public static <T> Result<T> successData(T data) {
        return successData((String) null, data);
    }

    public static <T> Result<T> success() {
        return successMessage((String) null);
    }

    public static <T> Result<T> errorData(String message, T data) {
        return create(ERROR, message, data);
    }

    public static <T> Result<T> error() {
        return errorMessage((String) null);
    }

    public static <T> Result<T> errorData(T data) {
        return errorData((String) null, data);
    }

    public static <T> Result<T> errorMessage(String message) {
        return errorData(message, null);
    }

    public static <T> Result<T> selectiveMessage(boolean success, String successMessage, String errorMessage) {
        return success ? successMessage(successMessage) : errorMessage(errorMessage);
    }

    public static <T> Result<T> selectiveMessage(boolean success, String successMessage, String errorMessage, T data) {
        return success ? successData(successMessage, data) : errorMessage(errorMessage);
    }

    public static <T> Result<T> error(Error e) {
        return e == null ? null : create(ERROR, e.getMessage());
    }

    public static <T> Result<T> error(Exception e) {
        return e == null ? null : create(ERROR, e.getMessage());
    }

    public static <T> Result<T> error(FurionException e) {
        if (e == null) {
            return null;
        } else {
            String code = e.getCode();
            code = StringUtils.isEmpty(code) ? ERROR : code;
            return create(e.getCode(), e.getMessage());
        }
    }

    public Result() {
        this.setMessage("");
    }

    Result(String code, String message, T data) {
        this.setCode(code);
        this.setMessage(message);
        this.setData(data);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = StringUtils.isEmpty(message) ? "" : message;
    }
}
