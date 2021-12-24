package blue.endless.rebard.score;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A "Part" represents the part of a musical piece played by one person on one instrument. Roughly equivalent to a MIDI Track.
 * This data format is very close to MIDI, having timestamped events arranged in a monotonic order.
 */
public class ScoreSequence {
	private SequenceMeta meta = new SequenceMeta();
	private TreeSet<SequenceEvent> data = new TreeSet<>((a, b)->{
		if (a.timestamp()==b.timestamp()) {
			boolean aNote = (a.type()==SequenceEvent.Type.NOTE_ON);
			boolean bNote = (b.type()==SequenceEvent.Type.NOTE_ON);
			if (aNote && !bNote) {
				return 1;
			} else if (bNote && !aNote) {
				return -1;
			} else {
				return 0;
			}
		} else {
			return Long.compareUnsigned(a.timestamp(), b.timestamp());
		}
	});
	
	/**
	 * Grab a view of the SequenceEvents in a timeslice.
	 * @param start The timestamp at the start of the timeslice
	 * @param end   The timestamp at the end of the timeslice
	 * @return A SortedSet containing the SequenceEvents that initiate within this timeslice.
	 */
	public SortedSet<SequenceEvent> getMessages(long start, long end) {
		return data.subSet(new SequenceEvent(start, SequenceEvent.Type.NOTE_OFF, 0, 0, 0), new SequenceEvent(end, SequenceEvent.Type.NOTE_OFF, 0, 0, 0));
	}
	
	public void add(SequenceEvent e) {
		data.add(e);
	}
	
	/**
	 * The length of this Sequence. Does not include the length of any held note at the end.
	 * @return
	 */
	public long timeLength() {
		if (data.isEmpty()) return 0L;
		
		return data.last().timestamp();
	}
	
	public SequenceMeta getMetadata() {
		return meta;
	}

	public void setMetadata(SequenceMeta meta) {
		this.meta = meta;
	}
}
