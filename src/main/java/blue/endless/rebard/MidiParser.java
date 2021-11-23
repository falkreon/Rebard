package blue.endless.rebard;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiParser {

	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"};
	public static final String[] octaves = {"-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

	String[] instruments;
	List<String> shownInstruments = new ArrayList<String>();
	String[][] sheets = new String[16][11];
	Integer[][] quality = new Integer[16][11];
	Integer[] totalNotes = new Integer[16];

	String filePath;

	String lastNote = "";
	int lastOctave;
	
	boolean trueTimings = false;
	
	boolean allNull = true;


	public MidiParser(String fileName, boolean trueTimings) {
		filePath = fileName;
		this.trueTimings = trueTimings;
	}

	public void getNotes(String filePath, int instrumentIndex, int octaveTarget, boolean includeHoldRelease) throws Exception {

		includeHoldRelease = true;

		for(int ins = 0; ins < 16; ins++) {
			totalNotes[ins] = 0;
			for(int oct = 0; oct < 11; oct++) {
				sheets[ins][oct] = "";
				quality[ins][oct] = 0;
			}
		}

		Sequence sequence = MidiSystem.getSequence(new File(filePath));
		TickConverter converter = new TickConverter(sequence);

		System.out.println("Sequence (MS): " + sequence.getMicrosecondLength()/1000);
		System.out.println("Sequence (ticks): " + sequence.getTickLength());
		System.out.println("Target octave: " + octaveTarget);	

		for(String i : instruments){
			System.out.println("About to get notes, found instrument: " + i);
		}

		for (Track track :  sequence.getTracks()) {

			long prevTick = 0;
			boolean firstLineDone = false;
			ShortMessage sm = null;
			for (int i=0; i < track.size(); i++) { 
				MidiEvent event = track.get(i);


				if(firstLineDone && (i+1 < track.size()) && (track.get(i+1).getMessage() instanceof ShortMessage)&& (track.get(i).getMessage() instanceof ShortMessage) && (track.get(i+1).getTick() - event.getTick()) < 2 && ((ShortMessage)track.get(i+1).getMessage()).getCommand() == NOTE_ON && ((ShortMessage)track.get(i).getMessage()).getCommand() == NOTE_ON) continue;



				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage) {
					sm = (ShortMessage) message;

					if ((sm.getCommand() == NOTE_ON) && (sm.getData2() > 0)) {
						totalNotes[sm.getChannel()]++;
						String line = "";

						if (firstLineDone || trueTimings) line += "w" + converter.ticksToMillis(event.getTick() - prevTick) + "\n";
						prevTick = event.getTick();

						int key = sm.getData1();
						int octave = (key / 12)-1;
						int note = key % 12;
						String noteName = NOTE_NAMES[note];
						lastNote = noteName;
						lastOctave =  octave;
						
						if (includeHoldRelease) line += "h";
						line += noteName;

						firstLineDone = true;


						for(int o = 0; o < 11; o++){
							sheets[sm.getChannel()][o] += line;
							if(noteName.equals("C") && octave > o+1){
								sheets[sm.getChannel()][o] += "+2";
								quality[sm.getChannel()][o]++;
							} else {
								if(octave < o) {
									sheets[sm.getChannel()][o] += "-1";
									if(octave == o-1) quality[sm.getChannel()][o]++;
								}
								if(octave > o) {
									sheets[sm.getChannel()][o] += "+1";
									if(octave == o+1) quality[sm.getChannel()][o]++;
								}
								if(octave == o) quality[sm.getChannel()][o]++;
							}	
						}
						checkIfChannelIsIndexed(sm.getChannel());
					} else 

						if (includeHoldRelease && (sm.getCommand() == NOTE_OFF || (sm.getData2() == 0))) {

							if (i+1 <= track.size() && track.get(i+1).getMessage() instanceof ShortMessage && ((ShortMessage)track.get(i+1).getMessage()).getCommand() == NOTE_ON && (track.get(i+1).getTick() - event.getTick()) < 2) {
								continue;
							}

							int key = sm.getData1();
							int octave = (key / 12)-1;
							int note = key % 12;
							String noteName = NOTE_NAMES[note];
							
							if (lastNote.equals(noteName) && lastOctave == octave) {
								String line = "";
								line += "w" + converter.ticksToMillis(event.getTick() - prevTick) + "\n";
								prevTick = event.getTick();
								for(int o = 0; o < 11; o++){
									sheets[sm.getChannel()][o] += "\n" + line;
								}
								addStringToAllSheets(sm.getChannel(), "release");
							}
							lastNote = "";
							lastOctave = -10;
						}

					if (sm.getCommand() == NOTE_ON || (includeHoldRelease && sm.getCommand() == NOTE_OFF)) {
						//prevTick = event.getTick();
						addStringToAllSheets(sm.getChannel(), "\n");
					}

				} 

			}

		}
		
		shownInstruments = new ArrayList<String>();
		for (String ins : instruments) {
			System.out.println("PROCESSING INSTRUMENT: '" + ins + "'");

			if(ins != null) {
				shownInstruments.add(ins);
				allNull = false;
			}
		}

		for (int ins = 0; ins < 16; ins++) {
			estimateOctaveQuality(ins);
			for (int oct = 0; oct < 11; oct++) {
				sheets[ins][oct] = sheets[ins][oct].replaceAll("release\nw0\n", "");
				sheets[ins][oct] = sheets[ins][oct].replaceAll("\n+", "\n");
			}
		}



		return;

	}
	
	public int convertShownInstrument(String shown) {
		int index = 0;
		for (String instrument : instruments) {
			if (instrument != null && instrument.equals(shown)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public String[] getOctaveQuality(String shownInstrument){
		int instrument = convertShownInstrument(shownInstrument);
		
		for (int q = 0; q < 11; q++) {
			System.out.println("Instrument: " + (instrument) + ", q: " + q);
			if (instrument < 0) octaves[q] = "What just happened?";
			else {
				octaves[q] = (q-1) + " (quality: " + (quality[instrument][q]) + "%)";
				System.out.println("Octave quality " + (q-1) + ": " + quality[instrument][q]);
			}
		} 
		return octaves;
	}

	private void estimateOctaveQuality(int instrument){
		if(sheets[instrument][0].equals("")) return;
		System.out.println("Processing quality of instrument " + instrument);
				
		boolean ignorePrint = true;
		for(int q = 0; q < 11; q++){
			if(quality[instrument][q] != 0) ignorePrint = false;
		}

		for(int q = 0; q < 11; q++){
			int qualityPercentage = 0;
			if(quality[instrument][q] != 0) qualityPercentage = (int)(((float)quality[instrument][q]/(float)totalNotes[instrument])*100);
			quality[instrument][q] = qualityPercentage;
			octaves[q] = (q-1) + " (quality: " + (qualityPercentage) + "%)";
			if(!ignorePrint) {
				System.out.println("Octave quality " + (q-1) + ": " + qualityPercentage);
			}
		}
	}

	private void checkIfChannelIsIndexed(int instrument) {
		if(instruments[instrument] != null) return;
		System.out.println("Instrument in channel " + instrument + " does not appear to be indexed. Setting it to unspecified.");
		try {
			if (instruments[instrument] == null) instruments[instrument] = (instrument + 1) + ". Unspecified";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getHighestQualityOctave(String shownInstrument){
		int instrument = convertShownInstrument(shownInstrument);
		int highest = 6; //Octave 5 is the default
		System.out.println(quality.toString());
		for(int q = 0; q < 11; q++){
			if(quality[instrument][q] > quality[instrument][highest]) highest = q;
		}
		return highest;
	}

	private void addStringToAllSheets(int instrument, String s){
		for(int o = 0; o < 11; o++){
			sheets[instrument][o] += s;
		}
	}

	public void getInstruments(String filePath) throws Exception {
		
		//Enumerate instruments
		Map<Integer, String> instrumentNames = new HashMap<>();
		for(Instrument instrument : MidiSystem.getSynthesizer().getDefaultSoundbank().getInstruments()) {
			instrumentNames.put(instrument.getPatch().getProgram(), instrument.getName());
		}
		/* Zero typically shows up as "Standard Kit" a.k.a. percussion, but this is typically because of midi editors
		 * that are trying to do the right thing, but don't have a correct patch to supply, and therefore erroneously
		 * supply program #0 instead of #1
		 */
		instrumentNames.put(0, "Unspecified");

		Sequence sequence = MidiSystem.getSequence(new File(filePath));

		instruments = new String[16];

		for (Track track :  sequence.getTracks()) {

			for (int i=0; i < track.size(); i++) {

				MidiEvent event = track.get(i);

				MidiMessage message = event.getMessage();
				
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;
					if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
						String instrumentName = instrumentNames.get(sm.getData1());
						if (instrumentName==null) instrumentName = "program"+sm.getData1();
						instrumentName += " ("+sm.getData1()+")";
						
						if (instruments[sm.getChannel()] == null) {
							instruments[sm.getChannel()] = instrumentName;
						} else {
							instruments[sm.getChannel()] += ", "+instrumentName;
						}
					}
				}
			}
		}
		
		for(String instrument : instruments) {
			if (instrument==null) continue;
			System.out.println("Adding instrument: '" + instrument + "'");

			if(instrument != null) {
				shownInstruments.add(instrument);
				allNull = false;
			}
		}
		
	}

	public String getSheet(int shownInstrument, int octave) {
		//int instrument = convertShownInstrument(shownInstrument);
		//if(instrument == -1 || sheets[instrument][octave] == null) return "";
		//else return sheets[instrument][octave];
		
		
		if (sheets[0][5]==null) return "";
		return sheets[0][5];
	}
}
