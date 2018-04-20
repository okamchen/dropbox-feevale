package br.feevale.server;

import java.io.IOException;

public class Server {
    
	public static void main(String[] args) throws IOException {
		new Thread(new ServerStart()).start();
    }

}
