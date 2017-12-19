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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.sound.sampled.LineUnavailableException;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.pitch.PitchDetectionHandler;
import core.be.tarsos.dsp.pitch.PitchDetectionResult;
import core.be.tarsos.dsp.pitch.PitchProcessor;
import core.be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

public class Client {
	
	private int client_state; // waiting for connection
	private String oldState;
	private boolean sending;
	
	private void Client(){
		client_state=0; // waiting for connection
		oldState= "SND2";
		sending= false;
		
	}

	public static void main(String[] args) throws IOException, LineUnavailableException {
		
		//Constructing the date
		
		Client client1= new Client();
		
		DateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm a");//To Set the Format of the Date.
		Date currentdate = new Date();//To Get the Current Date.
		
		//Creating The Log File For The Client Side
		File file=new File("logclient1.txt");

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
	

		do {
			
		//Waiting and Receiving The multicast Message from The Server ("SEVRON"-->means that the Server is logging in and waiting for receiving the messages from the sender"clients")
		byte[] buffer=new byte[100];
		DatagramPacket datagramPacketmMlticastMessage=new DatagramPacket(buffer,buffer.length);
		multicastSocket.receive(datagramPacketmMlticastMessage);
		String messageReceivedMulticast=new String(buffer);
		System.out.println(messageReceivedMulticast);
		//Compare if you are receiving SERVERon ---WHILE LOOP
		if(messageReceivedMulticast.equals(new String("SEVRON"))){
			client1.client_state=2;
		}
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
			client1.client_state=1; //sending data
			while(client1.client_state==1){
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
						        	   if (!currentState.equals(new String(client1.oldState))){
						        		   client1.oldState=currentState;
						        		   client1.sending=true;
						        	   }	
						        	   int previous_message=0;
						        	   String memorystring ="";
						        	   while(!serverIP.isReachable(2000)){//not sure about it
						        		   //Inserting The Latest 3 Sound States In The Memory Array
											for(int x=0;x<memory.length;x++){
												if(x!=memory.length){
													memory[x]=client1.oldState;
												}
											//converting the memory elements into a One String
											memorystring=String.join(",",memory);
											previous_message++;
											}
										}//The End of unReachable WHILE loop
						        	   
										if (client1.sending){
										//Sending The Current State to The Server Side
										String sendcurrentState=currentState;
										byte [] b1=sendcurrentState.getBytes();//Transferring the Strings to Bytes
										DatagramPacket datagramPacketForUniCast=new DatagramPacket(b1,b1.length,serverIP,portunicast);
										datagramSocketForUniCast.send(datagramPacketForUniCast);//send the packet
										
										//After Comparing We Will Write in The log file of the client side 
										FileWriter filewriterStates=new FileWriter(file,true);
										filewriterStates.write(dateformat.format(currentdate)+currentState+"\r\n");
										filewriterStates.flush();
										filewriterStates.close();
										}
										//if the "previous_message" is more than zero we will send the memory array
										if(previous_message!= 0){
											//Repeat Sending The Memory 3 Times 
											for(int times=1;times<=3;times++){										
												byte [] bMemory=memorystring.getBytes();
												DatagramPacket datagramPacketForUniCastRepeat=new DatagramPacket(bMemory,bMemory.length,serverIP,portunicast);
												datagramSocketForUniCast.send(datagramPacketForUniCastRepeat);
											}//end of the FOR loop
										}
										//Receiving the responded message after sending the Current state Message
										datagramSocketForUniCast.setSoTimeout(2000);
											try {
												byte [] b2=new byte[100];
												DatagramPacket datagramPacketunicastmessage=new DatagramPacket(b2, b2.length);
												datagramSocketForUniCast.receive(datagramPacketunicastmessage);
												String messagerecived=new String(b2);
												if(!messagerecived.equals(new String("200"))){
													//Send Again The Current State to The Server Side
													String sendcurrentState=currentState;
													byte [] bRepeat=sendcurrentState.getBytes();
													DatagramPacket datagramPacketForUniCastRepeat=new DatagramPacket(bRepeat,bRepeat.length,serverIP,portunicast);
													datagramSocketForUniCast.send(datagramPacketForUniCastRepeat);
												}
											} catch (SocketTimeoutException socketerror) {
												client1.client_state=2;//close and get out of the loop
											}	
						        	   }catch (FileNotFoundException e) {
										e.printStackTrace();
									}
								catch (IOException e) {
										e.printStackTrace();
									} 
						        }//The End of The "handlePitch" Override method in The PitchDetectionHandler-handler Object Class
						    };//The End Of The PitchDetectionHandler-handler Object Class
						    AudioDispatcher adp = AudioDispatcherFactory.fromDefaultMicrophone(44100,16384, 0);
						    adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 44100, 16384, handler));
						    adp.run(); 
						}//the end of the while loop
		  //if The Client Receives "200" it means that the Server is Ready to get Receives the Sound States	
		}else if(messagerecived.equals(new String("500"))){
			// what are we going to do here?
			client1.client_state=0;//waiting for connection 
			//Sending a "DRQ" message to the receiver like an acknowledgement
			String disconnectRequestMessage="DRQ";
			byte [] bytedisconnectRequestMessage=disconnectRequestMessage.getBytes();//Transferring the Strings to Bytes
			DatagramPacket datagramPacketdisconnectRequestMessage=new DatagramPacket(bytedisconnectRequestMessage,bytedisconnectRequestMessage.length,serverIP,portunicast);//creating the packet
			datagramSocketForUniCast.send(datagramPacketForUniCastCRQ);//send the packet
		}else if(messagerecived.equals(new String("555"))){
			//"555" means that the server wants to disconnect
			client1.client_state=2;//close and get out of the loop
			//close and disconnect the datagramSocketForUniCast
			datagramSocketForUniCast.close();
			datagramSocketForUniCast.disconnect();
			//leave the multicastSocket
			multicastSocket.leaveGroup(group);
		}//The End of The IF/Else condition
		
		}while (client1.client_state!=2);
	    
	}

}
