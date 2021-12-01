package blue.endless.rebard.score;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SequenceMeta {
	public static final int SEQUENCE_NAME = 0x03;
	public static final int INSTRUMENT_NAME = 0x04;
	public static final int END_OF_TRACK = 0x2F; //Ignore
	public static final int TEMPO = 0x51;
	public static final int KEY_SIGNATURE = 0x59;
	
	private transient boolean parsedFirstTempo = false;
	
	protected String name = "untitled";
	protected double bpm = 120.0;
	protected int ppq = 480;
	protected int timeSigNumerator = 4;
	protected int timeSigDenominator = 4;
	protected String key = "C";
	protected int patch = 0;
	
	public String getName() { return name; }
	public double getBPM() { return bpm; }
	public int getPPQ() { return ppq; }
	public int getPatch() { return patch; }
	
	public void applyMeta(int message, byte[] data) {
		switch(message) {
		case SEQUENCE_NAME:
			name = new String(data, StandardCharsets.UTF_8);
			break;
		case TEMPO:
			if (!parsedFirstTempo) {
				if (data.length<3) return;
				int usecsPerQuarter = 0;
				usecsPerQuarter |= (data[0] & 0xFF) << 16;
				usecsPerQuarter |= (data[1] & 0xFF) << 8;
				usecsPerQuarter |= (data[2] & 0xFF);
				bpm = 60_000_000.0 / (double) usecsPerQuarter;
				parsedFirstTempo = true;
			} else {
				System.out.println("Note: Song has tempo changes that will be ignored");
			}
			break;
		case KEY_SIGNATURE:
			key = new String(data, StandardCharsets.UTF_8);
			if (key==null || key.isBlank()) key = "C";
			break;
		
		default:
		
		}
	}
	
	public String toString() {
		return "{ "+
			"'name': '"+name+"', "+
			"'key': '"+key+"', "+
			"'timeSignature': "+timeSigNumerator+" / "+timeSigDenominator+", "+
			"'bpm': "+(int)bpm+", "+
			"'ppq': "+ppq+
			" }";
				
	}
}
