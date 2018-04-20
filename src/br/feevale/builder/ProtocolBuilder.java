package br.feevale.builder;

import com.google.gson.Gson;

public class ProtocolBuilder {
	
	private String key;
	private String value;
	
	private String msg;
	
	public ProtocolBuilder(String key, String value) {
		this.key  = key;
		this.value = value;
	}
	
	public ProtocolBuilder(String msg) {
		this.msg = msg;
	}
	
	public String getJson() {
		return new Gson().toJson(this);
	}
	
	public ProtocolBuilder getObj() {
		return new Gson().fromJson(msg, ProtocolBuilder.class);
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
}
