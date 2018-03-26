package com.anluy.restful.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回结果实体
 * @author hc.zeng
 * @create 2018-03-14 9:58
 */

public class Status {
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";
    private String status;
    private String message;
    private Map<String, Object> context;
    private Integer code;

    public Status(String status) {
        this.status = status;
    }

    public Status(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public Status(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status(String status, String message, Integer code) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getContext() {
        if(this.context == null) {
            this.context = new HashMap();
        }

        return this.context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public void setError(String message) {
        this.setStatus("error");
        if(message != null) {
            this.setMessage(message);
        }

    }

    public void setSuccess(String message) {
        this.setStatus("success");
        if(message != null) {
            this.setMessage(message);
        }

    }

    public boolean isFailed() {
        return "error".equals(this.getStatus());
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static Status ok() {
        return new Status("success", Integer.valueOf(200));
    }

    public static Status error() {
        return new Status("error", Integer.valueOf(200));
    }
}
