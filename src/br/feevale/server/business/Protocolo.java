package br.feevale.server.business;

import com.google.gson.Gson;

import br.feevale.server.dto.CommunicationProtocol;
import br.feevale.server.dto.FileDTO;

public abstract class Protocolo {
	
	private CommunicationProtocol cp;
	
	public Protocolo(String msg) {
		cp = new Gson().fromJson(msg, CommunicationProtocol.class);
	}
	
	public boolean isName() {
		return cp.getKey().equals("name");
	}

	public boolean isFile() {
		return cp.getKey().equals("file");
	}
	
	public String getName() {
		return cp.getValue();
	}
	
	public FileDTO getFile() {
		return null;
	}
	
}
