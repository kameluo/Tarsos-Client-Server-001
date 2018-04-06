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
					AudioDispatcher adp = AudioDispatcherFactory.fromDefaultMicrophone(44100, 16384, 0);
					adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 44100, 16384, handler));
					
					MFCC mfcc = new MFCC(16384, 44100, 40, 50, 300, 3000);
					adp.addAudioProcessor(mfcc);
					adp.addAudioProcessor(new AudioProcessor() {
				        @Override
				        public void processingFinished() {
				            System.out.println("DONE");
				        }
				        @Override
				        public boolean process(AudioEvent audioEvent) {
				        		float[] mfccArray = mfcc.getMFCC();
				        		double[] mfccArrayDouble=new double[13];
				        		for(int k=0;k<=12;k++) {
				        			mfccArrayDouble[k]=mfccArray[k];
				        		}
					           // System.out.println(mfccArray[0]+" "+mfccArray[1]+" "+mfccArray[2]+" "+mfccArray[3]+" "+mfccArray[4]+" "+mfccArray[5]+" "+mfccArray[6]+" "+mfccArray[7]+" "+mfccArray[8]+" "+mfccArray[9]+" "+mfccArray[10]+" "+mfccArray[11]+" "+mfccArray[12]);
								String[] excelSheets = {"f1.xls","f2.xls","f3.xls","f4.xls","f5.xls","f6.xls","f7.xls","f8.xls","f9.xls"};
				            	int numIterations=1;
				            	
				            	boolean matrixOrDiagonal=true;//this is a flag to indicate if we are going to use the whole sigma matrix or the diagonal,true means we are going to use the sigma diagonal,false means the whole matrix
									if(matrixOrDiagonal){
					            		try {
											for (int j = 0; j < excelSheets.length; j++) {
												double tmp = 0.0;
												for (int ngauss = 0; ngauss < 30; ngauss++) {
													double prop=MathUtils.getGaussianPdfValue(mfccArrayDouble, getMuArray1d(getMuArray2d(excelSheets[j]),ngauss), getSigmaArrayDiagonal(getSigmaArraysDiagonal(excelSheets[j]),ngauss));
													double[] weights = new double[ngauss];
													weights[j] = 1.0f / 30;
													tmp += weights[j] * prop;
												}
												double[] logLikelihoods = null;
												logLikelihoods[numIterations - 1] += Math.log(tmp);
												System.out.println("prop is :"+logLikelihoods[numIterations - 1]);
											}
										} catch (FileNotFoundException e) {
											e.printStackTrace();
										} catch (IOException e) {
											e.printStackTrace();
										}
				        			}else{
				        				try {
											for (int j = 0; j < excelSheets.length; j++) {
												double tmp = 0.0;
												for (int ngauss = 0; ngauss < 30; ngauss++) {
													double prop=MathUtils.getGaussianPdfValue(mfccArrayDouble, getMuArray1d(getMuArray2d(excelSheets[j]),ngauss), getSigmaArrayDiagonal(getSigmaArraysDiagonal(excelSheets[j]),ngauss));
													double[] weights = new double[ngauss];
													weights[j] = 1.0f / 30;
													tmp += weights[j] * prop;
												}
												double[] logLikelihoods = null;
												logLikelihoods[numIterations - 1] += Math.log(tmp);
												System.out.println("prop is :"+logLikelihoods[numIterations - 1]);
											}
										} catch (FileNotFoundException e) {
											e.printStackTrace();
										} catch (IOException e) {
											e.printStackTrace();
										}
				        			}	
					            return true;
				        }
				    });
					
					adp.run();
				
					//start of mfcc part------------------
					/*
					int sampleRate = 44100;
				    int bufferSize = 16384;
				    int bufferOverlap = 128;
				    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100,16384,512);
					MFCC mfcc = new MFCC(bufferSize, sampleRate, 40, 50, 300, 3000);
					dispatcher.addAudioProcessor(mfcc);
					
					dispatcher.addAudioProcessor(new AudioProcessor() {
				        @Override
				        public void processingFinished() {
				            System.out.println("DONE");
				        }
				        @Override
				        public boolean process(AudioEvent audioEvent) {
				            	float[] mfccArr = mfcc.getMFCC();
					            System.out.println(mfccArr);
				            return true;
				        }
				    });
					dispatcher.run();
					//end of mfcc part------------------
					*/
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
	
	
	
	/** 
	 * 7 functions to get the data matrices from the excel sheets "sigma,mu and the component proportional[]"
	 * 		1-"double[] getSigmaArrayDiagonal"function to get one row from the 2d array "double[][] getSigmaArraysDiagonal"
			2-"double[][] getSigmaArraysDiagonal" is a function to get each array "1x13" in every sheet (which is the Diagonal Sigma Vector) and store them in 2d "30x13" array
			3-"double[][] getSigmaArrays2d" to get a single sigma matrix from the multidimensional array "double[][][] getSigmaArrays3d"
			4-"double[][][] getSigmaArrays3d" is a multidimensional array to get the 30 matrices of sigma in each file and store them in 3d array
			5-"double[] getMuArray1d"function to get one row from the 2d array "double[][] getMuArray"
			6-"double[][] getMuArray" is a function to get the Mu matrix from the excel file
			7-"double[] getComponentProportionElement"function to get the component proportional Element from the "double[] getComponentProportionArray"
			8-"double[] getComponentProportionArray"function to get the component proportional Array from the Excel file
	*/
	//"double[] getSigmaArrayDiagonal"function to get one row from the 2d array "double[][] getSigmaArraysDiagonal"
		public static double[] getSigmaArrayDiagonal(double[][] getSigmaArraysDiagonal,int rowNumber) {
			double[] arraySigmaDiagonal=new double[13];
				for(int column=0;column<=12;column++) {
					arraySigmaDiagonal[column]=getSigmaArraysDiagonal[rowNumber][column];
				}
			return arraySigmaDiagonal;
		}
	
	//"double[][] getSigmaArraysDiagonal" is a function to get each array in every sheet and store them in 2d "30x13" array
	public static double[][] getSigmaArraysDiagonal(String filename) throws FileNotFoundException, IOException{
        double[][] arraysSigmaDiagonal=new double[30][13];
        HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(filename));//to be able to create everything in the excel sheet
        for(int s=0;s<=29;s++){//for loop to get the 30 Sigma Arrays "1x13" in a 2d-dimensional array
            String sheetname="SigmaDiagonal"+(String.valueOf(s+1));//adding the index to the sheet name
            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
            for(int column=0;column<=12;column++){//for loop to get the  in each Sigma Array in the each single excel sheet 
            	HSSFRow row=sheet.getRow(0);//we have only one row in each sigma diagonal sheet
            	arraysSigmaDiagonal[s][column]=row.getCell(column).getNumericCellValue(); 
            }
        }
        return arraysSigmaDiagonal;
    }
	
	// "double[][] getSigmaArrays2d" to get a single sigma matrix from the multidimensional array "double[][][] getSigmaArrays3d"
	public static double[][] getSigmaArrays2d(double[][][] getSigmaArrays3d,int Sigma) throws FileNotFoundException, IOException{
		//sigma is an integer from 0 to 29 to indicate which matrix do we want from that category(we have 30 matrices)
		double[][] arraysigma2d=new double[13][13];
		for(int row=0;row<=12;row++) {
			for(int column=0;column<=12;column++) {
				arraysigma2d[row][column]=getSigmaArrays3d[Sigma][row][column];
			}
		}
		return arraysigma2d;
	}
	
	// "double[][][] getSigmaArrays3d" is a multi dimentional array to get the 30 matrices of sigma in each file
	public static double[][][] getSigmaArrays3d(String filename) throws FileNotFoundException, IOException{
        double[][][] arraysigma=new double[30][13][13];
        HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(filename));//to be able to create everything in the excel sheet
        for(int s=0;s<=29;s++){//for loop to get the 30 Sigma matrices "13x13" in a multidimensional array
            String sheetname="Sigma"+(String.valueOf(s+1));//adding the index to the sheet name
            HSSFSheet sheet=workbook.getSheet(sheetname);//getting the sheet
            for(int i=0;i<=12;i++){//for loop to get the rows and columns in each Sigma matrix
                for(int j=0;j<=12;j++){
                    HSSFRow row=sheet.getRow(i);
                    arraysigma[s][i][j]=row.getCell(j).getNumericCellValue();
                }
            }
        }
        return arraysigma;
    }
	//"double[] getMuArray1d"function to get one row from the 2d array "double[][] getMuArray"
	public static double[] getMuArray1d(double[][] getMuArray2d,int rowNumber) {
		double[] getMuArray1d=new double[13];
			for(int column=0;column<=12;column++) {
				getMuArray1d[column]=getMuArray2d[rowNumber][column];
			}
		return getMuArray1d;
	}
	//"double[][] getMuArray" is a function to get the Mu matrix from the excel file
	public static double[][] getMuArray2d(String filename) throws FileNotFoundException, IOException{
	    double[][] arraymu=new double[30][13];
	    HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(filename));//to be able to create everything in the excel sheet
	    HSSFSheet sheet=workbook.getSheet("mu");//getting the sheet
	    for(int i=0;i<=29;i++){//for loop to get the Mu matrix "30x13" in a multidimensional array
	        for(int j=0;j<=12;j++){
	            HSSFRow row=sheet.getRow(i);
	            arraymu[i][j]=row.getCell(j).getNumericCellValue();
	        }
	    }
	    return arraymu;
	}
	//"double[] getComponentProportionElement"function to get the component proportional Element from the "double[] getComponentProportionArray"
	public static double getComponentProportionElement(double[] getComponentProportionArray,int numberOfElement) {
		double componentArrayElement=getComponentProportionArray[numberOfElement];
		return componentArrayElement;
	}
	
	//"double[] getComponentProportionArray"function to get the component proportional Array from the Excel file
	public static double[] getComponentProportionArray(String filename) throws FileNotFoundException, IOException{
	    double[] arrayComponentProportion=new double[30];
	    HSSFWorkbook workbook=new HSSFWorkbook(new FileInputStream(filename));//to be able to create everything in the excel sheet
	    HSSFSheet sheet=workbook.getSheet("ComponentProportion");//getting the sheet
	    for(int j=0;j<=29;j++){
	        HSSFRow row=sheet.getRow(0);
	        arrayComponentProportion[j]=row.getCell(j).getNumericCellValue();
	    }    
	    return arrayComponentProportion;
}

}