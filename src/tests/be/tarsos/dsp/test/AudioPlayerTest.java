/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/


package tests.be.tarsos.dsp.test;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;

import core.be.tarsos.dsp.AudioDispatcher;
import core.be.tarsos.dsp.io.PipedAudioStream;
import core.be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import jvm.be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import jvm.be.tarsos.dsp.io.jvm.AudioPlayer;
import jvm.be.tarsos.dsp.io.jvm.JVMAudioInputStream;

public class AudioPlayerTest {
	
	@Test
	public void testAudioPlayer() throws UnsupportedAudioFileException, LineUnavailableException{
		float[] sine = TestUtilities.audioBufferSine();
		AudioDispatcher dispatcher = AudioDispatcherFactory.fromFloatArray(sine, 44100, 10000, 128);
		dispatcher.addAudioProcessor(new AudioPlayer(JVMAudioInputStream.toAudioFormat(dispatcher.getFormat())));	
		dispatcher.run();
	}
	
	
	public void testStreamAudioPlayer() throws UnsupportedAudioFileException, LineUnavailableException{
		PipedAudioStream file = new PipedAudioStream("http://mp3.streampower.be/stubru-high.mp3");
		TarsosDSPAudioInputStream stream = file.getMonoStream(44100,0);
		AudioDispatcher d;
		d = new AudioDispatcher(stream, 1024, 128);
	    d.addAudioProcessor(new AudioPlayer(JVMAudioInputStream.toAudioFormat(d.getFormat())));
	    d.run();
	}

}
