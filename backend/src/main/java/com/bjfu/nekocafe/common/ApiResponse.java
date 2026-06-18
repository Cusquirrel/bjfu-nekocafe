package com.bjfu.nekocafe.common;

public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
    private String traceId;

    public ApiResponse() {}
    public ApiResponse(boolean success, String code, String message, T data, String traceId) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = traceId;
    }
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<T>(true, "OK", "success", data, TraceId.get()); }
    public static <T> ApiResponse<T> fail(String code, String message) { return new ApiResponse<T>(false, code, message, null, TraceId.get()); }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
}
