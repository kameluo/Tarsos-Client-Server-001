package mPackageAuduioFile;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;
import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.AudioProcessor;
import core.be.tarsos.dsp.io.TarsosDSPAudioFormat;
import core.be.tarsos.dsp.io.UniversalAudioInputStream;
import core.be.tarsos.dsp.mfcc.MFCC;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;


public class mfccexample {
	static float[] mfccArrayavareage=null;
	
	static ArrayList<float[]> al=new ArrayList<float[]>();
	public static void main(String[] args) throws LineUnavailableException, FileNotFoundException {
		//while (true) {
		    int sampleRate = 16000;
		    int bufferSize =160;//441
		    int bufferOverlap = 0;
		    //this command for audio files
		    InputStream inStream=new FileInputStream("glassbreaking16000.wav");
		    AudioDispatcher dispatcher = new AudioDispatcher(new UniversalAudioInputStream(inStream,new TarsosDSPAudioFormat(sampleRate,bufferSize,1,true,true)),bufferSize,bufferOverlap);
		    MFCC mfcc = new MFCC(bufferSize, sampleRate, 13,20, 300f, 3700f);//like in the matlab code,lower freq is 133.3 and the highest is 6890
		    dispatcher.addAudioProcessor(mfcc);
		    dispatcher.addAudioProcessor(new AudioProcessor() {
		        @Override
		        public void processingFinished() { 
		        	float[] array =new float[13];
		        	int numberofelements=al.size();
		        	for (int i=1;i<=numberofelements;i++) {
			    			float[] x=al.get(i);
				    			for(int k=1;k<=13;k++) {
				    			mfccArrayavareage[k]=(x[k]+array[k])/2;
				    			}
		        	}
		        	System.out.println("DONE");
		        }
		        @Override
		        public boolean process(AudioEvent audioEvent) {
		        	float[] mfccArray = mfcc.getMFCC(); 
			        System.out.println(mfccArray[0]+" "+mfccArray[1]+" "+mfccArray[2]+" "+mfccArray[3]+" "+mfccArray[4]+" "+mfccArray[5]+" "+mfccArray[6]+" "+mfccArray[7]+" "+mfccArray[8]+" "+mfccArray[9]+" "+mfccArray[10]+" "+mfccArray[11]+" "+mfccArray[12]);
			        al.add(mfccArray);
			        return true;
		        }
		    });
		    dispatcher.run();
		}
	
	public static float[] setarray(float[] array) {
		for (int i=1;i<=13;i++) {
			float x=mfccArrayavareage[i];
			mfccArrayavareage[i]=array[i]+x;
			//GMMTrainer GT=new GMMTrainer();
		}
		return array;
	}
	//}
	}