package br.feevale.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
	
	public static byte[] readBytesFromFile(String path) {
		try {
			return Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
	
	public static void createFile(Path path, byte[] bytes) {
		try {
			Files.write(path, bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateFile(Path path, byte[] bytes) {
		deleteFile(path);
		createFile(path, bytes);
	}

	public static void deleteFile(Path path) {
		try {
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createPath(Path path) {
		File file = new File(path.toString());
		file.mkdirs();
	}

}
