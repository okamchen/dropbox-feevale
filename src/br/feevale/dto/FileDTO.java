package br.feevale.dto;

public class FileDTO {
	
	private String name;
	private byte[] content;
	
	public FileDTO(String name, byte[] content) {
		this.name = name;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public byte[] getContent() {
		return content;
	}

}
