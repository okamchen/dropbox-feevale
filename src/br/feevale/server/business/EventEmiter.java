package br.feevale.server.business;

public interface EventEmiter {
	
	void execute(String nameUserEmiter, String msg);

}
