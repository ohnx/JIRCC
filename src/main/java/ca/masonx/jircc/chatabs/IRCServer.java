/**
 * IRCServer.java
 * 
 * Abstraction for the IRC Server
 */
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
	
	/**
	 * Create a new instance of an IRCServer, without connecting yet.
	 * 
	 * @param hostnameFull - Full hostname, possibly including port (if none, will assume 6667)
	 * @throws NumberFormatException - There was a colon in the server name, but it was not followed by a number
	 */
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
	
	/**
	 * Create a new instance of an IRCServer, without connecting yet.
	 * 
	 * @param hostname - the hostname of the server
	 * @param port - the port to connect to on the server
	 */
	public IRCServer(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Attempt a connection to the desired server.
	 * 
	 * @throws UnknownHostException - Could not resolve the domain
	 * @throws IOException - Failed writing to the socke
	 */
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(hostname, port);
		System.out.println("** Socket connected.");
		
		osw = new OutputStreamWriter(socket.getOutputStream());
		isr = new InputStreamReader(socket.getInputStream());
		System.out.println("** Opened OutputStreamWriter and InputStreamReader.");
		
		bw = new BufferedWriter(osw);
		br = new BufferedReader(isr);
		System.out.println("** Opened BufferedWriter and BufferedReader.");
		
		isConnected = true;
	}
	
	/**
	 * Get the buffered writer to talk to the server.
	 * @return BufferedWriter
	 */
	protected BufferedWriter getBW() {
		return bw;
	}
	
	/**
	 * Get the buffered reader to receive from the server.
	 * @return BufferedReader
	 */
	protected BufferedReader getBR() {
		return br;
	}
	
	/**
	 * Send data to the server
	 * @param msg - what to send to the server
	 * @throws IOException - Failed to write to the server
	 */
	public void send(String msg) throws IOException {
		if (!isConnected) throw new IOException("Not connected to a server!");
		System.out.println("<< " + msg);
		bw.write(msg);
		bw.write("\r\n");
		bw.flush();
	}
	
	/**
	 * Get a line from the server
	 * @return A line that was read
	 * @throws IOException Could not read from the server
	 */
	public String getLine() throws IOException {
		String line = br.readLine();
		System.out.println(">> " + line);
		return line;
	}
	
	/**
	 * Disconnect from the server and clean up readers/writers.
	 * @throws IOException - Could not disconnect
	 */
	public void disconnect() throws IOException {
		bw.close();
		br.close();
		osw.close();
		isr.close();
		socket.close();
		isConnected = false;
	}
}
