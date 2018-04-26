package br.feevale.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
    
    private InputStream in;
	private OutputStream out;

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
                        		sendMessage(new ProtocolDTO(username, bytes, filePath.toString(), EnumCommand.CREATE));
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
								sendMessage(new ProtocolDTO(username, bytes, filePath.toString(), EnumCommand.UPDATE));
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
								sendMessage(new ProtocolDTO(username, filePath.toString()));
	                            
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
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(ProtocolDTO msg) {
        try {
        	ObjectOutputStream outObj = new ObjectOutputStream(out);
        	outObj.writeObject(msg);
        	outObj.flush();
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
					while ((msg = (ProtocolDTO) new ObjectInputStream(in).readObject()) != null) {
		        		if(msg.isCreate()) {
		        			createFile(msg);
		        		}
		        		
		        		if(msg.isUpdate()) {
		        			updateFile(msg);
		        		}
		        		
		        		if(msg.isDelete()) {
		        			deleteFile(msg);
		        		}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
            }

			private void deleteFile(ProtocolDTO msg) {
				Path path = getPath(msg);
				FileUtils.deleteFile(path);
				System.out.println("CLIENT: Delete " + msg.getFileName() + " File on Client");
			}


			private void updateFile(ProtocolDTO msg) {
				Path path = getPath(msg);
				FileUtils.updateFile(path, msg.getBytes());
				System.out.println("CLIENT: Update " + msg.getFileName() + " File on Client");
			}

			private void createFile(ProtocolDTO msg) {
				Path path = getPath(msg);
				if(!isThereFile(msg.getFileName())) {
					FileUtils.createFile(path, msg.getBytes());
					System.out.println("CLIENT: Create " + msg.getFileName() + " File on Client");
				}
			}
			
			private Path getPath(ProtocolDTO msg) {
				return Paths.get(pathClient.toString()+ "/" + msg.getFileName());
			}

        }.start();
    }

	private boolean isThereFile(String fileName) {
		File folder = new File(pathClient.toString());
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile() && file.getName().equals(fileName)) {
		    	return true;
		    }
		}
		return false;
	}
}
