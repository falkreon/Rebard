package blue.endless.rebard;

import java.util.HashMap;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import blue.endless.rebard.score.Note;
import blue.endless.rebard.score.SequenceEvent;

public class GeneralMidiSynth implements ScoreSynth {
	private boolean open = false;
	private Synthesizer synth;
	private MidiChannel midiChannel;
	private boolean foldNotes = true; //Whether to transpose notes into the bard keyboard range
	
	private HashMap<Integer, SequenceEvent> activeNotes = new HashMap<>();
	
	
	public GeneralMidiSynth() {
		try {
			synth = MidiSystem.getSynthesizer();
			MidiChannel[] channels = synth.getChannels();
			if (channels.length>0) {
				midiChannel = channels[0];
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			synth.close();
			synth = null;
			midiChannel = null;
			open = false;
		}
		
	}
	
	@Override
	public void consumeEvent(SequenceEvent evt) {
		if (!open && synth!=null) {
			try {
				synth.open();
				for(Instrument inst : synth.getAvailableInstruments()) {
					if (inst.getPatch().getProgram()==6) {
						System.out.println("Using harpsichord ("+inst.getName()+")");
						synth.loadInstrument(inst);
						midiChannel.programChange(inst.getPatch().getBank(), inst.getPatch().getProgram());
						break;
					}
				}
				//midiChannel.programChange(46); //Orchestral Harp, the classic solo bard performance
				open = true;
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				synth.close();
				synth = null;
				midiChannel = null;
				open = false;
			}
		}
		
		switch(evt.type()) {
		case NOTE_ON: {
			int noteToPlay = evt.arg1();
			
			if (foldNotes) {
				while (noteToPlay<Note.LOWEST_REPRESENTABLE_NOTE) noteToPlay += 12;
				while(noteToPlay>Note.HIGHEST_REPRESENTABLE_NOTE) noteToPlay -= 12;
			}
			
			SequenceEvent priorNote = activeNotes.get(noteToPlay);
			if (priorNote!=null) {
				midiChannel.noteOff(noteToPlay);
			}
			
			System.out.println(evt);
			
			midiChannel.noteOn(noteToPlay, 100);
			activeNotes.put(noteToPlay, evt);
			break;
		}
		case NOTE_OFF: {
			int noteToPlay = evt.arg1();
			
			if (foldNotes) {
				while (noteToPlay<Note.LOWEST_REPRESENTABLE_NOTE) noteToPlay += 12;
				while(noteToPlay>Note.HIGHEST_REPRESENTABLE_NOTE) noteToPlay -= 12;
			}
			
			SequenceEvent priorNote = activeNotes.get(noteToPlay);
			if (priorNote!=null) {
				midiChannel.noteOff(noteToPlay);
			}
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void stopAll() {
		if (midiChannel!=null) midiChannel.allNotesOff();
		activeNotes.clear();
		
		if (synth==null) return;
		synth.close();
		synth = null;
		midiChannel = null;
		open = false;
	}

}
