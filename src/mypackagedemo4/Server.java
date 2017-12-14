package mypackagedemo4;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {

	public static void main(String[] args) throws IOException {
		
		//Constructing the date
		DateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm a");//To Set the Format of the Date
		Date currentdate = new Date();//To Get the Current Date
		
		//creating a log file for the receiver side
		File file=new File("logserver1.txt");
		if(!file.exists()){
			file.createNewFile();}
		
		//(1)First step is to send a multicast message for all the clients
		InetAddress group=InetAddress.getByName("225.4.5.6");//creating a multicast IP address
		MulticastSocket multicastSocket=new MulticastSocket(3456);//opening a multicast socket port
		multicastSocket.joinGroup(group);//subscribing the multicast IP address to  that socket port,listening to the messages of that IP address from that port
		int portmulticast=3456;
		
		//Sending the log In message to the whole group by a multicast datagram object
		String loginMessage="SEVRON";
		DatagramPacket datagrampacketsentmulticastmessage=new DatagramPacket(loginMessage.getBytes(),loginMessage.length(),group,portmulticast);
		multicastSocket.send(datagrampacketsentmulticastmessage);
		
		//Receiving the "CRQ" message from the Client by a unicast datagram object
		int portunicast=2000;
		byte [] b=new byte[100];
		DatagramSocket datagramSocketunicast=new DatagramSocket(portunicast);//
		DatagramPacket datagramPacketunicastmessage=new DatagramPacket(b, b.length);
		datagramSocketunicast.receive(datagramPacketunicastmessage);
		InetAddress clientIP=datagramPacketunicastmessage.getAddress();//getting the IP of the client side
		String messagerecived=new String(b);
		if(messagerecived=="CRQ"){
			//Sending the "200" means that the server has received the "CRQ" message unicast datagram object
			String acknowledgement="200";
			byte [] byteAcknowledgement=acknowledgement.getBytes();//Transferring the Strings to Bytes
			DatagramPacket datagramPacketUnicast=new DatagramPacket(b,b.length,clientIP,portunicast);//creating the packet
			datagramSocketunicast.send(datagramPacketUnicast);//send the packet
			
			//Receiving the Sound States
			byte [] bsoundstates=new byte[100];
			DatagramPacket datagramPacketSoundStates=new DatagramPacket(bsoundstates, bsoundstates.length);
			datagramSocketunicast.receive(datagramPacketSoundStates);
			String messagerecieved=new String(bsoundstates);
			
			//Sending Acknowledgment to the client to let him know that the server received the Sound State Message
			String acknowledgementSoundState="200";
			byte [] byteAcknowledgementSoundState=acknowledgementSoundState.getBytes();//Transferring the Strings to Bytes
			DatagramPacket datagramPacketUnicastSoundState=new DatagramPacket(byteAcknowledgementSoundState,byteAcknowledgementSoundState.length,clientIP,portunicast);//creating the packet
			datagramSocketunicast.send(datagramPacketUnicastSoundState);//send the packet
			//Identifying the received message
			String state=null;
			if(messagerecieved.equals("SND0")){
				state="Speech";//Speech
			}else if(messagerecieved.equals("SND1")){
				state="Alarm";//Alarm
			}else if(messagerecieved.equals("SND2")){
				state="Silence";//Silence
			}
			//String Contains the received sound state,the date of receiving it and the IP of the client
			String currentState=dateformat.format(currentdate)+" "+datagramPacketSoundStates.getAddress()+" "+state;

			//Write the received state in The Log File Of The Server
			FileWriter fileWriterSoundStates=new FileWriter(file,true);
			fileWriterSoundStates.write(currentState+"\r\n");
			fileWriterSoundStates.flush();
			fileWriterSoundStates.close();
			
		}else{
			//incase if we didn't receive the "CRQ" we will send "500"
			String Disconnect="500";
			byte [] byteDisconnect=Disconnect.getBytes();//Transferring the Strings to Bytes
			DatagramPacket datagramPacketDisconnect=new DatagramPacket(byteDisconnect,byteDisconnect.length,clientIP,portunicast);//creating the packet
			datagramSocketunicast.send(datagramPacketDisconnect);//send the packet
		}//The End of The IF/Else condition
	}
}