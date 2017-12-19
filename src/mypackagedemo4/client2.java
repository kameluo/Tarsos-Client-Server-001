package mypackagedemo4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import javax.sound.sampled.LineUnavailableException;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.pitch.PitchDetectionHandler;
import core.be.tarsos.dsp.pitch.PitchDetectionResult;
import core.be.tarsos.dsp.pitch.PitchProcessor;
import core.be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

public class client2 {

	public static void main(String[] args) throws IOException, LineUnavailableException {
		
		//Constructing the date
		DateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm a");//To Set the Format of the Date.
		Date currentdate = new Date();//To Get the Current Date.
		
		//Creating The Log File For The Client Side
		File file=new File("logclient1.txt");
	
		String oldState= "SND2";
		boolean sending= false;
		int client_state=0; // waiting for connection
		
		//Creating an object from the FileWriter Class to be able to write in it the states
		FileWriter filewriter=new FileWriter(file,true);//the FileWriter contains 2 arguments,first one is for the file name which is in this case is "file" and the second argument is boolean to allow us to write at the end of the file rather than overwrite and lose our previous data
		
		if(!file.exists()){//checking if the file exists or not,if not it will construct it.
			file.createNewFile();
			
			//initial state Silence,to avoid the error when we read the last state before we write in the file for the first time,check it (text.length()-1))
			filewriter.write(dateformat.format(currentdate)+" SND2"+"\r\n");// SND2 stands for Silence
		
			
			filewriter.flush();
			filewriter.close();
		}
		
		//Creating an Object for Sending the Multicast messages
		InetAddress group=InetAddress.getByName("225.4.5.6");//creating a multicast IP address
		MulticastSocket multicastSocket=new MulticastSocket(3456);//opening a multicast socket port
		multicastSocket.joinGroup(group);//subscribing the multicast IP address to  that socket port,listening to the messages of that IP address from that port
		int portmulticast=3456;
		
		//do {
		
		//Waiting and Receiving The multicast Message from The Server ("SEVRON"-->means that the Server is logging in and waiting for receiving the messages from the sender"clients")
		//Compare if you are receiving SERVERon ---WHILE LOOP
		byte[] buffer=new byte[100];
		DatagramPacket datagramPacketmMlticastMessage=new DatagramPacket(buffer,buffer.length);
		multicastSocket.receive(datagramPacketmMlticastMessage);
		String messageReceivedMulticast=new String(buffer);
		System.out.println(messageReceivedMulticast);
		InetAddress serverIP=datagramPacketmMlticastMessage.getAddress();//from the multicast message we will get the IP of The Server to use it when we send the unicast packet
		
		//Creating an Object for Sending the Unicast messages for each sound case
		DatagramSocket datagramSocketForUniCast=new DatagramSocket();//creating the socket;
		int portunicast=2000;
				
		//Sending a "CRQ" message to the receiver like an acknowledgement
		String acknowledgementMessage="CRQ";
		byte [] byteAcknowledgementMessage=acknowledgementMessage.getBytes();//Transferring the Strings to Bytes
		DatagramPacket datagramPacketForUniCastCRQ=new DatagramPacket(byteAcknowledgementMessage,byteAcknowledgementMessage.length,serverIP,portunicast);//creating the packet
		datagramSocketForUniCast.send(datagramPacketForUniCastCRQ);//send the packet
		
		//Receiving the responded message after sending the "CRQ" Message
		byte [] byteAcknowledgementMessageResponse=new byte[100];
		DatagramPacket datagramPacketForUniCastCRQResponse=new DatagramPacket(byteAcknowledgementMessageResponse, byteAcknowledgementMessageResponse.length);
		datagramSocketForUniCast.receive(datagramPacketForUniCastCRQResponse);
		String messagerecived=new String(byteAcknowledgementMessageResponse);
		System.out.println(messagerecived);
		
		//if The Client Receives "200" it means that the Server is Ready to get Receives the Sound States
		if(messagerecived.equals("200")){
			//Sound Detecting Part
			client_state=1; // sending data
			while(client_state== 1){
			
							//creating a memory of array of 3 elements size
							String[] memory=new String[3];
							
							PitchDetectionHandler handler = new PitchDetectionHandler() {
								@Override
						        public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
						           float freq=pitchDetectionResult.getPitch();
						           System.out.println(freq);
						           
						           String currentState;
						           //if condition for detecting  the Speech
						           if(freq > 60 && freq < 250){
						        	   currentState="SND0";
					        	   //if condition for detecting  the Alarm
						           }else if(freq > 450 && freq < 2600){
						        	   currentState="SND1";
					        	   //if condition for detecting  the Silence
						           }else{
						        	   currentState="SND2";
						           }
						           try {
						        	   
						        	   /*
									//first we will check"read" the last Word in the log file of the client Log File to not repeat it again,if it is different we will write it 
						        	   		//Reading The Whole File	
						        	   		FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\logclient1.txt");
						        	   		BufferedReader reader=new BufferedReader(fileName);
											String text="";
											String line=reader.readLine();
											
											
											while(line != null){
												text += line;
												line=reader.readLine();
											}
											//checking the last letter in the log file,if its not the "0" (stands for "SND0"-->Speech)
											String lastWord=null;
											if(text.charAt(text.length()-1) == '0'){
												lastWord="SND0";//(stands for "SND0"-->Speech)
											}else if(text.charAt(text.length()-1) == '1'){
												lastWord="SND1";//(stands for "SND1"-->Alarm)
											}else if(text.charAt(text.length()-1) == '2'){
												lastWord="SND2";//(stands for "SND1"-->Silence)
											}
											
											*/
											if (!currentState.equals(oldState)){
												oldState=currentState;
												sending=true;
											}						
											/*
											//Comparing If The Last Word In The Log File Of The Client Is The Same Like The Current State
											if(!currentState.equals(lastWord)){
														//Inserting The Latest 3 Sound States In The Memory
														for(int x=0;x<memory.length;x++){
															if(x!=memory.length){
																memory[x]=currentState;
																//if the array is full
																}else if(x==memory.length){
																	String memorySecondElement=memory[1];
																	String memoryThirdElement=memory[2];
																	memory[0]=memorySecondElement;
																	memory[1]=memoryThirdElement;
																	memory[2]=currentState;
															}
														}//The End Of The Memory	
												//After Comparing We Will Write in The log file of the client side 
												FileWriter filewriterStates=new FileWriter(file,true);
												filewriterStates.write(dateformat.format(currentdate)+currentState+"\r\n");
												filewriterStates.flush();
												filewriterStates.close();
												*/
												
												int previous_message=0;
											
												if (sending){
												//Sending The Current State to The Server Side
												String sendcurrentState=currentState;
												byte [] b1=sendcurrentState.getBytes();//Transferring the Strings to Bytes
												if (previous_message!= 0){
													
													
													}
												
												DatagramPacket datagramPacketForUniCast=new DatagramPacket(b1,b1.length,serverIP,portunicast);
												datagramSocketForUniCast.send(datagramPacketForUniCast);//send the packet
												
												//Receiving the responded message after sending the Current state Message
												byte [] b2=new byte[100];
												DatagramSocket datagramSocket=new DatagramSocket(portunicast);
												DatagramPacket datagramPacketunicastmessage=new DatagramPacket(b2, b2.length);
												datagramSocket.receive(datagramPacketunicastmessage);
												String messagerecived=new String(b2);
												if(!messagerecived.equals("200")){
													
												client_state=2;
												//get out of the loop
													
													//Send Again The Current State to The Server Side
													String sendcurrentStateRepeat=currentState;
													byte [] bRepeat=sendcurrentState.getBytes();
													DatagramPacket datagramPacketForUniCastRepeat=new DatagramPacket(bRepeat,bRepeat.length,serverIP,portunicast);
													datagramSocketForUniCast.send(datagramPacketForUniCastRepeat);
												}
												//while(messagerecived==null){/////////////
												while(!serverIP.isReachable(2000)){//not sure about it
													//Repeat Sending The Memory 3 Times 
													for(int times=1;times<=3;times++){
													//converting the memory elements into a One String
													String memorystring=String.join(",",memory);
													byte [] bMemory=memorystring.getBytes();
													DatagramPacket datagramPacketForUniCastRepeat=new DatagramPacket(bMemory,bMemory.length,serverIP,portunicast);
													datagramSocketForUniCast.send(datagramPacketForUniCastRepeat);
													}//end of the FOR loop
												}//The End of WHILE loop	
											}//The End of the Comparing IF condition	
									}catch (FileNotFoundException e) {
										e.printStackTrace();
									}catch (IOException e) {
										e.printStackTrace();
									} 
						        }//The End of The "handlePitch" Override method in The PitchDetectionHandler-handler Object Class
						    };//The End Of The PitchDetectionHandler-handler Object Class
						    AudioDispatcher adp = AudioDispatcherFactory.fromDefaultMicrophone(44100,16384, 0);
						    adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 44100, 16384, handler));
						    adp.run(); 
							}
						}//the end of the infinite while loop
		    
		  //if The Client Receives "200" it means that the Server is Ready to get Receives the Sound States	
		}else if(messagerecived.equals("500")){
			// what are we going to do here?
			//send (drq)message
			client_State=0;
			
		}else if(messagerecived.equals("555")){
			client_State=2;
			//"555" means that the server wants to disconnect
			//close and disconnect the datagramSocketForUniCast
			datagramSocketForUniCast.close();
			datagramSocketForUniCast.disconnect();
			//leave the multicastSocket
			multicastSocket.leaveGroup(group);
		}//The End of The IF/Else condition
		
		
	    
		//}while (client_State!=2)
			
		
		}

}
