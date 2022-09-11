package tech.asmussen.auth.web;

import tech.asmussen.auth.core.UltraAuthenticator;
import tech.asmussen.auth.util.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;

public class WebServer extends Utility implements Runnable {
	
	private final int port;
	
	public WebServer(int port) {
		
		this.port = port;
	}
	
	public int getPort() {
		
		return port;
	}
	
	@Override
	public void run() {
		
		try (ServerSocket serverSocket = new ServerSocket(this.port)) {
			
			while (true) {
				
				try (Socket clientSocket = serverSocket.accept()) {
					
					BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					
					StringBuilder request = new StringBuilder();
					
					String getValue = null;
					
					String line;
					while (!(line = input.readLine()).isBlank()) {
						
						request.append(line).append("\n");
						
						if (line.startsWith("GET")) getValue = line.split(" ")[1].replace("/", "");
					}
					
					if (serverConfig.getBoolean("server.web.verbose")) {
						
						System.out.println("-- REQUEST --");
						System.out.println(request);
					}
					
					// Routing.
					try {
						
						FileInputStream image = new FileInputStream(getPlugin().getDataFolder() + "/QRs/" + getValue + ".png");
						
						OutputStream output = clientSocket.getOutputStream();
						
						output.write("HTTP/1.1 200 OK\n".getBytes());
						output.write("\n".getBytes());
						output.write(image.readAllBytes());
						
						output.flush();
						output.close();
						
					} catch (IOException e) {
						
						OutputStream output = clientSocket.getOutputStream();
						
						output.write("HTTP/1.1 200 OK\n".getBytes());
						output.write("\n".getBytes());
						output.write(new FileInputStream(getPlugin().getDataFolder() + serverConfig.getString("server.web.path") + "/404.html").readAllBytes());
						
						output.flush();
						output.close();
					}
				}
			}
			
		} catch (IOException ignored) { }
	}
	
	public static void makeWebFiles(String path) {
		
		File webFolder = new File(path);
		
		webFolder.mkdirs();
		
		// Create 404.html
		File fileNotFound = new File(webFolder, "404.html");
		
		try {
			
			if (fileNotFound.createNewFile()) {
				
				InputStream inputStream = UltraAuthenticator.class.getResourceAsStream("/web/404.html");
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream, "File not found!")));
				
				StringBuilder htmlCode = new StringBuilder();
				
				String line;
				while ((line = reader.readLine()) != null)
					
					htmlCode.append(line).append("\n");
				
				Files.write(fileNotFound.toPath(), htmlCode.toString().getBytes());
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
