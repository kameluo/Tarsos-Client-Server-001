package mypackagedemo4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.pitch.PitchDetectionHandler;
import core.be.tarsos.dsp.pitch.PitchDetectionResult;
import core.be.tarsos.dsp.pitch.PitchProcessor;
import core.be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

public class draft2 {
	public static void main(String[] args) throws IOException, LineUnavailableException {
		/* memory
		String[] memory=new String[3];
		for(int x=0;x<memory.length;x++){
			memory[x]=;
			//if the array is full
			if(x==memory.length){
				String memorySecondElement=memory[1];
				String memoryThirdElement=memory[2];
				memory[0]=memorySecondElement;
				memory[1]=memoryThirdElement;
				memory[2]=;
			}
		}
		*/
		/*array to string
		String[] string=new String[3];
		string[0]="123";
		string[1]="456";
		string[2]="789";
		String finalstring=String.join(",",string);
		System.out.println(finalstring);
		*/
		
		PitchDetectionHandler handler = new PitchDetectionHandler(){

			@Override
			public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {				
				float freq=pitchDetectionResult.getPitch();
			}
			
		};AudioDispatcher adp = AudioDispatcherFactory.fromDefaultMicrophone(44100,16384, 0);
	    adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 44100, 16384, handler));
	    adp.run();
	}
}
