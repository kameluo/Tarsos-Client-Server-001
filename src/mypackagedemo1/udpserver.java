package mypackagedemo1;
//this package is working well to transmit data from client side to the server side and give the server the possibility to open the camera if he wants
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.Timer;


public class udpserver {

	public static void main(String[] args) throws IOException,InterruptedException,SocketException {
		DatagramSocket datagramSocket=new DatagramSocket(2000);
		
		String previouscase="00";
		
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Ready to Receive Data from The UDP Client");
	
		while(true){
			byte [] b=new byte[100];
			DatagramPacket datagramPacket=new DatagramPacket(b, b.length);
			datagramSocket.setSoTimeout(10000);
			
			try {
				datagramSocket.receive(datagramPacket);
			} catch (SocketTimeoutException e1) {
				System.out.println("Client Disconnected...");
				e1.printStackTrace();
			}
			String servermessage =new String(b);
			String messageparts[]=servermessage.split(" ");
			String clientname=messageparts[0];
			String clientcase=messageparts[1];
			String IP=messageparts[2];
			//System.out.println(IP);
			
			Character letter=clientcase.charAt(1);
			
			if(!clientcase.equals(previouscase)){
				System.out.println("the client name is: "+clientname+" and the case is: "+clientcase);
				if( letter=='1'){
					System.out.println("its an Alarm,Do you want to check The camera? (y/n)");
					
					String lettercamera = keyboard.nextLine();
					if(lettercamera.equals("n")){
						System.out.println("ok");
					}else if(lettercamera.equals("y")){
						System.out.println("yesssss");
						
						Thread t1=new Thread(new Runnable() {
							
							@Override
							public void run() {
								try {/*
									OpenCVFrameGrabber frameGrabber = new OpenCVFrameGrabber("http://10.82.240.143:8080/mjpg/video.mjpg"); 
									frameGrabber.setFormat("mjpeg");
									frameGrabber.start();
									IplImage iPimg = frameGrabber.grab();
									CanvasFrame canvasFrame = new CanvasFrame("Camera");
									canvasFrame.setCanvasSize(iPimg.width(), iPimg.height());
									
									while (canvasFrame.isVisible() && (iPimg = frameGrabber.grab()) != null) {
									    canvasFrame.showImage(iPimg);
									}
									frameGrabber.stop();
									canvasFrame.dispose();
									//System.exit(0);
								*/	
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						t1.start();
					}
				}
			}
		previouscase=clientcase;
		}
	}
}
