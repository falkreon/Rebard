package blue.endless.rebard.score;

import java.util.ArrayList;

/**
 * A "Part" represents the part of a musical piece played by one person on one instrument. Roughly equivalent to a MIDI Track.
 * This data format is very close to MIDI, having timestamped events arranged in a monotonic order.
 */
public class TimedPart {
	private ArrayList<Long> data = new ArrayList<>();
	
	public ArrayList<Long> getMessages(int start, int end) {
		return null;
	}
}
