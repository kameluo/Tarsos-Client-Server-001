package mypackagemulticastexample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Receiver {

	public static void main(String[] args) {
		
			try {
				
				InetAddress group=InetAddress.getByName("225.4.5.6");//creating a multicast IP address
				MulticastSocket multicastsocket=new MulticastSocket(3456);//opening a multicast socket port
				multicastsocket.joinGroup(group);//subscribing the multicast IP address to  that socket port,listening to the messages of that IP address from that port
				
				byte[] buffer=new byte[100];
				DatagramPacket datagrampacket=new DatagramPacket(buffer,buffer.length);
				multicastsocket.receive(datagrampacket);
				System.out.println(new String(buffer));
				multicastsocket.close();
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
/*
			try {
				InetAddress group=InetAddress.getByName("225.4.5.6");
				MulticastSocket multicastsocket=new MulticastSocket();
				multicastsocket.joinGroup(group);
				String message="Hello How Are You? from the Recviver";
				
				DatagramPacket datagrampacket=new DatagramPacket(message.getBytes(),message.length(),group,3455);
				multicastsocket.send(datagrampacket);
				multicastsocket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		*/
		
		
		
		
	}
}
