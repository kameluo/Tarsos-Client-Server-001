package mypackagesendfileudp;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.googlecode.javacv.FrameGrabber.Array;

public class clientudp {

	public static void main(String[] args) throws IOException {
		DatagramSocket datagramsocket=new DatagramSocket();
		String filepath="C:\\Users\\Ahmed Kamel\\workspace\\testcam\\src\\mypackagesendfileudp\\Desert.jpg";
		//String filepath="C:\\Users\\Ahmed Kamel\\workspace\\testcam\\log.txt";
		FileInputStream filein=new FileInputStream(filepath);
		// sending the file name first
		String filename=filepath.substring(filepath.lastIndexOf("\\"));
		byte b1[]=filename.getBytes();
		DatagramPacket p1=new DatagramPacket(b1,b1.length,InetAddress.getByName("WIN-NBLVI5CO5KD"),7);
		datagramsocket.send(p1);
		
		// sending the contents of the file
		byte b2[]=new byte[filein.available()];
		filein.read(b2);
		filein.close();
		DatagramPacket p2=new DatagramPacket(b2,b2.length,InetAddress.getByName("WIN-NBLVI5CO5KD"),7);
		datagramsocket.send(p2);
		datagramsocket.close();
		
	}
}
