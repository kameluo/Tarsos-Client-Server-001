package tests.be.tarsos.dsp.test;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.PitchShifter;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import jvm.be.tarsos.dsp.io.jvm.AudioPlayer;

public class PitchShifterTest {
	
	@Test
	public void testPitchShiftSine() throws UnsupportedAudioFileException, LineUnavailableException, IOException{
		float[] audioBuffer = TestUtilities.audioBufferSine();
		double factor = 1.35;
		int sampleRate = 44100;
	
		final AudioDispatcher d = AudioDispatcherFactory.fromFloatArray(audioBuffer, sampleRate, 1024,1024-32);
		d.setZeroPadLastBuffer(true);
		PitchShifter w = new PitchShifter(factor,sampleRate,1024,1024-32);
		AudioFormat f = new AudioFormat(sampleRate,16,1,true,false);
		d.addAudioProcessor(w);
		
		d.addAudioProcessor(new AudioPlayer(f));
		d.run();
	}

}
