package br.feevale.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import br.feevale.dto.EnumCommand;
import br.feevale.dto.ProtocolDTO;
import br.feevale.utils.FileUtils;

public class Client {

    private Socket socket;
    
    private ObjectInputStream in;
	private ObjectOutputStream out;

	private String username;

	protected Path pathClient;
	
    public Client () {
    	connect();
    	getUserConnected(this);
    	sendUserLogged(this);

    	listenServer();
    	listenDirCreateEvent(this.username);
    	listenDirUpdateEvent(this.username);
    	listenDirDeleteEvent(this.username);
    }

	private void listenDirCreateEvent(String nameDir) {
		new Thread() {
            
			@Override
            public void run() {
				try {
					WatchService watchService = pathClient.getFileSystem().newWatchService();
                
	                pathClient.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
	                
	                WatchKey watchKey = null;
	                while (true) {
	                    watchKey = watchService.poll(10, TimeUnit.MINUTES);
	                    
	                    if(watchKey != null) {
	                        
	                        watchKey.pollEvents().stream().forEach(event -> {
                            	Path filePath = (Path) event.context();
								byte[] bytes = FileUtils.readBytesFromFile(pathClient + "/" + filePath.toString());
								sendMessage(new ProtocolDTO(username, bytes, EnumCommand.CREATE));
	                            
	                        });
	                    }
	                    
	                    watchKey.reset();
	                }
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            	
            }


        }.start();
	}
	

	private void listenDirUpdateEvent(String nameDir) {
		new Thread() {
            
			@Override
            public void run() {
				try {
					WatchService watchService = pathClient.getFileSystem().newWatchService();
                
	                pathClient.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
	                
	                WatchKey watchKey = null;
	                while (true) {
	                    watchKey = watchService.poll(10, TimeUnit.MINUTES);
	                    
	                    if(watchKey != null) {
	                        watchKey.pollEvents().stream().forEach(event -> {
	                        	Path filePath = (Path) event.context();
								byte[] bytes = FileUtils.readBytesFromFile(pathClient + "/" + filePath.toString());
								sendMessage(new ProtocolDTO(username, bytes, EnumCommand.UPDATE));
	                        });
	                    }
	                    
	                    watchKey.reset();
	                }
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            	
            }


        }.start();
	}
	

	private void listenDirDeleteEvent(String nameDir) {
		new Thread() {
            
			@Override
            public void run() {
				try {
					WatchService watchService = pathClient.getFileSystem().newWatchService();
                
	                pathClient.register(watchService, StandardWatchEventKinds.ENTRY_DELETE);
	                
	                WatchKey watchKey = null;
	                while (true) {
	                    watchKey = watchService.poll(10, TimeUnit.MINUTES);
	                    
	                    if(watchKey != null) {
	                        
	                        watchKey.pollEvents().stream().forEach(event -> {
	                        	Path filePath = (Path) event.context();
								byte[] bytes = FileUtils.readBytesFromFile(pathClient + "/" + filePath.toString());
								sendMessage(new ProtocolDTO(username, bytes, EnumCommand.DELETE));
	                            
	                        });
	                    }
	                    
	                    watchKey.reset();
	                }
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            	
            }


        }.start();
	}
	
	private static void getUserConnected(Client self) {
		System.out.println("Enter your username: ");
        Scanner scanner = new Scanner(System.in);
        self.username = scanner.nextLine();
        self.pathClient = Paths.get("./share/" + self.username);
        FileUtils.createPath(self.pathClient);
	}

	private static void sendUserLogged(Client self) {
		self.sendMessage(new ProtocolDTO(self.username));
	}

    private void connect() {
        try {
            socket = new Socket("localhost", 8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(ProtocolDTO msg) {
        try {
        	out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private void listenServer() {
        new Thread() {
            @Override
            public void run() {
            	ProtocolDTO msg;
            	
            	try {
            		in = new ObjectInputStream(socket.getInputStream());
					while ((msg = (ProtocolDTO) in.readObject()) != null) {
					    try {
					        System.out.println(msg);
					    } catch (Exception e) {
					        e.printStackTrace();
					    }
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
            }

        }.start();
    }
}
