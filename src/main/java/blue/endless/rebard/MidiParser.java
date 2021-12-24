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

import blue.endless.rebard.score.Score;
import blue.endless.rebard.score.SequenceEvent;
import blue.endless.rebard.score.SequenceMeta;
import blue.endless.rebard.score.ScoreSequence;

public class MidiParser {

	public MidiParser() {
	}

	public Score getScore(File f) throws Exception {
		ArrayList<ScoreSequence> sequences = new ArrayList<>();
		
		HashMap<Integer, SequenceEvent> heldNotes = new HashMap<>();
		ScoreSequence timedSequence = new ScoreSequence();

		Sequence sequence = MidiSystem.getSequence(f);
		
		
		Map<Integer, String> patches = new HashMap<>();
		for(Instrument instrument : MidiSystem.getSynthesizer().getDefaultSoundbank().getInstruments()) {
			patches.put(instrument.getPatch().getProgram(), instrument.getName());
		}
		
		SequenceMeta midiMeta = new SequenceMeta();
		
		if (sequence.getDivisionType()==Sequence.PPQ) {
			System.out.println("PPQ: "+sequence.getResolution());
			midiMeta.setPPQ(sequence.getResolution());
		}
		
		
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
						} else {
							SequenceEvent previousNote = heldNotes.remove(key);
							if (previousNote!=null) {
								SequenceEvent noteCutEvt = new SequenceEvent(event.getTick(), SequenceEvent.Type.NOTE_CUT, key, key, 0);
								timedSequence.add(noteCutEvt);
							}
						}
						break;
					case ShortMessage.NOTE_OFF:
						SequenceEvent previousNote = heldNotes.remove(key);
						if (previousNote!=null) {
							SequenceEvent noteOffEvt = new SequenceEvent(event.getTick(), SequenceEvent.Type.NOTE_OFF, key, key, 0);
							timedSequence.add(noteOffEvt);
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
					
					if (meta.getType()==0x51) {
						//System.out.println("Tempo Change");
						byte[] d = meta.getData();
						//System.out.println(Arrays.toString(d));
						if (d.length>=3) {
							int usecsPerQuarter = 0;
							usecsPerQuarter |= (d[0] & 0xFF) << 16;
							usecsPerQuarter |= (d[1] & 0xFF) << 8;
							usecsPerQuarter |= (d[2] & 0xFF);
							double bpm = 60_000_000.0 / (double) usecsPerQuarter;
							SequenceEvent evt = new SequenceEvent(event.getTick(), SequenceEvent.Type.TEMPO_CHANGE, 0, usecsPerQuarter, (int)bpm);
							timedSequence.add(evt);
						} else {
							//System.out.println("Len="+d.length);
						}
					//} else  if (meta.getType()==0x51) {
						
					} else {
						//System.out.println("Midi Meta: "+Integer.toHexString(meta.getType()));
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
