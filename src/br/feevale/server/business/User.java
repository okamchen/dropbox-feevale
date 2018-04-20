package br.feevale.server.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import br.feevale.server.dto.FileDTO;

public class User implements Runnable {
	
	private Socket socket;
	private InterestingEvent event;
	
	private String name;
	private List<FileDTO> files;
	
	private BufferedReader in;
	private PrintWriter out;
	
	public User(Socket socket, InterestingEvent event) {
		try {
			this.socket = socket;
			this.event = event;
			
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.out = new PrintWriter(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void sendMessage(String msg){
        try {
            out.println(msg);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void run() {
		String msg;
        try {
        	while ((msg = in.readLine()) != null) {
                System.out.println(msg);
                event.avisaGeral(name, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }		
	}

	public String getName() {
		return name;
	}

}
