package mypackagedemo5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class clienttest {

	public static void main(String[] args) {
		
		DatagramPacket datagramPacket = null;
		try {
			DatagramSocket clientSocket = new DatagramSocket(4000);
			byte[] buffer = new byte[65507];
			datagramPacket = new DatagramPacket(buffer, buffer.length);
			clientSocket.receive(datagramPacket);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

         String receivedMessage = new String(datagramPacket.getData());
         System.out.println(receivedMessage);
	}

}
