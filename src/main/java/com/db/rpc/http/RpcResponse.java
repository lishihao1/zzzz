package com.db.rpc.http;

/**
 * rpc响应
 * @author dengbo
 */
public class RpcResponse {
	private String requestId;
	private Throwable throwable;//返回异常
	private Object result;//返回结果
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
}
