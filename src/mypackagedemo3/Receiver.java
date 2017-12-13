package mypackagedemo3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Receiver {
	
	public static void main(String[] args) {
		
		try {
			InetAddress group=InetAddress.getByName("225.4.5.6");//creating a multicast IP address
			MulticastSocket multicastsocket=new MulticastSocket(3456);//opening a multicast socket port
			multicastsocket.joinGroup(group);//subscribing the multicast IP address to  that socket port,listening to the messages of that IP address from that port
			int port=3456;
			
			String loginmessage="100";
			DatagramPacket datagrampacketsent=new DatagramPacket(loginmessage.getBytes(),loginmessage.length(),group,port);
			multicastsocket.send(datagrampacketsent);
			
			//Constructing the date
			DateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm a");//To Set the Format of the Date
			Date currentdate = new Date();//To Get the Current Date
			//creating a log file for the receiver side
			File file=new File("logserver.txt");
			if(!file.exists()){
				file.createNewFile();}
			
			FileWriter filewriter=new FileWriter(file,true);
			//initial state Silence
			filewriter.write(dateformat.format(currentdate)+" Silence"+"\r\n");
			filewriter.flush();
			filewriter.close();
			/////////////
			while(true){
				byte[] buffer=new byte[100];
				DatagramPacket datagrampacketrecieved=new DatagramPacket(buffer,buffer.length);
				multicastsocket.receive(datagrampacketrecieved);
				String messagerecieved=new String(buffer);
				System.out.println(new String(buffer));
				char check=messagerecieved.charAt(0);
				
				if(check=='2'){
					String overall=dateformat.format(currentdate)+" from "+datagrampacketrecieved.getAddress()+" Speech";
					 try {
				        	FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\logserver.txt");
				    		BufferedReader reader=new BufferedReader(fileName);
				        		String text="";
								String line=reader.readLine();
								while(line != null){
									text += line;
									line=reader.readLine();
								}
									if(text.charAt(text.length()-1) != 'h'){
										FileWriter filewritercase1=new FileWriter(file,true);
										filewritercase1.write(overall+"\r\n");
										filewritercase1.flush();
										filewritercase1.close();
									}
							} catch (IOException e) {
								e.printStackTrace();
							}
				}else if(check=='3'){
					String overall=dateformat.format(currentdate)+" from "+datagrampacketrecieved.getAddress()+" Alarm";
					 try {
				        	FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\logserver.txt");
				    		BufferedReader reader=new BufferedReader(fileName);
				        		String text="";
								String line=reader.readLine();
								while(line != null){
									text += line;
									line=reader.readLine();
								}
									if(text.charAt(text.length()-1) != 'm'){
										FileWriter filewritercase1=new FileWriter(file,true);
										filewritercase1.write(overall+"\r\n");
										filewritercase1.flush();
										filewritercase1.close();
									}
							} catch (IOException e) {
								e.printStackTrace();
							}
				}else if(check=='4'){
					String overall=dateformat.format(currentdate)+" from "+datagrampacketrecieved.getAddress()+" Silence";
					 try {
				        	FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\logserver.txt");
				    		BufferedReader reader=new BufferedReader(fileName);
				        		String text="";
								String line=reader.readLine();
								while(line != null){
									text += line;
									line=reader.readLine();
								}
									if(text.charAt(text.length()-1) != 'e'){
										FileWriter filewritercase1=new FileWriter(file,true);
										filewritercase1.write(overall+"\r\n");
										filewritercase1.flush();
										filewritercase1.close();
									}
							} catch (IOException e) {
								e.printStackTrace();
							}
				}
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
