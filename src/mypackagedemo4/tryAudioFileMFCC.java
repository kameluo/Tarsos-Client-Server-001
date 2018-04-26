package mypackagedemo4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.AudioProcessor;
import core.be.tarsos.dsp.io.TarsosDSPAudioFormat;
import core.be.tarsos.dsp.io.UniversalAudioInputStream;
import core.be.tarsos.dsp.mfcc.MFCC;

public class tryAudioFileMFCC {
		
	static float[] mfccArrayavareageee = new float[13];
	static float down=1;
	public static void main(String[] args) throws IOException {
			readExcel readexcel=new readExcel();
			readexcel.readExcelsheets();
		
			processExcel processexcel=new processExcel();
			
		 	float sampleRate = 16000f;
		 	float framesize = 2f;//0.025f ;//25ms
		    int bufferSize=Math.round(sampleRate*framesize);//400
		    int bufferOverlap=8;
		    
		    InputStream inStream=new FileInputStream("door-bell13.wav");
		    
		    File soundwav=new File("door-bell13.wav");
		    //AudioSystem audiosystemnnn=new AudioSystem();
		    try {
		    	AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(soundwav);
		    	
		    	//int r=audioInputStream.read();
		    	
		    	
		    	System.out.println("framelength"+audioInputStream.getFrameLength());

		    	System.out.println("SampleSizeInBits="+audioInputStream.getFormat().getSampleSizeInBits());
		    	System.out.println("FrameRate="+audioInputStream.getFormat().getFrameRate());
		    	System.out.println("FrameSize="+audioInputStream.getFormat().getFrameSize());
		    	System.out.println("SampleRate="+audioInputStream.getFormat().getSampleRate());
		    	System.out.println("getChannels="+audioInputStream.getFormat().getChannels());
		    	System.out.println("getEncoding="+audioInputStream.getFormat().getEncoding());
		    	System.out.println("BigEndian="+audioInputStream.getFormat().isBigEndian());
		    	
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    AudioDispatcher dispatcher = new AudioDispatcher(new UniversalAudioInputStream(inStream,new TarsosDSPAudioFormat(sampleRate,bufferSize,1,true,false)),bufferSize,bufferOverlap);
		    MFCC mfcc = new MFCC(bufferSize,sampleRate,13,20,300f,3700f);
		    dispatcher.addAudioProcessor(mfcc);
		    dispatcher.addAudioProcessor(new AudioProcessor() {
		       
		        int i=1;
		        @Override
		        public boolean process(AudioEvent audioEvent) {
		        	
		        	float[] mfccArray = mfcc.getMFCC(); 
		        	//float[] mfccArray = {43.1617f,-2.5968f,-2.9166f,-8.3133f,-10.244f,-22.6054f,-15.5679f,-2.5437f,14.0194f,7.8595f,13.7796f,8.0754f,3.9074f};
		        	//float[] mfccArray = {55.1195f,-9.3242f,-0.5767f,3.3303f,-4.3555f,-3.0318f,2.2356f,-6.6261f,-6.8031f,-9.2916f,-6.0330f,-2.6578f,3.9958f};
		        	System.out.println(mfccArray[0]+" "+mfccArray[1]+" "+mfccArray[2]+" "+mfccArray[3]+" "+mfccArray[4]+" "+mfccArray[5]+" "+mfccArray[6]+" "+mfccArray[7]+" "+mfccArray[8]+" "+mfccArray[9]+" "+mfccArray[10]+" "+mfccArray[11]+" "+mfccArray[12]);
		        	processexcel.send(mfccArray);
		        	System.out.println("Event "+i++ );
		            return true;
		       }
		        @Override
		        public void processingFinished() {
		        	float[] array=processexcel.averageAndClear();
		        	System.out.println(array[0]+" "+array[1]+" "+array[2]+" "+array[3]+" "+array[4]+" "+array[5]+" "+array[6]+" "+array[7]+" "+array[8]+" "+array[9]+" "+array[10]+" "+array[11]+" "+array[12]);
			        
			        double[] mfccArrayDouble=new double[13];
	        		for(int k=0;k<=12;k++) {
	        			mfccArrayDouble[k]=array[k];
	        		}
			       // processExcel processexcel=new processExcel();
        			String category=processexcel.processExcel(mfccArrayDouble);
        			System.out.println("The Category is:"+category);
			        System.out.println("DONE");
		        }
		    });
		    dispatcher.run();
	}
}
