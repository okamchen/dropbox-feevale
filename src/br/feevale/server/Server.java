package br.feevale.server;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import br.feevale.dto.ProtocolDTO;
import br.feevale.server.business.EventServerManipulateFile;
import br.feevale.server.business.User;
import br.feevale.utils.FileUtils;

public class Server implements Runnable, EventServerManipulateFile {
	
	private final static String ROOT_PATH =  "./server/";
	private Path path = Paths.get(ROOT_PATH);
	
	private List<User> users = new ArrayList<>();

	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(8080);
			FileUtils.createPath(path);
			while (true) {
				Socket socket = ss.accept();
				User user = new User(socket, this);
				users.add(user);
				new Thread(user).start();
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
		FileUtils.createFile(path, msg.getBytes());
		notifyUsers(msg);
	}

	@Override
	public void updateFile(ProtocolDTO msg) {
		FileUtils.updateFile(path, msg.getBytes());
		notifyUsers(msg);
	}

	@Override
	public void deleteFile(ProtocolDTO msg) {
		FileUtils.deleteFile(path);
		notifyUsers(msg);
	}

}
