package ca.masonx.jircc.chatabs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class IRCServer {
	private final String hostname;
	private final int port;
	private BufferedWriter bw;
	private BufferedReader br;
	private Socket socket;
	private OutputStreamWriter osw;
	private InputStreamReader isr;
	private boolean isConnected = false;
	
	public IRCServer(String hostnameFull) throws NumberFormatException {
		if (hostnameFull.contains(":")) {
			String[] parts = hostnameFull.split(":");
			hostname = parts[0];
			port = Integer.parseInt(parts[1]);
		} else {
			hostname = hostnameFull;
			port = 6667;
		}
	}
	
	public IRCServer(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(hostname, port);
		System.out.println("*** Socket connected.");
		
		osw = new OutputStreamWriter(socket.getOutputStream());
		isr = new InputStreamReader(socket.getInputStream());
		System.out.println("*** Opened OutputStreamWriter and InputStreamReader.");
		
		bw = new BufferedWriter(osw);
		br = new BufferedReader(isr);
		System.out.println("*** Opened BufferedWriter and BufferedReader.");
		
		isConnected = true;
	}
	
	protected BufferedWriter getBW() {
		return bw;
	}
	
	protected BufferedReader getBR() {
		return br;
	}
	
	public void send(String msg) throws IOException {
		if (!isConnected) throw new IOException("Not connected to a server!");
		System.out.println("<< " + msg);
		bw.write(msg);
		bw.write("\r\n");
		bw.flush();
	}
	
	public String getLine() throws IOException {
		String line = br.readLine();
		System.out.println(">> " + line);
		return line;
	}
	
	public void disconnect() throws IOException {
		bw.close();
		br.close();
		osw.close();
		isr.close();
		socket.close();
		isConnected = false;
	}
}
