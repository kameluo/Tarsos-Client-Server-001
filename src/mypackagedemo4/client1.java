package mypackagedemo4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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

public class client1 implements clientInterface{
	
	private static int client_state; // waiting for connection
	private String oldState;
	private boolean sending;
	
	private void client1(){
		client_state=0; // waiting for connection
		oldState= "SND2";
		sending= false;
	}

	public static void main(String[] args) throws IOException, LineUnavailableException {
		
		//Creating an object from the client class to be able to use the variables inside our program
		client1 client1= new client1();
		
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
 		//Creating an Object for Sending the Multicast messages
		int portmulticast=3456;
		InetAddress group=InetAddress.getByName("225.4.5.6");//creating a multicast IP address
		
		InetSocketAddress socket = new InetSocketAddress("192.168.0.110",portmulticast);
		InetSocketAddress mg = new InetSocketAddress(group,portmulticast);
		NetworkInterface ni = NetworkInterface.getByInetAddress(socket.getAddress());
		MulticastSocket multicastSocket=new MulticastSocket(socket);//opening a multicast socket port
		multicastSocket.joinGroup(mg,ni);//subscribing the multicast IP address to  that socket port,listening to the messages of that IP address from that port
		
		//Sending a "CRQ Request" message to the server to ask for connection
		byte [] byteCRQ=connectionRequest.getBytes();//Transferring the Strings to Bytes
 		DatagramPacket datagramPacketForMultiCastCRQ=new DatagramPacket(byteCRQ,byteCRQ.length,group,portmulticast);//creating the packet
		multicastSocket.send(datagramPacketForMultiCastCRQ);
 		multicastSocket.send(datagramPacketForMultiCastCRQ);//send the packet
 		
		//the unicast part
		int portUniCast=20002;//receiving  port
		//loop here
		
		//Waiting and Receiving The multicast Message from The Server ("SEVRON"-->means that the Server is logging in and waiting for receiving the messages from the sender"clients")
		SocketAddress socket2 = new InetSocketAddress("192.168.0.110",portUniCast);//the IP of This Machine
		String messagerecieved=recievemessage(socket2);
		
		System.out.println(getclientPort());

		//Compare if you are receiving SERVERon ---WHILE LOOP
		boolean test=messagerecieved.equals(serverON);
		System.out.println(test);
		
		//label-break statement is used to give the client a second chance to send the "servON" message if not we will repeat the process but by  
		label:{
			int i;
			for(i=1;i<100;i++){
				if(!messagerecieved.equals(serverON)){
					client1.client_state=2;//"client_state=2" means stop sending data to the server	
					String messageRecievedAgain=recievemessage(socket);
					try {
						TimeUnit.MINUTES.sleep(i);//delay for "i" minutes
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else
					break label;
			}		
		}// the end of label block
		do {
			
		//if The Client Receives "readyToReceive" it means that the Server is Ready to get Receives the Sound States
		if(messagerecieved.equals(serverON)){
			//Sound Detecting Part
			client1.client_state=1; //"client_state=1"means that the server is ready for sending data
			while(client1.client_state==1){
							//creating a memory of array of 3 elements size
							String[] memory=new String[3];
							
							PitchDetectionHandler handler = new PitchDetectionHandler() {
								@Override
						        public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
						           float freq=pitchDetectionResult.getPitch();
						           //System.out.println(freq);
						           
						           String currentState="";
						           if(freq > 60 && freq < 250){
						        	   currentState="SD0";//if condition for detecting  the Speech
						        	   System.out.println("its a speech");
						           }else if(freq > 450 && freq < 2600){
						        	   currentState="SD1";//if condition for detecting  the Alarm
						        	   System.out.println("its an Alarm");
						           }else{
						        	   currentState="SD2";//if condition for detecting  the Silence
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
						        	   while(!client1.clientIP.isReachable(2000)){//not sure about it
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
										//Sending The Current State to The Server Side
										String sendcurrentState=currentState;
										send(sendcurrentState,client1.clientIP,client1.clientPort);
									
										System.out.println(currentState);
										//After Comparing We Will Write in The log file of the client side 
										filewriter.write(dateformat.format(currentdate)+" "+currentState+"\r\n");
										filewriter.flush();
										filewriter.close();
										}
										//if the "previous_message" is more than zero we will send the memory array
										if(previous_message!= 0){
											//Repeat Sending The Memory 3 Times 
											for(int times=1;times<=3;times++){		
												send(memorystring,client1.clientIP,client1.clientPort);
											}//end of the FOR loop
										}
										//Receiving the responded message after sending the Current state Message
											//datagramSocketUniCast.setSoTimeout(2000);// -------------ask juan carlos about deleting this step 1-2-2018 
												String messageRecieved2=recievemessage(socket);
												if(!messageRecieved2.equals(readyToReceive)){
													//Send Again The Current State to The Server Side
													String sendcurrentState=currentState;
													send(sendcurrentState,client1.clientIP,client1.clientPort);
												}else if(messageRecieved2.equals(serverWantsDisconnect)){
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
		}else if(messagerecieved.equals("500")){
			// what are we going to do here?
			client1.client_state=0;//waiting for connection 
			//Sending a "disconnectRequestMessage" message to the server like an acknowledgement,check the clientInterface
			send(disconnectRequestMessage,client1.clientIP,client1.clientPort);
		}else if(messagerecieved.equals(serverWantsDisconnect)){//"555"message
			//"serverWantsDiconnect" means that the server wants to disconnect
			client1.client_state=2;//close and get out of the loop
			//Disconnect message sent to the server to acknowledgement his disconnect request
			send(disconnectRequestMessage,client1.clientIP,client1.clientPort);
			
		}//The End of The IF/Else condition
		
		}while (client1.client_state!=2);
		
	}
	
	//methods --->
	//Send Packets
	public static void send(String message, InetAddress IP, int Port) {
		byte[] buffer=message.getBytes();//Transferring the Strings to Bytes
		System.out.println(IP+"   sending");
		DatagramPacket datagramPacketsend=new DatagramPacket(buffer,buffer.length,IP,20002);//creating the packet
		datagramPacketsend.setPort(20002);
		try {
			DatagramSocket datagramSocketUniCast = new DatagramSocket();
			datagramSocketUniCast.send(datagramPacketsend);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//Receive Packets
	private static InetAddress clientIP;
	private static int clientPort;
	public static String recievemessage(SocketAddress sockect) throws UnknownHostException{
		byte [] buffer=new byte [3];
		DatagramPacket datagrampacket=new DatagramPacket(buffer,buffer.length);
		try {
			DatagramSocket datagramsocket=new DatagramSocket(sockect);
			datagramsocket.receive(datagrampacket);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message=new String(buffer);
		System.out.println(message);
		InetAddress clientIP=datagrampacket.getAddress();
		
		System.out.println(clientIP+"   Receiving");
		setclientIP(clientIP);
		int clientPort=datagrampacket.getPort();
		setclientPort(clientPort);
		return message;
	}
	//Getter and Setter IP and Port
	public static void setclientIP(InetAddress clientIP){
		System.out.println(clientIP+"   setclientIP");
		client1.clientIP=clientIP;
	}
	public static InetAddress getclientIP(){
		return clientIP;
	}
	public static void setclientPort(int clientPort){
		client1.clientPort=clientPort;
	}
	public static int getclientPort(){
		return clientPort;
	}
}