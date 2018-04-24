package br.feevale.server.business;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import br.feevale.dto.ProtocolDTO;

public class User implements Runnable {
	
	private String userName;
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private EventServerManipulateFile eventUser;
	
	public User(Socket socket, EventServerManipulateFile eventUser) {
		try {
			this.eventUser = eventUser;
			
			this.in = new ObjectInputStream(socket.getInputStream());
			this.out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void sendMessage(ProtocolDTO msg){
        try {
            out.writeObject(msg);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void run() {
		listen();		
	}

	private void listen() {
		ProtocolDTO msg;
        try {
        	while ((msg = (ProtocolDTO) in.readObject()) != null) {
        		
        		if(msg.isInstanceUser()) {
        			userName = msg.getUserName();
        			System.out.println("Create Instance User: ".concat(userName));
        		}
        		
        		if(msg.isCraete()) {
        			eventUser.createFile(msg);
        			System.out.println("Create File of User: ".concat(userName));
        		}
        		
        		if(msg.isUpdate()) {
        			eventUser.updateFile(msg);
        			System.out.println("Update File of User: ".concat(userName));
        		}
        		
        		if(msg.isDelete()) {
        			eventUser.deleteFile(msg);
        			System.out.println("Delete File of User: ".concat(userName));
        		}
        		
        		
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public String getUserName() {
		return userName;
	}

}
