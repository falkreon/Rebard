package blue.endless.rebard.score;

/**
 * Notes are per MIDI and are integers; the lowest valid note is 0, representing C-1 (that is, octave negative-one).
 * Textual representation is different from TBard; "C5" is middle C
 * 
 * <p>Representable notes are integers between LOWEST_REPRESENTABLE_NOTE and LOWEST_REPRESENTABLE_NOTE+37, inclusive.
 * Integers outside this range will either not be played, or will be transposed into this range when played, depending
 * on settings.
 */
public class Note {
	
	public static final int LOWEST_REPRESENTABLE_NOTE = 12*4; //C-3
	public static final int HIGHEST_REPRESENTABLE_NOTE = 12*7; //C-6 (!)
	
	private static final String[] NOTES = { "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B" };

	
	
	public static String getNoteName(int note) {
		int octave = (note / 12) - 1;
		int noteId = note % 12;
		return NOTES[noteId]+octave;
	}
	
	public static int getNoteForName(String name) {
		int note = -1;
		
		name = name.trim().toLowerCase();
		for(int i=0; i<NOTES.length; i++) {
			if (name.startsWith(NOTES[i].toLowerCase())) {
				note = i;
				name = name.substring(NOTES[i].length());
				break;
			}
		}
		
		if (note==-1) return -1;
		
		try {
			int octave = Integer.parseInt(name);
			return (12*(octave+1)) + note;
		} catch (Throwable t) {
			return (12*5)+note; //Default to middle octave if we can't make sense of the one supplied
		}
	}
}
