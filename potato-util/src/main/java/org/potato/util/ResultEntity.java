package org.potato.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ResultEntity
 *
 * <p>
 *     该类已被 Result.java 替代
 * </p>
 */
@Deprecated
public class ResultEntity {
	
	private Boolean success;
	private Integer code;
	private String message;
	private Map<String, Object> payload = new LinkedHashMap<>();

	// 提取出来作为单独的类，命名为ResultEntityBuilder.java，可以解决转化json时多出一个builder的字段（待测试）；不一定非得作为子类
	public class Builder {
		
		private ResultEntity re = new ResultEntity();
		
		public Builder success() {
			re.success(true);
			return this;
		}
		
		public Builder failure() {
			re.success(false);
			return this;
		}

		public Builder nothing() {
			re.success(null);
			return this;
		}
		
		public Builder code(Integer code) {
			re.code(code);
			return this;
		}
		
		public Builder message(String message) {
			re.message(message);
			return this;
		}
		
		public Builder addPayload(String key, Object value) {
			re.addPayload(key, value);
			return this;
		}
		
		public ResultEntity build() {
			return re;
		}
		public Map<String, Object> buildMap() {
			return re.buildMap();
		}
	}
	
	public static Builder builder() {
		return new ResultEntity().new Builder();
	}

	public Boolean success() {
		return success;
	}
	public void success(Boolean success) {
		this.success = success;
	}
	public String message() {
		return message;
	}
	public void message(String message) {
		this.message = message;
	}
	public Integer code() {
		return code;
	}
	public void code(Integer code) {
		this.code = code;
	}
	public void addPayload(String key, Object value) {
		this.payload.put(key, value);
	}
	@SuppressWarnings("unchecked")
	public <T> T getPayload(String key) {
		Object value = this.payload.get(key);
		if (value != null) {
			return (T) value;
		}
		return null;
	}
	public Map<String, Object> buildMap() {
		Map<String, Object> map = new HashMap<>();
		if (this.success != null) map.put("success", this.success);
		if (this.message != null) map.put("message", this.message);
		if (this.code != null) map.put("code", this.code);
		if (!this.payload.isEmpty()) map.put("payload", this.payload);
		return map;
	}
}