package mypackagedemo4;

import java.io.File;
import java.io.FileInputStream;
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
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.lang.Object;
import javax.sound.sampled.LineUnavailableException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.AudioProcessor;
import core.be.tarsos.dsp.io.TarsosDSPAudioFormat;
import core.be.tarsos.dsp.io.UniversalAudioInputStream;
import core.be.tarsos.dsp.mfcc.MFCC;
import core.be.tarsos.dsp.pitch.PitchDetectionHandler;
import core.be.tarsos.dsp.pitch.PitchDetectionResult;
import core.be.tarsos.dsp.pitch.PitchProcessor;
import core.be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

import marytts.util.math.MathUtils;



public class client1 implements clientInterface {

	private static int client_state = 0; // waiting for connection
	private static String oldState = "";
	private static boolean sending = false;
	
	

	public static void main(String[] args) throws IOException, LineUnavailableException {
		//Extracting the data from the Excel Sheets
		readExcel readexcel=new readExcel();
		readexcel.readExcelsheets();
		
		// Constructing the date
		DateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm a");// To Set the Format of the Date.
		Date currentdate = new Date();// To Get the Current Date.

		// Creating an Object for Sending the Multicast messages
		int portmulticast = 3456;
		InetAddress group = InetAddress.getByName("225.4.5.6");// creating a multicast IP address

		InetSocketAddress socket = new InetSocketAddress("192.168.0.100", portmulticast);// the IP of this machine
		InetSocketAddress mg = new InetSocketAddress(group, portmulticast);
		NetworkInterface ni = NetworkInterface.getByInetAddress(socket.getAddress());
		MulticastSocket multicastSocket = new MulticastSocket(socket);// opening a multicast socket port
		multicastSocket.joinGroup(mg, ni);// subscribing the multicast IP address to that socket port,listening to the
											// messages of that IP address from that port

		// Sending a "CRQ Request" message to the server to ask for connection
		byte[] byteCRQ = connectionRequest.getBytes();// Transferring the Strings to Bytes
		DatagramPacket datagramPacketForMultiCastCRQ = new DatagramPacket(byteCRQ, byteCRQ.length, group,
				portmulticast);// creating the packet
		multicastSocket.send(datagramPacketForMultiCastCRQ);// send the packet

		// the unicast part
		int portUniCast = 20002;// receiving port
		// loop here

		// Waiting and Receiving The multicast Message from The Server ("SEVRON"-->means
		// that the Server is logging in and waiting for receiving the messages from the
		// sender"clients")
		SocketAddress socket2 = new InetSocketAddress("192.168.0.100", portUniCast);// the IP of This Machine
		String messagerecieved = recievemessage(socket2);

		System.out.println(getclientPort());

		// Compare if you are receiving SERVERon ---WHILE LOOP
		boolean test = messagerecieved.equals(serverON);
		System.out.println(test);

		// label-break statement is used to give the client a second chance to send the
		// "servON" message if not we will repeat the process but by
		label: {
			int i;
			for (i = 1; i < 100; i++) {
				if (!messagerecieved.equals(serverON)) {
					client_state = 2;// "client_state=2" means stop sending data to the server
					String messageRecievedAgain = recievemessage(socket2);
					try {
						TimeUnit.MINUTES.sleep(i);// delay for "i" minutes
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else
					break label;
			}
		} // the end of label block
		do {

			// if The Client Receives "readyToReceive" it means that the Server is Ready to
			// get Receives the Sound States
			if (messagerecieved.equals(serverON)) {
				// Sound Detecting Part
				client_state = 1; // "client_state=1"means that the server is ready for sending data
				while (client_state == 1) {
					// creating a memory of array of 3 elements size
					String[] memory = new String[3];
					// sending=false;

					PitchDetectionHandler handler = new PitchDetectionHandler() {
						@Override
						public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
							float freq = pitchDetectionResult.getPitch();
							// System.out.println(freq);

							String currentState = "";
							if (freq > 60 && freq <= 250) {
								currentState = "SD0";// if condition for detecting the Speech
								System.out.println("its a speech");
							} else if (freq > 450 && freq < 2600) {
								currentState = "SD1";// if condition for detecting the Alarm
								System.out.println("its an Alarm");
							} else {
								currentState = "SD2";// if condition for detecting the Silence
								System.out.println("its Silent");
							}
							try {
								// checking if the current state is the same like the old state or not
								if (!currentState.equals(oldState)) {
									oldState = currentState;
									// sending=true;

									int previous_message = 0;
									String memorystring = "";
									while (!serverIP.isReachable(2000)) {// not sure about it
										// Inserting The Latest 3 Sound States In The Memory Array
										for (int x = 0; x < memory.length; x++) {
											if (x != memory.length) {
												memory[x] = oldState;
											}
											// converting the memory elements into a One String
											memorystring = String.join(",", memory);
											previous_message++;
										}
									} // The End of unReachable WHILE loop

									// if (sending){
									// Sending The Current State to The Server Side
									send(currentState, serverIP, serverPort);

									System.out.println(currentState);

									// After Comparing We Will Write in The log file of the client side
									writeToFile(dateformat, currentdate, currentState);
									
									try {
									// send the sound state and date to the database
									//	getConnection(dateformat.format(currentdate),currentState);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// }
									// if the "previous_message" is more than zero we will send the memory array
									if (previous_message != 0) {
										// Repeat Sending The Memory 3 Times
										for (int times = 1; times <= 3; times++) {
											send(memorystring, serverIP, serverPort);
										} // end of the FOR loop
									}
									// Receiving the responded message after sending the Current state Message
									// datagramSocketUniCast.setSoTimeout(2000);// -------------ask juan carlos
									// about deleting this step 1-2-2018
									String messageRecieved2 = recievemessage(socket2);
									if (!messageRecieved2.equals(readyToReceive)) {
										// Send Again The Current State to The Server Side
										String sendcurrentState = currentState;
										send(sendcurrentState, serverIP, serverPort);
									} else if (messageRecieved2.equals(serverWantsDisconnect)) {
										client_state = 2;// close and get out of the loop
									}
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}

						}// The End of The "handlePitch" Override method in The
							// PitchDetectionHandler-handler Object Class
					};// The End Of The PitchDetectionHandler-handler Object Class
					AudioDispatcher adp = AudioDispatcherFactory.fromDefaultMicrophone(16000, 400, 0);
					adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 16000, 400, handler));
					
					MFCC mfcc = new MFCC(400,16000f,13,20,300f,3700f);
					adp.addAudioProcessor(mfcc);
					adp.addAudioProcessor(new AudioProcessor() {
				        @Override
				        public void processingFinished() {
				            System.out.println("DONE");
				        }
				        @Override
				        public boolean process(AudioEvent audioEvent) {
				        		float[] mfccArray = mfcc.getMFCC();
				        		
				        		
				        		processExcel.sendRealTime(mfccArray);
				        		
				        		if(processExcel.arraylistMFCCrealtime.size()>=40) {
				        		
				        		float[] mfccAverageArray=processExcel.averageAndClearRealTime();
					        		double[] mfccArrayDouble=new double[13];
					        		for(int k=0;k<=12;k++) {
					        			mfccArrayDouble[k]=mfccAverageArray[k];
					        		}
					        	String category=processExcel.processExcel(mfccArrayDouble);
					        	System.out.println("The Category is:"+category);
				        		}
					       return true;
				        }
				    });
					adp.run();
					
					
				} // --------------------------------------------------check here

				// should we put those to conditions with the 200 message
			} else if (messagerecieved.equals("500")) {
				// what are we going to do here?
				client_state = 0;// waiting for connection
				// Sending a "disconnectRequestMessage" message to the server like an
				// acknowledgement,check the clientInterface
				send(disconnectRequestMessage, serverIP, serverPort);
			} else if (messagerecieved.equals(serverWantsDisconnect)) {// "555"message
				// "serverWantsDiconnect" means that the server wants to disconnect
				client_state = 2;// close and get out of the loop
				// Disconnect message sent to the server to acknowledgement his disconnect
				// request
				send(disconnectRequestMessage, serverIP, serverPort);

			} // The End of The IF/Else condition

		} while (client_state != 2);
	}

	// methods --->
	// Send Packets
	public static void send(String message, InetAddress IP, int Port) {
		byte[] buffer = message.getBytes();// Transferring the Strings to Bytes
		DatagramPacket datagramPacketsend = new DatagramPacket(buffer, buffer.length, IP, 20002);// creating the packet
		datagramPacketsend.setPort(20002);
		try {
			DatagramSocket datagramSocketUniCast = new DatagramSocket();
			datagramSocketUniCast.send(datagramPacketsend);
			datagramSocketUniCast.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Receive Packets
	private static InetAddress serverIP;
	private static int serverPort;

	public static String recievemessage(SocketAddress sockect) throws UnknownHostException {
		byte[] buffer = new byte[3];
		DatagramPacket datagrampacket = new DatagramPacket(buffer, buffer.length);
		try {
			DatagramSocket datagramsocket = new DatagramSocket(sockect);
			datagramsocket.setReuseAddress(true);
			datagramsocket.receive(datagrampacket);
			datagramsocket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(buffer);
		System.out.println(message);
		InetAddress serverIP = datagrampacket.getAddress();

		setclientIP(serverIP);
		int serverPort = datagrampacket.getPort();
		setclientPort(serverPort);
		return message;
	}

	// Getter and Setter IP and Port
	public static void setclientIP(InetAddress serverIP) {
		client1.serverIP = serverIP;
	}

	public static InetAddress getclientIP() {
		return serverIP;
	}

	public static void setclientPort(int serverPort) {
		client1.serverPort = serverPort;
	}

	public static int getclientPort() {
		return serverPort;
	}

	public static void writeToFile(DateFormat dateformat, Date currentdate, String currentstate) {
		// Creating The Log File For The Client Side
		File file = new File("logclient1.txt");
		try {
			FileWriter filewriter = new FileWriter(file, true);// the FileWriter contains 2 arguments,first one is for
																// the file name which is in this case is "file" and the
																// second argument is boolean to allow us to write at
																// the end of the file rather than overwrite and lose
																// our previous data

			if (!file.exists()) {// checking if the file exists or not,if not it will construct it.
				file.createNewFile();
			}
			// After Comparing We Will Write in The log file of the client side
			filewriter.write(dateformat.format(currentdate) + " " + currentstate + "\r\n");
			filewriter.flush();
			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*
	public static Connection getConnection(String date,String soundstate) throws Exception{
		try{ 
			Class.forName("com.mysql.jdbc.Driver"); 
			Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/demo?autoReconnect=true&useSSL=false","root","1234"); 
			String create="CREATE TABLE IF NOT EXISTS soundstatetable(id int NOT NULL AUTO_INCREMENT PRIMARY KEY,date VARCHAR(255) NOT NULL,soundstate VARCHAR(255) NOT NULL)";
			Statement statement=conn.createStatement();	
			statement.executeUpdate(create);
			String sqlstatment="INSERT INTO `soundstatetable`(`date`,`soundstate`) VALUES ('"+date+"','"+soundstate+"')";
			statement.executeUpdate(sqlstatment);
			statement.close();
			
			//insert.close();
			System.out.println("Connected");
			conn.close();
		}catch(SQLException e){
			System.out.println(e);
		} 
		return null;
	}
	*/
}