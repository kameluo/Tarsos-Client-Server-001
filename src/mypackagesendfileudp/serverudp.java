package mypackagesendfileudp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class serverudp {

	public static void main(String[] args) throws IOException {
		
		InetAddress IP=InetAddress.getLocalHost();
		String hostname=IP.getHostName();
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa"+hostname);
		String hostIPaddress=IP.getHostAddress();
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+hostIPaddress);
		
		DatagramSocket datagramsocket=new DatagramSocket(7);
		byte b1[]=new byte[200];
		DatagramPacket p1=new DatagramPacket(b1,b1.length);
		datagramsocket.receive(p1);
		String fname=new String(b1).trim();
		FileOutputStream fileout=new FileOutputStream("C:\\"+fname);
		
		byte b2[]=new byte[1024*1024];
		DatagramPacket p2=new DatagramPacket(b2,b2.length);
		datagramsocket.receive(p2);
		fileout.write(b2);
		fileout.close();
		
	}

}
