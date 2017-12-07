package testcam;

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

	public static void main(String[] args) throws LineUnavailableException, IOException {
		DatagramSocket datagramSocket=new DatagramSocket();//creating the socket;
		InetAddress IP=InetAddress.getByName("localhost");//getting the IP
		int port=2000;
		
		PitchDetectionHandler handler = new PitchDetectionHandler() {
			@Override
	        public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
	           //System.out.println(audioEvent.getTimeStamp() + " " + pitchDetectionResult.getPitch());
	           float freq=pitchDetectionResult.getPitch();
	           String stringfreq=String.valueOf(freq);
	           byte [] b=stringfreq.getBytes();//Transferring the String to Bytes
	           DatagramPacket datagramPacket=new DatagramPacket(b,b.length,IP,port);//creating the packet
	   			try {
					datagramSocket.send(datagramPacket);//send the packet
				} catch (IOException e) {
					e.printStackTrace();
				}
	           
	        }
	    };
		
	    AudioDispatcher adp = AudioDispatcherFactory.fromDefaultMicrophone(44100,16384, 0);
	    adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 44100, 16384, handler));
	    adp.run();
	}
}
