package br.feevale.dto;

import java.io.Serializable;

public class ProtocolDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String userName;
	private String fileName;
	private byte[] bytes;
	private EnumCommand cmd;
	
	public ProtocolDTO(String userName) {
		this.userName = userName;
		this.cmd = EnumCommand.INSTANCE_USER;
	}
	
	public ProtocolDTO(String userName, String fileName) {
		this.userName = userName;
		this.fileName = fileName;
		this.cmd = EnumCommand.DELETE;
	}
	
	public ProtocolDTO(String userName, byte[] bytes, EnumCommand cmd) {
		this.userName = userName;
		this.bytes = bytes;
		this.cmd = cmd;
	}
	
	public boolean isInstanceUser() {
		return this.cmd.equals(EnumCommand.INSTANCE_USER);
	}
	
	public boolean isCreate() {
		return this.cmd.equals(EnumCommand.CREATE);
	}
	
	public boolean isUpdate() {
		return this.cmd.equals(EnumCommand.UPDATE);
	}
	
	public boolean isDelete() {
		return this.cmd.equals(EnumCommand.DELETE);
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public EnumCommand getCmd() {
		return cmd;
	}

	public void setCmd(EnumCommand cmd) {
		this.cmd = cmd;
	}

	public String getFileName() {
		return fileName;
	}

	public byte[] getBytes() {
		return bytes;
	}
	
}
