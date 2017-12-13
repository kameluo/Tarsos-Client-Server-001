package mypackagedemo1;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

	public static void main(String args[]) throws LineUnavailableException, IOException {
		DatagramSocket datagramSocket=new DatagramSocket();//creating the socket;
		InetAddress IP=InetAddress.getByName("localhost");//getting the IP
		String hostname=IP.getHostName();
		int port=2000;
		//String clientnumber=args[0];
		System.out.println(hostname +" is connected");
        PitchDetectionHandler handler = new PitchDetectionHandler() {
			@Override
	        public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
	           //System.out.println(audioEvent.getTimeStamp() + " " + pitchDetectionResult.getPitch());
	           float freq=pitchDetectionResult.getPitch();
	           
	           if(freq > 60 && freq < 250){
	        	   String case1="00";//Speech
	        	   System.out.println(freq);
	        	   String overall=hostname+" "+case1+" "+IP.getHostAddress();
	        	   byte [] b=overall.getBytes();//Transferring the Strings to Bytes
	        	   DatagramPacket datagramPacket=new DatagramPacket(b,b.length,IP,port);//creating the packet
	        	   try {
					datagramSocket.send(datagramPacket);//send the packet
				} catch (IOException e) {
					e.printStackTrace();
				}
	           }else if(freq > 450 && freq < 2600){
	        	   String case2="01";//Alarm
	        	   System.out.println(freq);
	        	   String overall=hostname+" "+case2+" "+IP.getHostAddress();
	        	   byte [] b=overall.getBytes();//Transferring the Strings to Bytes
	        	   DatagramPacket datagramPacket=new DatagramPacket(b,b.length,IP,port);//creating the packet
	        	   
	        	   try {
					datagramSocket.send(datagramPacket);//send the packet
				} catch (IOException e) {
					e.printStackTrace();
				}
	           }else{
	        	   String case3="02";//Silence
	        	   System.out.println(freq);
	           		String overall=hostname+" "+case3+" "+IP.getHostAddress();
        	   		byte [] b=overall.getBytes();//Transferring the Strings to Bytes
        	   		DatagramPacket datagramPacket=new DatagramPacket(b,b.length,IP,port);//creating the packet
        	   		try {
						datagramSocket.send(datagramPacket);//send the packet
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