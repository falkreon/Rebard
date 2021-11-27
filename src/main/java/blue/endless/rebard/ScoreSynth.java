package blue.endless.rebard;

import blue.endless.rebard.score.SequenceEvent;

public interface ScoreSynth {
	public void consumeEvent(SequenceEvent evt);
	public void stopAll();
}
