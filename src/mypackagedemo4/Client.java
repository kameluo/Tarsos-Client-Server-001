package mypackagedemo4;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.LineUnavailableException;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.pitch.PitchDetectionHandler;
import core.be.tarsos.dsp.pitch.PitchDetectionResult;
import core.be.tarsos.dsp.pitch.PitchProcessor;
import core.be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

public class Client implements clientInterface{
	
	private int client_state; // waiting for connection
	private String oldState;
	private boolean sending;
	
	private void Client(){
		client_state=0; // waiting for connection
		oldState= "SND2";
		sending= false;
	}

	public static void main(String[] args) throws IOException, LineUnavailableException {
		
		//Creating an object from the client class to be able to use the variables inside our program
		Client client1= new Client();
		
		//Constructing the date
		DateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm a");//To Set the Format of the Date.
		Date currentdate = new Date();//To Get the Current Date.
		
		//Creating The Log File For The Client Side
		File file=new File("logclient1.txt");

		//Creating an object from the FileWriter Class to be able to write in it the states
		FileWriter filewriter=new FileWriter(file,true);//the FileWriter contains 2 arguments,first one is for the file name which is in this case is "file" and the second argument is boolean to allow us to write at the end of the file rather than overwrite and lose our previous data
		
		if(!file.exists()){//checking if the file exists or not,if not it will construct it.
			file.createNewFile();
		}
		//the broadcast part
			InetAddress broadcastIP=InetAddress.getByName("192.168.0.255");
			int portBroadCast=20001;//sending port
			int portUniCast=20002;//receiving port
			//Sending a "CRQ Request" message to the server to ask for connection,(-->datagramPacketForUniCastCRQ2)
			byte [] byteAcknowledgementMessage2=connectionRequest.getBytes();//Transferring the Strings to Bytes
			DatagramPacket datagramPacketForUniCastCRQ2=new DatagramPacket(byteAcknowledgementMessage2,byteAcknowledgementMessage2.length,broadcastIP,portBroadCast);//creating the packet
			DatagramSocket datagramSocketForbroadCast=new DatagramSocket();//creating the socket;
			datagramSocketForbroadCast.send(datagramPacketForUniCastCRQ2);
		 //end of the broadcast
		//loop here
		
		//Waiting and Receiving The multicast Message from The Server ("SEVRON"-->means that the Server is logging in and waiting for receiving the messages from the sender"clients"),(-->datagramPacketmMlticastMessage1)
		byte[] buffermulticast1=new byte[6];
		SocketAddress socket = new InetSocketAddress("192.168.0.106",portUniCast);//the IP of This Machine
		DatagramPacket datagramPacketmUniCastMessage1=new DatagramPacket(buffermulticast1,buffermulticast1.length);
		DatagramSocket datagramSocketUniCast=new DatagramSocket(socket);//creating the socket;
		datagramSocketUniCast.receive(datagramPacketmUniCastMessage1);
		String messageReceivedUnicast=new String(buffermulticast1);
		System.out.println(messageReceivedUnicast);
		
		int x=datagramPacketmUniCastMessage1.getPort();
		System.out.println(x);

		//Compare if you are receiving SERVERon ---WHILE LOOP
		boolean test=messageReceivedUnicast.equals(serverON);
		System.out.println(test);
		
		//label-break statement is used to give the client a second chance to send the "servON" message if not we will repeat the process but by  
		label:{
			int i;
			for(i=1;i<100;i++){
				if(!messageReceivedUnicast.equals(serverON)){
					client1.client_state=2;//"client_state=2" means stop sending data to the server	
					byte[] buffermulticast2=new byte[100];
					DatagramPacket datagramPacketmMlticastMessage2=new DatagramPacket(buffermulticast2,buffermulticast2.length);
					datagramSocketUniCast.receive(datagramPacketmMlticastMessage2);
					String messageReceivedUniCast2=new String(buffermulticast2);
					try {
						TimeUnit.MINUTES.sleep(i);//delay for "i" minutes
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else
					break label;
			}		
		}// the end of label block
		
		//do {
			
		//getting the ip of the server side to use it when we send him unicast datagram socket
		InetAddress serverIP=datagramPacketmUniCastMessage1.getAddress();//from the unicast message we will get the IP of The Server
		int portunicast=datagramPacketmUniCastMessage1.getPort();
		
		/*there is no need for this step now
		//Receiving the responded message after sending the "acknowledgementMessage" Message,(-->datagramPacketForUniCastCRQResponse3)
		byte [] byteAcknowledgementMessageResponse3=new byte[3];
		DatagramPacket datagramPacketForUniCastCRQResponse3=new DatagramPacket(byteAcknowledgementMessageResponse3, byteAcknowledgementMessageResponse3.length);
		datagramSocketUniCast.receive(datagramPacketForUniCastCRQResponse3);
		String messagerecived=new String(byteAcknowledgementMessageResponse3);
		System.out.println(messagerecived);
		*/
		
		do {
			
		//if The Client Receives "readyToReceive" it means that the Server is Ready to get Receives the Sound States
		if(messageReceivedUnicast.equals(serverON)){
			//Sound Detecting Part
			client1.client_state=1; //"client_state=1"means that the server is ready for sending data
			while(client1.client_state==1){
							//creating a memory of array of 3 elements size
							String[] memory=new String[3];
							
							PitchDetectionHandler handler = new PitchDetectionHandler() {
								@Override
						        public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
						           float freq=pitchDetectionResult.getPitch();
						           System.out.println(freq);
						           
						           String currentState="";
						           //if condition for detecting  the Speech
						           if(freq > 60 && freq < 250){
						        	   currentState="SD0";
						        	   System.out.println("its a speech");
					        	   //if condition for detecting  the Alarm
						           }else if(freq > 450 && freq < 2600){
						        	   currentState="SD1";
						        	   System.out.println("its an Alarm");
					        	   //if condition for detecting  the Silence
						           }else{
						        	   currentState="SD2";
						        	   System.out.println("its Silent");
						           }
						           try {
						        	   //checking if the current state is the same like the old state or not
						        	   if (!currentState.equals(client1.oldState)){
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
						        	   
										if (client1.sending==true){
										//Sending The Current State to The Server Side,(-->datagramPacketForUniCastSoundState3)
										String sendcurrentState=currentState;
										byte [] b4=sendcurrentState.getBytes();//Transferring the Strings to Bytes
										DatagramPacket datagramPacketForUniCastSoundState4=new DatagramPacket(b4,b4.length,serverIP,portunicast);
										datagramSocketUniCast.send(datagramPacketForUniCastSoundState4);//send the packet
										System.out.println(currentState);
										//After Comparing We Will Write in The log file of the client side 
										filewriter.write(dateformat.format(currentdate)+" "+currentState+"\r\n");
										filewriter.flush();
										filewriter.close();
										}
										//if the "previous_message" is more than zero we will send the memory array,(-->datagramPacketForUniCastRepeat4)
										if(previous_message!= 0){
											//Repeat Sending The Memory 3 Times 
											for(int times=1;times<=3;times++){										
												byte [] bMemory=memorystring.getBytes();
												DatagramPacket datagramPacketForUniCastRepeat4=new DatagramPacket(bMemory,bMemory.length,serverIP,portunicast);
												datagramSocketUniCast.send(datagramPacketForUniCastRepeat4);
											}//end of the FOR loop
										}
										//Receiving the responded message after sending the Current state Message,(-->datagramPacketunicastmessage5)
										datagramSocketUniCast.setSoTimeout(2000);
											try {
												byte [] b5=new byte[3];
												DatagramPacket datagramPacketunicastmessage5=new DatagramPacket(b5, b5.length);
												datagramSocketUniCast.receive(datagramPacketunicastmessage5);
												String messagerecived2=new String(b5);
												if(!messagerecived2.equals(readyToReceive)){
													//Send Again The Current State to The Server Side,(-->datagramPacketForUniCastRepeat6)
													String sendcurrentState=currentState;
													byte [] bRepeat6=sendcurrentState.getBytes();
													DatagramPacket datagramPacketForUniCastRepeat6=new DatagramPacket(bRepeat6,bRepeat6.length,serverIP,portunicast);
													datagramSocketUniCast.send(datagramPacketForUniCastRepeat6);
												}
											} catch (SocketTimeoutException socketerror) {
												client1.client_state=2;//close and get out of the loop
											}	
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
						}//--------------------------------------------------check here
			
			//should we put those to conditions with the 200 message
		}else if(messageReceivedUnicast.equals("500")){
			// what are we going to do here?
			client1.client_state=0;//waiting for connection 
			//Sending a "disconnectRequestMessage" message to the server like an acknowledgement,(-->datagramPacketdisconnectRequestMessage7),check the clientInterface
			byte [] bytedisconnectRequestMessage7=disconnectRequestMessage.getBytes();//Transferring the Strings to Bytes
			DatagramPacket datagramPacketdisconnectRequestMessage7=new DatagramPacket(bytedisconnectRequestMessage7,bytedisconnectRequestMessage7.length,serverIP,portunicast);//creating the packet
			datagramSocketUniCast.send(datagramPacketdisconnectRequestMessage7);//send the packet
		}else if(messageReceivedUnicast.equals(serverWantsDisconnect)){//"555"message
			//"serverWantsDiconnect" means that the server wants to disconnect
			client1.client_state=2;//close and get out of the loop
			//Disconnect message sent to the server to acknowledgement his disconnect request
			byte [] bytedisconnectRequestMessage7=disconnectRequestMessage.getBytes();//Transferring the Strings to Bytes
			DatagramPacket datagramPacketdisconnectRequestMessage7=new DatagramPacket(bytedisconnectRequestMessage7,bytedisconnectRequestMessage7.length,serverIP,portunicast);//creating the packet
			datagramSocketUniCast.send(datagramPacketdisconnectRequestMessage7);//send the packet
			
			
			//the client class shouldn't be closed
			/*
			//close and disconnect the datagramSocketForUniCast
			datagramSocketForUniCast.close();
			datagramSocketForUniCast.disconnect();
			//leave the multicastSocket
			multicastSocket.leaveGroup(group);
			*/

		}//The End of The IF/Else condition
		
		}while (client1.client_state!=2);
	}
}
