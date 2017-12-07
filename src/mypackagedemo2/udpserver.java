package mypackagedemo2;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//in this class we will add the server code which will be the phone
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.Timer;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
public class udpserver {
//its workingggg
	public static void main(String[] args) throws IOException,InterruptedException,SocketException,SocketTimeoutException,IllegalArgumentException,ArrayIndexOutOfBoundsException {
		Scanner keyboard = new Scanner(System.in);
		DatagramSocket datagramSocket=new DatagramSocket(2000);
		InetAddress address=InetAddress.getByName("192.168.23.174");
		
		InetAddress IP=InetAddress.getLocalHost();
		String hostname=IP.getHostName();
		//System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa"+hostname);
		String hostIPaddress=IP.getHostAddress();
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+hostIPaddress);
		
		System.out.println("Checking the Connection...");
		if(address.isReachable(5000)){
			System.out.println("Client Class is Connected");
			System.out.println("Do You Want to Get The Log File? (y/n)");
			String letterlog = keyboard.nextLine();
			if(letterlog.equals("n")){
				System.out.println("ok");
			}else if(letterlog.equals("y")){
				System.out.println("Ok,Wait to Send you The File");
				
							
				Thread tlogfile=new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							DatagramSocket datagramsocket=new DatagramSocket(777);
							udpclientlogfile.main(args);
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
							System.out.println("File Received");
						} catch (SocketException e) {
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				tlogfile.start();	
						
			}
			//DatagramSocket datagramSocket=new DatagramSocket(2000);
			String previouscase="0";
			Scanner keyboardcamera = new Scanner(System.in);
			System.out.println("Ready to Receive Data from The UDP Client");
			while(true){
				byte [] b=new byte[100];
				DatagramPacket datagramPacket=new DatagramPacket(b, b.length);
				datagramSocket.setSoTimeout(10000);
				try {
					datagramSocket.receive(datagramPacket);
				} catch (SocketTimeoutException e1) {
					System.out.println("Client Disconnected...");
					//System.out.println(e1);
				//	e1.printStackTrace();
				}
				String servermessage =new String(b);
				String messageparts[]=servermessage.split(" ");
				String clientname=messageparts[0];
				String clientcase=messageparts[1];
				
				Character letter=clientcase.charAt(0);
				
				if(!clientcase.equals(previouscase)){
					System.out.println("the client name is: "+clientname+" and the case is: "+clientcase);
					if( letter=='2'){
						System.out.println("its an Alarm,Do you want to check The camera? (y/n)");
						
						String lettercamera = keyboardcamera.nextLine();
						if(lettercamera.equals("n")){
							System.out.println("ok");
						}else if(lettercamera.equals("y")){
							System.out.println("yesssss");
							Thread tcamera=new Thread(new Runnable() {
								@Override
								public void run() {
									try {
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
										
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
							tcamera.start();
						}//end of the camera thread
					}
				}
			previouscase=clientcase;
			}	
		}else{
			System.out.println("Client Class isnt Disconnected");
		}
		
	}
}