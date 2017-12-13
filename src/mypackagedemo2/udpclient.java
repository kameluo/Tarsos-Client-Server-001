package mypackagedemo2;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.print.attribute.standard.PrinterStateReason;
import javax.sound.sampled.LineUnavailableException;

import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.pitch.PitchDetectionHandler;
import core.be.tarsos.dsp.pitch.PitchDetectionResult;
import core.be.tarsos.dsp.pitch.PitchProcessor;
import core.be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

public class udpclient {

	public static void main(String args[]) throws LineUnavailableException, IOException,FileNotFoundException  {
		DatagramSocket datagramSocket=new DatagramSocket();//creating the socket;
		InetAddress IP=InetAddress.getLocalHost();//to make an object of that class to be able to get the information of that PC,The current PC
		String hostname=IP.getHostName();//to get The Name of That PC
		String hostIPaddress=IP.getHostAddress();//to get the IP of That PC
		int port=2000;
		DateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm a");//To Set the Format of the Date
		Date currentdate = new Date();//To Get the Current Date
		File file=new File("log.txt");
		if(!file.exists()){
			file.createNewFile();}
		
		FileInputStream logfile=new FileInputStream("log.txt");
		byte bfile [] =new byte [logfile.available()];
		logfile.read(bfile);
		logfile.close();
		
		//For Reading The Log File
		//FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\log.txt");
		//BufferedReader reader=new BufferedReader(fileName);
		
        PitchDetectionHandler handler = new PitchDetectionHandler() {
			@Override
	        public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
	           float freq=pitchDetectionResult.getPitch();
	          
	           if(freq > 60 && freq < 250){
	        	   String case1="1";//Speech
	        	   String overall=dateformat.format(currentdate)+" "+hostname+" "+hostIPaddress+" "+case1+" "+ "Speech";
	        	   System.out.println(overall);
	        	   
	        	   String overallsend=hostname+" "+case1;//Speech
	        	   byte [] b=overallsend.getBytes();//Transferring the Strings to Bytes
	        	   DatagramPacket datagramPacket=new DatagramPacket(b,b.length,IP,port);//creating the packet
	        	   try {
					datagramSocket.send(datagramPacket);//send the packet
				} catch (IOException e) {
					e.printStackTrace();
				}
	        	   
	        	   	
	        	   try {
			        	FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\log.txt");
			    		BufferedReader reader=new BufferedReader(fileName);
			        		String text="";
							String line=reader.readLine();
							while(line != null){
								text += line;
								line=reader.readLine();
							}
							
								if(text.charAt(text.length()-1) != 'h'){
									
									FileWriter filewriter=new FileWriter(file,true);
									filewriter.write(overall+"\r\n");
									filewriter.flush();
									filewriter.close();
								}
							
						} catch (IOException e) {
							e.printStackTrace();
						}	
					
	           }else if(freq > 450 && freq < 2600){
	        	   String case2="2";//Alarm
	        	   String overall=dateformat.format(currentdate)+" "+hostname+" "+hostIPaddress+" "+case2+" "+ "Alarm";
	        	   System.out.println(overall);
	        	   
	        	   String overallsend=hostname+" "+case2;//Alarm
	        	   byte [] b=overallsend.getBytes();//Transferring the Strings to Bytes
	        	   DatagramPacket datagramPacket=new DatagramPacket(b,b.length,IP,port);//creating the packet
	        	   try {
					datagramSocket.send(datagramPacket);//send the packet
				} catch (IOException e) {
					e.printStackTrace();
				}
	        	   
	        	   try {
			        	FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\log.txt");
			    		BufferedReader reader=new BufferedReader(fileName);
			        		String text="";
							String line=reader.readLine();
							while(line != null){
								text += line;
								line=reader.readLine();
							}
							
								if(text.charAt(text.length()-1) != 'm'){
									
									FileWriter filewriter=new FileWriter(file,true);
									filewriter.write(overall+"\r\n");
									filewriter.flush();
									filewriter.close();
								}
							
						} catch (IOException e) {
							e.printStackTrace();
						}
	           }else{
	        	   String case3="3";//Silence
	           		String overall=dateformat.format(currentdate)+" "+hostname+" "+hostIPaddress+" "+case3+" "+ "Silence";
	           		System.out.println(overall);
	           		
	           		String overallsend=hostname+" "+case3;//Silence
		        	byte [] b=overallsend.getBytes();//Transferring the Strings to Bytes
		        	DatagramPacket datagramPacket=new DatagramPacket(b,b.length,IP,port);//creating the packet
		        	try {
						datagramSocket.send(datagramPacket);//send the packet
					} catch (IOException e) {
						e.printStackTrace();
					}//send the packet
	           		
		        	try {
		        	FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\log.txt");
		    		BufferedReader reader=new BufferedReader(fileName);
		        		String text="";
						String line=reader.readLine();
						while(line != null){
							text += line;
							line=reader.readLine();
						}
						
							if(text.charAt(text.length()-1) != 'e'){
								
								FileWriter filewriter=new FileWriter(file,true);
								filewriter.write(overall+"\r\n");
								filewriter.flush();
								filewriter.close();
							}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
	           		
        	   		
	        }
	        }
	    };
	    AudioDispatcher adp = AudioDispatcherFactory.fromDefaultMicrophone(44100,16384, 0);
	    adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 44100, 16384, handler));
	    adp.run(); 
	}
}