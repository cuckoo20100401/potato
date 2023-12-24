package org.potato.util;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Result
 */
@JsonIgnoreProperties({"failure", "nothing"})
public class Result {

    private Boolean success;
    private Integer code;
    private String message;
    private Map<String, Object> payload = new LinkedHashMap<>();

    public Result(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public static Result success() {
        return new Result(true, 200, "ok");
    }
    public static Result failure() {
        return new Result(false, 500, "error");
    }
    public static Result nothing() {
        return new Result(null, 200, "do nothing");
    }

    public Result code(Integer code) {
        this.code = code;
        return this;
    }

    public Result message(String message) {
        this.message = message;
        return this;
    }

    public Result addPayload(String key, Object value) {
        this.payload.put(key, value);
        return this;
    }

    public Boolean isSuccess() {
        return this.success != null && this.success;
    }
    public Boolean isFailure() {
        return this.success != null && !this.success;
    }
    public Boolean isNothing() {
        return this.success == null;
    }

    @JsonGetter
    public Integer code() {
        return this.code;
    }
    @JsonGetter
    public String message() {
        return this.message;
    }
    public Map<String, Object> getPayload() {
        return payload;
    }
    public <T> T getPayload(String key) {
        Object value = this.payload.get(key);
        if (value != null) {
            return (T) value;
        }
        return null;
    }
}
