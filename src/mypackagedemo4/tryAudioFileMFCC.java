package mypackagedemo4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
		
		 	float sampleRate = 16000f;
		 	float framesize = 0.025f ;// 25 ms
		    int bufferSize = Math.round(sampleRate*framesize);//441//20
		    int bufferOverlap = 0;
		    
		    InputStream inStream=new FileInputStream("door-bell13.wav");
		    AudioDispatcher dispatcher = new AudioDispatcher(new UniversalAudioInputStream(inStream,new TarsosDSPAudioFormat(sampleRate,bufferSize,1,true,true)),bufferSize,bufferOverlap);
		    MFCC mfcc = new MFCC(bufferSize, sampleRate, 13,20, 300f, 3700f);//like in the matlab code,lower freq is 133.3 and the highest is 6890
		    dispatcher.addAudioProcessor(mfcc);
		    dispatcher.addAudioProcessor(new AudioProcessor() {
		        @Override
		        public void processingFinished() {
			        System.out.println(mfccArrayavareageee[0]+" "+mfccArrayavareageee[1]+" "+mfccArrayavareageee[2]+" "+mfccArrayavareageee[3]+" "+mfccArrayavareageee[4]+" "+mfccArrayavareageee[5]+" "+mfccArrayavareageee[6]+" "+mfccArrayavareageee[7]+" "+mfccArrayavareageee[8]+" "+mfccArrayavareageee[9]+" "+mfccArrayavareageee[10]+" "+mfccArrayavareageee[11]+" "+mfccArrayavareageee[12]);
			        double[] mfccArrayDouble=new double[13];
	        		for(int k=0;k<=12;k++) {
	        			mfccArrayDouble[k]=mfccArrayavareageee[k];
	        		}
			        processExcel processexcel=new processExcel();
        			String category=processexcel.processExcel(mfccArrayDouble);
        			System.out.println("The Category is:"+category);
			        System.out.println("DONE");
		        }
		        @Override
		        public boolean process(AudioEvent audioEvent) {
		        	float[] mfccArray = mfcc.getMFCC(); 
			        //System.out.println(mfccArray[0]+" "+mfccArray[1]+" "+mfccArray[2]+" "+mfccArray[3]+" "+mfccArray[4]+" "+mfccArray[5]+" "+mfccArray[6]+" "+mfccArray[7]+" "+mfccArray[8]+" "+mfccArray[9]+" "+mfccArray[10]+" "+mfccArray[11]+" "+mfccArray[12]);
			        send(mfccArray);
		            return true;
		        }
		    });
		    dispatcher.run();
	}
	public static void send(float[] array) {
		for(int i=0;i<13;i++) {
		float number=mfccArrayavareageee[i];
		mfccArrayavareageee[i]=(number+array[i])/down;
		down++;
		}
    }
}
