package net.arcadia.util.io;

import lombok.Getter;

import java.io.File;

public class CustomFile {
	
	private @Getter
	final
	File file;
	private @Getter
	final
	String resource;
	
	public CustomFile(File file, String path) {
		this.file = file;
		this.resource = path;
	}
	
}
