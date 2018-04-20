package br.feevale.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import br.feevale.builder.ProtocolBuilder;

public class Client {

    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
               
        
        System.out.println("Enter your username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        
        client.sendMessage(new ProtocolBuilder("user", username).getJson());
        
        client.receiveMessage();

    }

    private void connect() {
        try {
            socket = new Socket("localhost", 8080);
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

    private void receiveMessage() {
        new Thread() {
            @Override
            public void run() {
            	String msg;
            	
            	try {
					while ((msg = in.readLine()) != null) {
					    try {
					        System.out.println(msg);
					    } catch (Exception e) {
					        e.printStackTrace();
					    }
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
            }

        }.start();
    }
}
