package br.feevale.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import br.feevale.server.business.InterestingEvent;
import br.feevale.server.business.User;

public class ServerStart implements Runnable, InterestingEvent {
	
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
	public void avisaGeral(String nameUserEmiter, String msg) {
		for(User user : users) {
//			if(user.getName().equals(nameUserEmiter)) {
			user.sendMessage(msg);
//			}
		}
	}

}
