import java.io.*;
import java.net.*;

public class Client {
	
	static Integer port = 10000;
	static DatagramSocket socket;
	static Socket serverSocketTCP;
	static String serverName;
	
	public static void main(String... args) {
		
		// read server name, catch if there is no specified server
		try {
			serverName = args[0];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// if a port is specified (optional), else default is port 10000
		if (args.length > 1) {
			port = Integer.parseInt(args[1]);
		} 
		
		try {
			// let server get setup, in case someone starts both at almost the same time
			Thread.sleep(3000);
			// get RTT for various packet sizes to local machine over TCP
			
			// just in case there is an additional delay with the first send
			sendBytesTCP(10000);	
			
			long totalTime1byte = 0;
			long totalTime10byte = 0;
			long totalTime100byte = 0;
			long totalTime1000byte = 0;
			long totalTime10000byte = 0;
			
			System.out.println("Conducting 1000 trials sending bytes over TCP...");
			for (int i = 0; i < 1000; i++) {
				totalTime1byte += sendBytesTCP(1);
				totalTime10byte += sendBytesTCP(10);
				totalTime100byte += sendBytesTCP(100);
				totalTime1000byte += sendBytesTCP(1000);
				totalTime10000byte += sendBytesTCP(10000);
			}
			System.out.println("Average RTT for TCP 1 byte : " + (totalTime1byte/1000) + "ns");			
			System.out.println("Average RTT for TCP 10 bytes : " + (totalTime10byte/1000) + "ns");
			System.out.println("Average RTT for TCP 100 bytes : " + (totalTime100byte/1000) + "ns");
			System.out.println("Average RTT for TCP 1000 bytes : " + (totalTime1000byte/1000) + "ns");
			System.out.println("Average RTT for TCP 10000 bytes : " + (totalTime10000byte/1000) + "ns");

			

			// get RTT for various packet sizes to local machine over UDP
			
			// just in case there is an additional delay with the first send
			sendBytesUDP(10000);
			
			totalTime1byte = 0;
			totalTime10byte = 0;
			totalTime100byte = 0;
			totalTime1000byte = 0;
			totalTime10000byte = 0;
			
			System.out.println("Conducting 1000 trials sending bytes over UDP...");
			for (int i = 0; i < 1000; i++) {
				totalTime1byte += sendBytesUDP(1);
				totalTime10byte += sendBytesUDP(10);
				totalTime100byte += sendBytesUDP(100);
				totalTime1000byte += sendBytesUDP(1000);
				totalTime10000byte += sendBytesUDP(10000);
			}
			System.out.println("Average RTT for UDP 1 byte : " + (totalTime1byte/1000) + "ns");
			System.out.println("Average RTT for UDP 10 bytes : " + (totalTime10byte/1000) + "ns");
			System.out.println("Average RTT for UDP 100 bytes : " + (totalTime100byte/1000) + "ns");
			System.out.println("Average RTT for UDP 1000 bytes : " + (totalTime1000byte/1000) + "ns");
			System.out.println("Average RTT for UDP 10000 bytes : " + (totalTime10000byte/1000) + "ns");


			

			long totalTimeAmericanHost = 0;
			// get RTT to google over TCP
			System.out.println("Conducting 20 trials sending bytes to American host over TCP");
			for (int i = 0; i < 20; i++) {
				totalTimeAmericanHost += sendBytesToHostTCP(new URL("http://www.dogpile.com").getHost());
			}
			System.out.println("Average RTT for TCP to American (Oregon) host http://www.dogpile.com : " 
					+ (totalTimeAmericanHost / 20) + "ms");
			
			long totalTimeChineseHost = 0;
			// get RTT to china over TCP
			System.out.println("Conducting 20 trials sending bytes to Chinese host over TCP");
			for (int i = 0; i < 20; i++) {
				totalTimeChineseHost += sendBytesToHostTCP(new URL("http://www.yangfenzi.com").getHost());
			}
			System.out.println("Average RTT for TCP to Chinese host http://www.yangfenzi.com : " 
					+ (totalTimeChineseHost / 20) + "ms");			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static long sendBytesTCP(int length) {
		
		// measure elapsed time of process
		long startTime = System.nanoTime();
		Socket serverSocketTCP;
		
		try {
			
			// open our socket in exterior try/catch (so we will certainly close it if opened)
			serverSocketTCP = new Socket(serverName, port);
			
			// establish connection to server and send length bytes
			try {
				byte[] byteArray = new byte[length];
				final OutputStream toClient = serverSocketTCP.getOutputStream();
			    final DataOutputStream dos = new DataOutputStream(toClient);
			    
			    dos.writeInt(byteArray.length);
			    dos.write(byteArray);

				
			    // read in response from server and close socket
				final InputStream fromServer = serverSocketTCP.getInputStream();
			    final DataInputStream dis = new DataInputStream(fromServer);
	
			    int len = dis.readInt();
			    
			    byte[] data = new byte[len];
			    if (len > 0) {
			        dis.readFully(data, 0, data.length);
			    }
			} catch (Exception e) {
		    	e.printStackTrace();
		    }
		    
			// since we always must close our socket, use separate try/catch
		    serverSocketTCP.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		long endTime = System.nanoTime();
		
		return endTime - startTime;
	}
	
	
	public static long sendBytesUDP(int length) {
		
		// measure elapsed time
		long startTime = System.nanoTime();
		
		try {

			// open socket and send data to server
			DatagramSocket socketUDP = new DatagramSocket(port);
			byte[] byteArray = new byte[length];
			DatagramPacket packet = new DatagramPacket(
					byteArray, 0, byteArray.length, InetAddress.getByName(serverName), port);
			socketUDP.send(packet);
			
			// receive response from server and close socket
			byte[] data = new byte[length];
			DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			socketUDP.receive(receivePacket);
			socketUDP.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long endTime = System.nanoTime();
		
		return endTime - startTime;
	}
	
	public static long sendBytesToHostTCP(String host) {
		
		// since length doesn't matter here, let's choose 10
		int length = 10;
		
		// measure elapsed time of process
		long startTime = System.currentTimeMillis();
		
		try {
			
			// establish connection to server and send length bytes
			Socket serverSocketTCP = new Socket(host, 80);
			byte[] byteArray = new byte[length];
			final OutputStream toClient = serverSocketTCP.getOutputStream();
	
		    final DataOutputStream dos = new DataOutputStream(toClient);
		    dos.writeInt(length);
		    
		    if (length > 0) {
		        dos.write(byteArray, 0, length);
		    }
		    
		    // read in response from server and close socket
			final InputStream fromServer = serverSocketTCP.getInputStream();
			byte[] reply = new byte[length];
			fromServer.read(reply);
		    serverSocketTCP.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		
		return endTime - startTime;
	}

}
