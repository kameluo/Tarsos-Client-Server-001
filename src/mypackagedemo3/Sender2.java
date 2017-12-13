package mypackagedemo3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

import javax.sound.sampled.LineUnavailableException;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.pitch.PitchDetectionHandler;
import core.be.tarsos.dsp.pitch.PitchDetectionResult;
import core.be.tarsos.dsp.pitch.PitchProcessor;
import core.be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

public class Sender2 {
	static char a='0';
	public static void main(String[] args) throws IOException, LineUnavailableException {
		InetAddress group=InetAddress.getByName("225.4.5.6");//creating a multicast IP address
		MulticastSocket multicastsocket=new MulticastSocket(3456);//opening a multicast socket port
		multicastsocket.joinGroup(group);//subscribing the multicast IP address to  that socket port,listening to the messages of that IP address from that port
		int port=3456;
		
		//Constructing the date
		DateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm a");//To Set the Format of the Date.
		Date currentdate = new Date();//To Get the Current Date.
		//Creating a log file for the Sender"client" side.
		File file=new File("logclient.txt");
		if(!file.exists()){//checking if the file exists or not,if not it will construct it.
			file.createNewFile();}
		//Creating an object from the FileWriter Class to be able to write in it the states
		FileWriter filewriter=new FileWriter(file,true);//the FileWriter contains 2 arguments,first one is for the file name which is in this case is "file" and the second argument is boolean to allow us to write at the end of the file rather than overwrite and lose our previous data  
		//initial state Silence,to avoid the error when we read the last state before we write in the file for the first time,check it (text.length()-1))
		filewriter.write(dateformat.format(currentdate)+" Silence"+"\r\n");
		filewriter.flush();
		filewriter.close();
		
		
		
		while(true){
			byte[] buffer=new byte[100];
			DatagramPacket datagrampacket=new DatagramPacket(buffer,buffer.length);
			multicastsocket.receive(datagrampacket);
			String messagerecived=new String(buffer);
			System.out.println(messagerecived);
			a=messagerecived.charAt(0);//getting the first character from the code sent by the receiver
			if(a=='0' || a=='1'){break;}
		}
			
				
				PitchDetectionHandler handler = new PitchDetectionHandler() {
					@Override
			        public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
			           float freq=pitchDetectionResult.getPitch();
			           System.out.println(freq);
			           
			           if(freq > 60 && freq < 250){
			        	   String case1="200";//Speech
			        	   System.out.println(freq);
			        	   //Reading and Writing the states from the sender"client" side
			        	   String overall=dateformat.format(currentdate)+" Speech";
			        	   try {
			        		   //first we will check"read" the last state in the log file of the sender"client" to not repeat it again,if it is different we will write it 
			        		   	FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\logclient.txt");
								BufferedReader reader=new BufferedReader(fileName);
									String text="";
									String line=reader.readLine();
									while(line != null){
										text += line;
										line=reader.readLine();
									}
									//checking the last letter in the log file,if its not the "h" (stands for the last letter in "speech") we will write the current state which is speech
										if(text.charAt(text.length()-1) != 'h'){
											FileWriter filewritercase1=new FileWriter(file,true);
											filewritercase1.write(overall+"\r\n");
											filewritercase1.flush();
											filewritercase1.close();
										}
			        	   }catch (FileNotFoundException e1) {
							e1.printStackTrace();
			        	   }catch (IOException e1) {
							e1.printStackTrace();
			        	   }
			        	   
			        	   	//sending the current case if it is a speech if the server "Receiver" is logged in
				        	   if(a=='1'){
						        	   byte [] bytecase1=case1.getBytes();//Transferring the Strings to Bytes
						        	   DatagramPacket datagramPacket=new DatagramPacket(bytecase1,bytecase1.length,group,port);//creating the packet
						        	   System.out.println("aaaaaaaaa");
						        	   try {
					        		   multicastsocket.send(datagramPacket);//send the packet
						        	   } catch (IOException e) {
										e.printStackTrace();
						        	   }
				        	   }//the ending of the if condition of sending the current state  
			           }else if(freq > 450 && freq < 2600){
			        	   String case2="300";//Alarm
			        	   System.out.println(freq);
			        	 //Reading and Writing the states from the sender"client" side
			        	   String overall=dateformat.format(currentdate)+" Alarm";
			        	   try {
			        		   //first we will check"read" the last state in the log file of the sender"client" to not repeat it again,if it is different we will write it 
			        		   	FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\logclient.txt");
								BufferedReader reader=new BufferedReader(fileName);
									String text="";
									String line=reader.readLine();
									while(line != null){
										text += line;
										line=reader.readLine();
									}
									//checking the last letter in the log file,if its not the "m" (stands for the last letter in "Alarm") we will write the current state which is Alarm
										if(text.charAt(text.length()-1) != 'm'){
											FileWriter filewritercase1=new FileWriter(file,true);
											filewritercase1.write(overall+"\r\n");
											filewritercase1.flush();
											filewritercase1.close();
										}
			        	   }catch (FileNotFoundException e1) {
							e1.printStackTrace();
			        	   }catch (IOException e1) {
							e1.printStackTrace();
			        	   }
			        	   //sending the current case if it is a Alarm if the server "Receiver" is logged in
				        	   if(a=='1'){
					        	   byte [] bytecase2=case2.getBytes();//Transferring the Strings to Bytes
					        	   DatagramPacket datagramPacket=new DatagramPacket(bytecase2,bytecase2.length,group,port);//creating the packet
					        	   System.out.println("bbbbbbbbb");
					        	   try {
				        		   multicastsocket.send(datagramPacket);//send the packet
					        	   } catch (IOException e) {
									e.printStackTrace();
					        	   }
				        	   }//the ending of the if condition of sending the current state
			           }else{
			        	   String case3="400";//Silence
			        	   System.out.println(freq);
			        	 //Reading and Writing the states from the sender"client" side
			        	   String overall=dateformat.format(currentdate)+" Silence";
			        	   try {
			        		   //first we will check"read" the last state in the log file of the sender"client" to not repeat it again,if it is different we will write it 
			        		   	FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\logclient.txt");
								BufferedReader reader=new BufferedReader(fileName);
									String text="";
									String line=reader.readLine();
									while(line != null){
										text += line;
										line=reader.readLine();
									}
									//checking the last letter in the log file,if its not the "e" (stands for the last letter in "Silence") we will write the current state which is Silence
										if(text.charAt(text.length()-1) != 'e'){
											FileWriter filewritercase1=new FileWriter(file,true);
											filewritercase1.write(overall+"\r\n");
											filewritercase1.flush();
											filewritercase1.close();
										}
			        	   }catch (FileNotFoundException e1) {
							e1.printStackTrace();
			        	   }catch (IOException e1) {
							e1.printStackTrace();
			        	   }
			        	   //sending the current case if it is a Silence if the server "Receiver" is logged in
				        	   if(a=='1'){
					        	   byte [] bytecase3=case3.getBytes();//Transferring the Strings to Bytes
					        	   DatagramPacket datagramPacket=new DatagramPacket(bytecase3,bytecase3.length,group,port);//creating the packet
					        	   System.out.println("ccccccccc");
					        	   try {
				        		   multicastsocket.send(datagramPacket);//send the packet
					        	   } catch (IOException e) {
									e.printStackTrace();
					        	   }
				        	   }//the ending of the if condition of sending the current state
			        }
			        }
			    };
			    AudioDispatcher adp = AudioDispatcherFactory.fromDefaultMicrophone(44100,16384, 0);
			    adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 44100, 16384, handler));
			    adp.run();	
			
		
	}

}
