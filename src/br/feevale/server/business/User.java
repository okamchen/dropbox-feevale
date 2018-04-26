package br.feevale.server.business;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import br.feevale.dto.ProtocolDTO;

public class User implements Runnable {
	
	private String userName;
	
	private InputStream in;
	private OutputStream out;
	
	private EventServerManipulateFile eventUser;
	
	public User(Socket socket, EventServerManipulateFile eventUser) {
		try {
			this.eventUser = eventUser;
			
			this.in = socket.getInputStream();
			this.out = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void sendMessage(ProtocolDTO msg){
        try {
        	ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(msg);
        	objOut.flush();
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
        	while ((msg = (ProtocolDTO) new ObjectInputStream(in).readObject()) != null) {
        		
        		if(msg.isInstanceUser()) {
        			userName = msg.getUserName();
        			eventUser.sincronizePathWithUserInstance(msg);
        			System.out.println("SERVER: Create Instance User: ".concat(userName));
        		}
        		
        		if(msg.isCreate()) {
        			eventUser.createFile(msg);
        			System.out.println("SERVER: Create File of User: ".concat(userName));
        		}
        		
        		if(msg.isUpdate()) {
        			eventUser.updateFile(msg);
        			System.out.println("SERVER: Update File of User: ".concat(userName));
        		}
        		
        		if(msg.isDelete()) {
        			eventUser.deleteFile(msg);
        			System.out.println("SERVER: Delete File of User: ".concat(userName));
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
