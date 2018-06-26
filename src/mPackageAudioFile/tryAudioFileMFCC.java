package mPackageAudioFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.poi.sl.draw.geom.Path;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.AudioProcessor;
import core.be.tarsos.dsp.io.TarsosDSPAudioFormat;
import core.be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import core.be.tarsos.dsp.io.UniversalAudioInputStream;
import core.be.tarsos.dsp.mfcc.MFCC;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

public class tryAudioFileMFCC {
	static ArrayList<double[]> arraylistaverageoverall=new ArrayList<double[]>();
	//static float[] mfccArrayavareageee = new float[13];
	static float down=1;
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
			readExcel readexcel=new readExcel();
			readexcel.readExcelsheets();
		
			processExcel processexcel=new processExcel();
			
		 	float sampleRate=16000f;
		 	float framesize=0.025f;//25ms
		    int bufferSize= Math.round(sampleRate*framesize);//400
		    int bufferOverlap=(int) (0.015f * sampleRate);//with 240 i dont obtain the same number of the coef like in matlab
		    
		    //InputStream inStream=new FileInputStream("door-bell13.wav");
		    
		    //File soundwav=new File("door-bell13.wav");
		  
		    //AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(soundwav);
		    
		    
		    File audioFile = new File("traffic12.wav");
		    AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(audioFile);
		    System.out.println("framelength"+audioInputStream.getFrameLength());
		    System.out.println("SampleSizeInBits="+audioInputStream.getFormat().getSampleSizeInBits());
	    	System.out.println("FrameRate="+audioInputStream.getFormat().getFrameRate());
	    	System.out.println("FrameSize="+audioInputStream.getFormat().getFrameSize());
		    int frameNumber=(int)audioInputStream.getFrameLength();
	    	/*int length=(int) soundwav.length();
	    	byte[] bytes=new byte[length];
	    	audioInputStream.read(bytes,44,length);*/
		    	
	    	/*System.out.println("framelength"+audioInputStream.getFrameLength());
	    	System.out.println("SampleSizeInBits="+audioInputStream.getFormat().getSampleSizeInBits());
	    	System.out.println("FrameRate="+audioInputStream.getFormat().getFrameRate());
	    	System.out.println("FrameSize="+audioInputStream.getFormat().getFrameSize());
	    	System.out.println("SampleRate="+audioInputStream.getFormat().getSampleRate());
	    	System.out.println("getChannels="+audioInputStream.getFormat().getChannels());
	    	System.out.println("getEncoding="+audioInputStream.getFormat().getEncoding());
	    	System.out.println("BigEndian="+audioInputStream.getFormat().isBigEndian());
		    
		    for(int k=44;k<length;k+=2) {
		    	
	    		byte[] twoBytes=new byte[2];
	    		twoBytes[0]=bytes[k];
	    		twoBytes[1]=bytes[k+1];
	    		
	    		InputStream inStream1=new ByteArrayInputStream(twoBytes); 
	}*/
	    	
	    	AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(audioFile,bufferSize, bufferOverlap);
			//AudioFormat format = AudioSystem.getAudioFileFormat(audioFile).getFormat();
	    	
	    	//TarsosDSPAudioFormat format=new TarsosDSPAudioFormat(sampleRate,bufferSize,1,true,false);
	    	//TarsosDSPAudioInputStream input = new UniversalAudioInputStream(inStream,format);
	    	
	    	
			   // AudioDispatcher dispatcher = new AudioDispatcher(input,bufferSize,bufferOverlap);
			    
			    MFCC mfcc = new MFCC(bufferSize,sampleRate,13,20,300f,3700f);
			    dispatcher.addAudioProcessor(mfcc);
			    dispatcher.addAudioProcessor(new AudioProcessor() {
			       
			        int i=1;
			        @Override
			        public boolean process(AudioEvent audioEvent) {
			        	float [] mfccArrayFloat = mfcc.getMFCC(); 
			        	//float[] mfccArrayFloat = {43.1617f,-2.5968f,-2.9166f,-8.3133f,-10.244f,-22.6054f,-15.5679f,-2.5437f,14.0194f,7.8595f,13.7796f,8.0754f,3.9074f};
			        	//float[] mfccArrayFloat = {55.1195f,-9.3242f,-0.5767f,3.3303f,-4.3555f,-3.0318f,2.2356f,-6.6261f,-6.8031f,-9.2916f,-6.0330f,-2.6578f,3.9958f};
			        		
			        	double[] mfccArrayDouble=new double[13];
		        		for(int k=0;k<=12;k++) {
		        			mfccArrayDouble[k]=mfccArrayFloat[k]*-1.0;
		        		}
			        	
			        	System.out.println(mfccArrayDouble[0]+" "+mfccArrayDouble[1]+" "+mfccArrayDouble[2]+" "+mfccArrayDouble[3]+" "+mfccArrayDouble[4]+" "+mfccArrayDouble[5]+" "+mfccArrayDouble[6]+" "+mfccArrayDouble[7]+" "+mfccArrayDouble[8]+" "+mfccArrayDouble[9]+" "+mfccArrayDouble[10]+" "+mfccArrayDouble[11]+" "+mfccArrayDouble[12]);
			        	processexcel.send(mfccArrayDouble);
			        	System.out.println("Event "+i++);
			            return true;
			       }
			        @Override
			        public void processingFinished() {
			        	double[] array=processexcel.averageAndClear();
			        	arraylistaverageoverall.add(array); 
			        	System.out.println("--------->"+array[0]+" "+array[1]+" "+array[2]+" "+array[3]+" "+array[4]+" "+array[5]+" "+array[6]+" "+array[7]+" "+array[8]+" "+array[9]+" "+array[10]+" "+array[11]+" "+array[12]);
				        
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
		  
		    
			    double[] array=averageAndCleararraylistaverageoverall();
        	System.out.println(array[0]+" "+array[1]+" "+array[2]+" "+array[3]+" "+array[4]+" "+array[5]+" "+array[6]+" "+array[7]+" "+array[8]+" "+array[9]+" "+array[10]+" "+array[11]+" "+array[12]);
	}

	public static double[] averageAndCleararraylistaverageoverall() {
		//deleteMaxMin();
		int allength=arraylistaverageoverall.size();
		double[] arraywithoutdivide=new double[13];
		for(int i=0;i<allength;i++) {
			double[] arrayinsideal=arraylistaverageoverall.get(i);
			for(int insidearray=0;insidearray<13;insidearray++) {
				arraywithoutdivide[insidearray]+=arrayinsideal[insidearray];
			}
		}
		for(int k=0;k<13;k++) {
			arraywithoutdivide[k]=arraywithoutdivide[k]/allength;	
		}
		arraylistaverageoverall.clear();
		return arraywithoutdivide;
	}
	
}
