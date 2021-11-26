package blue.endless.rebard;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.Instrument;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

import blue.endless.rebard.midi.SequenceMeta;
import blue.endless.rebard.score.Score;
import blue.endless.rebard.score.SequenceEvent;
import blue.endless.rebard.score.TimedSequence;

public class MidiParser {

	String lastNote = "";
	int lastOctave;
	
	boolean allNull = true;


	public MidiParser() {
	}

	public Score getScore(File f) throws Exception {
		ArrayList<TimedSequence> sequences = new ArrayList<>();
		
		HashMap<Integer, SequenceEvent> heldNotes = new HashMap<>();
		TimedSequence timedSequence = new TimedSequence();

		Sequence sequence = MidiSystem.getSequence(f);
		
		Map<Integer, String> patches = new HashMap<>();
		for(Instrument instrument : MidiSystem.getSynthesizer().getDefaultSoundbank().getInstruments()) {
			patches.put(instrument.getPatch().getProgram(), instrument.getName());
		}
		
		SequenceMeta midiMeta = new SequenceMeta();
		
		for (Track track :  sequence.getTracks()) {
			
			//long prevTick = 0;
			//boolean firstLineDone = false;
			
			//long runningTicks = 0L;
			for (int i=0; i < track.size(); i++) {
				//try { Thread.sleep(200); } catch (InterruptedException iex) {}
				MidiEvent event = track.get(i);
				MidiMessage msg = event.getMessage();
				
				if (msg instanceof ShortMessage) {
					
					ShortMessage sm = (ShortMessage) msg;
					
					int key = sm.getData1();
					int velocity = sm.getData2();
					
					switch (sm.getCommand()) {
					case ShortMessage.NOTE_ON:
						if (velocity!=0) {
							SequenceEvent noteOnEvt = new SequenceEvent(event.getTick(), SequenceEvent.Type.NOTE_ON, key, key, velocity);
							timedSequence.add(noteOnEvt);
							heldNotes.put(key, noteOnEvt);
							//System.out.println(noteOnEvt);
							//channels[0].noteOn(key, 100);
						} else {
							SequenceEvent previousNote = heldNotes.remove(key);
							if (previousNote!=null) {
								SequenceEvent noteCutEvt = new SequenceEvent(event.getTick(), SequenceEvent.Type.NOTE_CUT, key, key, 0);
								timedSequence.add(noteCutEvt);
								//System.out.println(noteCutEvt);
								//channels[0].noteOff(key);
							} else {
								//System.out.println("Extraneous NOTE_CUT on key "+key);
							}
						}
						break;
					case ShortMessage.NOTE_OFF:
						SequenceEvent previousNote = heldNotes.remove(key);
						if (previousNote!=null) {
							SequenceEvent noteOffEvt = new SequenceEvent(event.getTick(), SequenceEvent.Type.NOTE_OFF, key, key, 0);
							timedSequence.add(noteOffEvt);
							//System.out.println(noteOffEvt);
							//channels[0].noteOff(key);
						} else {
							//System.out.println("Extraneous NOTE_OFF on key "+key);
						}
						break;
					case ShortMessage.PROGRAM_CHANGE:
						System.out.println("Program Change: Patch 0x"+Integer.toHexString(sm.getData1()));
						break;
					default:
						System.out.println("ShortMessage: 0x"+Integer.toHexString(sm.getCommand()));
					}
					
				} else if (msg instanceof MetaMessage) {
					MetaMessage meta = (MetaMessage) msg;
					
					midiMeta.applyMeta(meta.getType(), meta.getData());
					
					if (meta.getType()==0x58) {
						byte[] d = meta.getData();
						System.out.println(Arrays.toString(d));
						if (d.length>=4) {
							int numerator = d[0];
							int denominator = (int) Math.pow(2, d[1]);
							
							// Honestly the metronome timings and 32nd-notes-per-24-midi-clocks means nothing to me.
							// It certainly doesn't tell me what the BPM is.
							
							System.out.println("Time Signature: "+numerator+"/"+denominator);
						}
					}
				} else if (msg instanceof SysexMessage) {
					System.out.println("SysexMessage ("+msg.getLength()+"B)");
				}

			}
			
			timedSequence.setMetadata(midiMeta);
			sequences.add(timedSequence);
			System.out.println("MIDI Meta: "+midiMeta.toString());
		}

		return new Score(patches, sequences);

	}
	
}
