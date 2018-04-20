package br.feevale.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import br.feevale.server.business.EventEmiter;
import br.feevale.server.business.User;

public class ServerStart implements Runnable, EventEmiter {
	
	private List<User> users = new ArrayList<>();

	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(8080);
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

	@Override
	public void execute(String nameUserEmiter, String msg) {
		for(User user : users) {
			if(user.getName().equals(nameUserEmiter)) {
				user.sendMessage("Bem vindo, ".concat(nameUserEmiter));
			} else {
				user.sendMessage(msg);
			}
		}
	}

}
