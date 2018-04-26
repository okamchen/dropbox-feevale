package br.feevale.server;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import br.feevale.dto.EnumCommand;
import br.feevale.dto.ProtocolDTO;
import br.feevale.server.business.EventServerManipulateFile;
import br.feevale.server.business.User;
import br.feevale.utils.FileUtils;

public class Server implements Runnable, EventServerManipulateFile {
	
	private final static String ROOT_PATH =  "./server/";
	
	private List<User> users = new ArrayList<>();

	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(8080);
			Path path = Paths.get(ROOT_PATH);
			FileUtils.createPath(path);
			while (true) {
				Socket socket = ss.accept();
				User user = new User(socket, this);
				new Thread(user).start();
				users.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void notifyUsers(ProtocolDTO msg) {
		for(User user : users) {
			if (!user.getUserName().equals(msg.getUserName())) {
				user.sendMessage(msg);
			}
		}
	}

	@Override
	public void createFile(ProtocolDTO msg) {
		FileUtils.createFile(getPath(msg), msg.getBytes());
		notifyUsers(msg);
	}

	@Override
	public void updateFile(ProtocolDTO msg) {
		FileUtils.updateFile(getPath(msg), msg.getBytes());
		notifyUsers(msg);
	}

	@Override
	public void deleteFile(ProtocolDTO msg) {
		FileUtils.deleteFile(getPath(msg));
		notifyUsers(msg);
	}

	private Path getPath(ProtocolDTO msg) {
		return Paths.get(ROOT_PATH + msg.getFileName());
	}

	@Override
	public void sincronizePathWithClient(ProtocolDTO msg) {
		for(User user : users) {
			if (user.getUserName().equals(msg.getUserName())) {
				File folder = new File(ROOT_PATH);
				File[] listOfFiles = folder.listFiles();

				for (File file : listOfFiles) {
				    if (file.isFile()) {
				        byte[] bytes = FileUtils.readBytesFromFile(ROOT_PATH + file.getName());
				        user.sendMessage(new ProtocolDTO(msg.getUserName(), bytes, file.getName(), EnumCommand.CREATE));
				    }
				} 
			}
		}
	}

}
