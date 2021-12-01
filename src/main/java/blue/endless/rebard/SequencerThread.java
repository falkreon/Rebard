package blue.endless.rebard;

import java.util.SortedSet;

import blue.endless.rebard.score.ScoreSequence;
import blue.endless.rebard.score.SequenceEvent;

public class SequencerThread extends Thread {
	
	private ScoreSequence sequence;
	private int countdown = 3;
	private volatile boolean started = false;
	private volatile boolean paused = false;
	private volatile boolean stopped = false;
	private long lastTickMillis = 0L;
	private long lastMidiTick = 0L;
	private double midiTickBuffer = 0.0; //How many "fractional midi ticks" have we accumulated since the last playback tick?
	private ScoreSynth synth = null;
	
	public void setSequence(ScoreSequence sequence) {
		if (started) throw new IllegalStateException("Cannot set the Sequence of an active SequencerThread");
		this.sequence = sequence;
	}
	
	public void setCountdown(int seconds) {
		this.countdown = seconds;
	}
	
	public void setSynth(ScoreSynth synth) {
		this.synth = synth;
	}
	
	public void pause() {
		if (started && !stopped && !paused) {
			synchronized(this) {
				paused = true;
			}
		}
	}
	
	public void unpause() {
		if (started && !stopped && paused) {
			synchronized(this) {
				paused = false;
				lastTickMillis = System.nanoTime() / 1_000_000L;
				this.notify();
			}
		}
	}
	
	boolean isPaused() {
		return paused;
	}
	
	boolean isStopped() {
		return stopped;
	}
	
	public void setPaused(boolean paused) {
		if (paused) {
			unpause();
		} else {
			pause();
		}
	}
	
	public void play(ScoreSequence sequence, int countdown, ScoreSynth synth) {
		setSequence(sequence);
		setCountdown(countdown);
		setSynth(synth);
		lastTickMillis = System.nanoTime() / 1_000_000L;
		started = true;
		start();
	}
	
	public void stopPlaying() {
		stopped = true;
		synchronized(this) {
			this.notify();
		}
	}
	
	@Override
	public void run() {
		super.run();
		
		if (sequence==null) return;
		if (countdown>0) {
			int secondsRemaining = countdown;
			while(secondsRemaining>0) {
				System.out.println(""+secondsRemaining);
				secondsRemaining--;
				try { Thread.sleep(1000); } catch (InterruptedException ex) {}
			}
			countdown = 0;
			lastTickMillis = System.nanoTime() / 1_000_000L;
		}
		
		double midiTicksPerMilli = (sequence.getMetadata().getBPM() * sequence.getMetadata().getPPQ()) / 60_000.0;
		System.out.println("MidiTicksPerMilli = "+midiTicksPerMilli);
		long lastTimestamp = sequence.timeLength();
		
		while(started & !stopped) {
			//Time sync
			long now = System.nanoTime() / 1_000_000L;
			long elapsed = now - lastTickMillis;
			lastTickMillis = now;
			midiTickBuffer += midiTicksPerMilli * elapsed;
			long ticksToConsume = (long)Math.floor(midiTickBuffer);
			if (ticksToConsume>Integer.MAX_VALUE) {
				//Something went grievously wrong in the timings, consume some trivial number of ticks and do a full reset on the partial ticks
				midiTickBuffer = 0;
				ticksToConsume = 2;
			} else {
				midiTickBuffer -= ticksToConsume;
			}
			
			SortedSet<SequenceEvent> toPlay = sequence.getMessages(lastMidiTick, lastMidiTick+ticksToConsume);
			lastMidiTick += ticksToConsume;
			//if (toPlay.size()>0) System.out.println("Tick: "+lastMidiTick+" playing "+toPlay.size()+" events, Elapsed: "+elapsed);
			
			//Play notes
			if (synth!=null) {
				for(SequenceEvent evt : toPlay) synth.consumeEvent(evt);
			}
			try {
				Thread.sleep(1); //Yield is a no-op most of the time, give other Threads a chance to run
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
			
			if (lastMidiTick>=lastTimestamp) {
				stopped = true;
				break;
			}
			
			if (paused & !stopped) {
				System.out.println("Entering pause state");
				while (paused) {
					System.out.println("Waiting...");
					try {
						synchronized(this) {
							this.wait();
						}
					} catch (InterruptedException e) {} //Interruptions/Notifies are expected.
				}
				System.out.println("Leaving pause state");
			}
		}
		System.out.println("Completed.");
		
		if (synth!=null) synth.stopAll();
	}
}
