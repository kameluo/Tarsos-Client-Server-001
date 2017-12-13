package mypackagedemo3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Receiverclose {

	public static void main(String[] args) throws IOException {
		
		InetAddress group=InetAddress.getByName("225.4.5.6");//creating a multicast IP address
		MulticastSocket multicastsocket=new MulticastSocket(3456);//opening a multicast socket port
		multicastsocket.joinGroup(group);//subscribing the multicast IP address to  that socket port,listening to the messages of that IP address from that port
		int port=3456;
		
		String loginmessage="000";
		DatagramPacket datagrampacketsent=new DatagramPacket(loginmessage.getBytes(),loginmessage.length(),group,port);
		multicastsocket.send(datagrampacketsent);
		
	}
}
