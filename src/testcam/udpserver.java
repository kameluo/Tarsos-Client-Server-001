package testcam;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
public class udpserver {

	public static void main(String[] args) throws IOException {
		cam mycam=new cam();
		DatagramSocket datagramSocket=new DatagramSocket(2000);
		
		System.out.println("Ready to Receive Data from The UDP Client");
		while(true){
		byte [] b=new byte[100];
		DatagramPacket datagramPacket=new DatagramPacket(b, b.length);
		datagramSocket.receive(datagramPacket);
		String servermessage =new String(b);
		float serverfreq=Float.valueOf(servermessage);
		
		if(serverfreq > 60 && serverfreq < 175)
        	System.out.println("Recieved From the Client,The Frequency is:"+serverfreq+",its a male voice");
        else if(serverfreq > 180 && serverfreq < 250)
        	System.out.println("Recieved From the Client,The Frequency is:"+serverfreq+",its a female voice");
        else if(serverfreq > 450 && serverfreq < 2600){
        	for(int i=0;i<=5;i++){
        		System.out.println("Recieved From the Client,The Frequency is:"+serverfreq+",its an Alarm");
        		try {
					mycam.main(args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }else
        	System.out.println("Recieved From the Client,The Frequency is:"+serverfreq+",Silence");
	}
	}
}
