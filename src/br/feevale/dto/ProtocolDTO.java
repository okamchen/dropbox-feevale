package br.feevale.dto;

import java.io.Serializable;

public class ProtocolDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String userName;
	private byte[] bytes;
	private EnumCommand cmd;
	
	public ProtocolDTO(String userName) {
		this.userName = userName;
		this.cmd = EnumCommand.INSTANCE_USER;
	}
	
	public ProtocolDTO(String userName, byte[] bytes, EnumCommand cmd) {
		this.userName = userName;
		this.bytes = bytes;
		this.cmd = cmd;
	}
	
	public boolean isInstanceUser() {
		return this.cmd.equals(EnumCommand.INSTANCE_USER);
	}
	
	public boolean isCraete() {
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

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
