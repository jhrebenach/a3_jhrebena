import java.io.*;
import java.net.*;

public class Server {
	
	static Integer port = 10000;
	static DatagramSocket socket;
	static ServerSocket serverSocket;
	static String clientName;
	static DatagramSocket socketUDP;

	
	public static void main(String... args) {
		
		// read server name, catch if there is no specified server
		try {
			clientName = args[0];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// if a port is specified (optional), else default is port 10000
		if (args.length > 1) {
			port = Integer.parseInt(args[1]);
		} 
		
		try {
			// listen on specified port
			serverSocket = new ServerSocket(port);
			socketUDP = new DatagramSocket(port);

			// just in case there is an additional delay with the first send
			acceptBytesTCP(10000);
			
			// accept for each client send
			for (int i = 0; i < 1000; i++) {
				acceptBytesTCP(1);
				acceptBytesTCP(10);
				acceptBytesTCP(100);
				acceptBytesTCP(1000);
				acceptBytesTCP(10000);
			}
			
			serverSocket.close();
			
			// just in case there is an additional delay with the first send
			acceptBytesUDP(10000);
			
			// accept for each client send
			for (int i = 0; i < 1000; i++) {
				acceptBytesUDP(1);
				acceptBytesUDP(10);
				acceptBytesUDP(100);
				acceptBytesUDP(1000);
				acceptBytesUDP(10000);
			}

			socketUDP.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void acceptBytesTCP(int length) {

		try {
			// initialize socket to client and input/output streams
			Socket clientSocket = serverSocket.accept();
			InputStream fromClient = clientSocket.getInputStream();
			OutputStream toClient = clientSocket.getOutputStream();
		    
		    
			// accept sent bytes and return them
			int bytes_read;
			byte[] reply = new byte[1024];

			while ( (bytes_read = fromClient.read(reply)) != -1) {
				toClient.write(reply, 0, bytes_read);
				toClient.flush();
			}
			
			// close the socket
			clientSocket.close();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void acceptBytesUDP(int length) {

		try {
			
			// setup input byte array and prepare to receive packet
			byte[] data = new byte[length];
			DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			socketUDP.receive(receivePacket);
			
			// take received packet and send it back to client
			byte[] byteArray = new byte[length];
			DatagramPacket packet = new DatagramPacket(
					byteArray, 0, byteArray.length, InetAddress.getByName(clientName), port);
			socketUDP.send(packet);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return;
	}


}
