package mypackagedemo4;

import javax.sound.sampled.LineUnavailableException;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.AudioEvent;
import core.be.tarsos.dsp.pitch.PitchDetectionHandler;
import core.be.tarsos.dsp.pitch.PitchDetectionResult;
import core.be.tarsos.dsp.pitch.PitchProcessor;
import core.be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

public class class22 {
	

	public static void main(String[] args) throws LineUnavailableException {
		String previousState=null;
		PitchDetectionHandler handler = new PitchDetectionHandler() {
			@Override
	        public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
	           float freq=pitchDetectionResult.getPitch();
	         //  System.out.println(freq);
	           
	           String currentState;
	           //if condition for detecting  the Speech
	           if(freq > 60 && freq < 250){
	        	   currentState="SND0";
        	   //if condition for detecting  the Alarm
	           }else if(freq > 450 && freq < 2600){
	        	   currentState="SND1";
        	   //if condition for detecting  the Silence
	           }else{
	        	   currentState="SND2";
	           }
	           
	           if(previousState!=currentState){
	        	   System.out.println(currentState);
	           }

	           String previousState=currentState;
			}
	    };
	    AudioDispatcher adp = AudioDispatcherFactory.fromDefaultMicrophone(44100,16384, 0);
	    adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 44100, 16384, handler));
	    adp.run(); 
	}

}
