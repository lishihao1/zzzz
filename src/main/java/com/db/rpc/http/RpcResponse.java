package com.db.rpc.http;

/**
 * rpc��Ӧ
 * @author dengbo
 */
public class RpcResponse {
	private String requestId;
	private Throwable throwable;//�����쳣
	private Object result;//���ؽ��
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
