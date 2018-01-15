package mypackagedemo4;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.Character;

public class Server {

	public static void main(String[] args) throws IOException {
		
		int serverstate;//flag
		
		//Constructing the date
		DateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm a");//To Set the Format of the Date
		Date currentdate = new Date();//To Get the Current Date
		
		//creating a log file for the receiver side
		File file=new File("logserver1.txt");
		if(!file.exists()){
			file.createNewFile();
			}
		
		//First step is to send a multicast message for all the clients
		InetAddress group=InetAddress.getByName("225.4.5.6");//creating a multicast IP address
		MulticastSocket multicastSocket=new MulticastSocket(3456);//opening a multicast socket port
		multicastSocket.joinGroup(group);//subscribing the multicast IP address to  that socket port,listening to the messages of that IP address from that port
		int portmulticast=3456;
		
		//Sending the log In message to the whole group by a multicast datagram object,(-->datagrampacketsentmulticastmessage1)
		String loginMessage="SEVRON";
		DatagramPacket datagrampacketsentmulticastmessage1=new DatagramPacket(loginMessage.getBytes(),loginMessage.length(),group,portmulticast);
		multicastSocket.send(datagrampacketsentmulticastmessage1);
		
		
		
		//Receiving the "CRQ" message from the Client by a unicast datagram object,(-->datagrampacketsentmulticastmessage2)
		int portunicast=2000;
		byte [] b2=new byte[100];
		DatagramSocket datagramSocketunicast=new DatagramSocket(portunicast);//creating an object from the datasocket class to send all the unicast packages through it
		DatagramPacket datagramPacketunicastmessage2=new DatagramPacket(b2, b2.length);
		datagramSocketunicast.receive(datagramPacketunicastmessage2);
		InetAddress clientIP=datagramPacketunicastmessage2.getAddress();//getting the IP of the client side
		b2=datagramPacketunicastmessage2.getData();
		String messagereceived=new String (b2);
		System.out.println(messagereceived);
		String compare=new String("CRQ");
		char firstCahracterReceivedMessage= messagereceived.charAt(0);
		System.out.println("outt the if condition");
		//if(messagereceived.equals(compare)){
		if(messagereceived.contains("R")){
			
			System.out.println("inside the if condition");
			//Sending the "200" means that the server has received the "CRQ" message unicast datagram object,(-->datagrampacketsentmulticastmessage3)
			String acknowledgement="200";
			byte [] byteAcknowledgement=acknowledgement.getBytes();//Transferring the Strings to Bytes
			DatagramPacket datagramPacketUnicast3=new DatagramPacket(byteAcknowledgement,byteAcknowledgement.length,clientIP,portunicast);//creating the packet
			datagramSocketunicast.send(datagramPacketUnicast3);//send the packet
			serverstate=1;//the server is ready to receive 
			while(true){
				if(serverstate == 1){                             }
				//Receiving the Sound States,(-->datagramPacketSoundStates4)
				byte [] bsoundstates=new byte[100];
				DatagramPacket datagramPacketSoundStates4=new DatagramPacket(bsoundstates, bsoundstates.length);
				datagramSocketunicast.receive(datagramPacketSoundStates4);
				String soundStateMessageRecieved=new String(bsoundstates);
				System.out.println(soundStateMessageRecieved);
				
				//Sending Acknowledgment to the client to let him know that the server received the Sound State Message,(-->datagramPacketUnicastSoundState5)
				String acknowledgementSoundState="200";
				byte [] byteAcknowledgementSoundState=acknowledgementSoundState.getBytes();//Transferring the Strings to Bytes
				DatagramPacket datagramPacketUnicastSoundState5=new DatagramPacket(byteAcknowledgementSoundState,byteAcknowledgementSoundState.length,clientIP,portunicast);//creating the packet
				datagramSocketunicast.send(datagramPacketUnicastSoundState5);//send the packet
				//Identifying the received message
				String soundState="";
					if(soundStateMessageRecieved.charAt(3)=='0'){
						soundState="Speech";//Speech=SND0
						System.out.println(soundState);
					}else if(soundStateMessageRecieved.charAt(3)=='1'){
						soundState="Alarm";//Alarm=SND1
						System.out.println(soundState);
					}else if(soundStateMessageRecieved.charAt(3)=='2'){
						soundState="Silence";//Silence==SND2
						System.out.println(soundState);
					}else if(soundStateMessageRecieved.charAt(2)=='Q'){
						//Receiving "DRQ" from the client means that he will disconnect
						//close and disconnect the datagramSocketForUniCast
						datagramSocketunicast.close();
						datagramSocketunicast.disconnect();
						//leave the multicastSocket
						multicastSocket.leaveGroup(group);
						serverstate=0;
					}else{
						//if the client sends something else rather than the sound states or the disconnect message we will send him "500" message,(-->datagramPacketUnicastunknownCommandMessage6)
						System.out.println("UnKnown Command !!!");
						String unknownCommandMessage="500";
						byte [] byteunknownCommandMessage=unknownCommandMessage.getBytes();//Transferring the Strings to Bytes
						DatagramPacket datagramPacketUnicastunknownCommandMessage6=new DatagramPacket(byteunknownCommandMessage,byteunknownCommandMessage.length,clientIP,portunicast);//creating the packet
						datagramSocketunicast.send(datagramPacketUnicastunknownCommandMessage6);//send the packet
					}
				//String Contains the received sound state,the date of receiving it and the IP of the client
				String currentState=dateformat.format(currentdate)+" "+datagramPacketUnicastSoundState5.getAddress()+" "+soundState;
	
				//Write the received state in The Log File Of The Server
				FileWriter fileWriterSoundStates=new FileWriter(file,true);
				fileWriterSoundStates.write(currentState+"\r\n");
				fileWriterSoundStates.flush();
				fileWriterSoundStates.close();
			}//the end of the infinite while loop
			
		}else{
			//incase if we didn't receive the "CRQ" we will send "500",,(-->datagramPacketUnicastunknownCommandMessage7)
			serverstate=0;
			String Disconnect="555";
			byte [] byteDisconnect=Disconnect.getBytes();//Transferring the Strings to Bytes
			DatagramPacket datagramPacketDisconnect=new DatagramPacket(byteDisconnect,byteDisconnect.length,clientIP,portunicast);//creating the packet
			datagramSocketunicast.send(datagramPacketDisconnect);//send the packet
		}//The End of The IF/Else condition
	}
}