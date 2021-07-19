package net.servercore.util.io;

import lombok.Getter;

import java.io.File;

public class CustomFile {
	
	private @Getter final File file;
	private @Getter final String resource;
	private @Getter final boolean dir;
	
	public CustomFile(File file, String path, boolean dir) {
		this.file = file;
		this.resource = path;
		this.dir = dir;
	}
	
}
