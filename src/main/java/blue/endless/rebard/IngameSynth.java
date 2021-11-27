package blue.endless.rebard;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import blue.endless.rebard.score.Note;
import blue.endless.rebard.score.SequenceEvent;

public class IngameSynth implements ScoreSynth {
	private static int[] fullTBBardKeyboardBinds = {
			KeyEvent.VK_Y, KeyEvent.VK_V, KeyEvent.VK_U, KeyEvent.VK_B, KeyEvent.VK_I, KeyEvent.VK_O, KeyEvent.VK_N, KeyEvent.VK_P, KeyEvent.VK_M, KeyEvent.VK_A, KeyEvent.VK_COMMA, KeyEvent.VK_S, 
			KeyEvent.VK_9, KeyEvent.VK_K, KeyEvent.VK_0, KeyEvent.VK_L, KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_Z, KeyEvent.VK_E, KeyEvent.VK_X, KeyEvent.VK_R, KeyEvent.VK_C,     KeyEvent.VK_T,
			KeyEvent.VK_1, KeyEvent.VK_D, KeyEvent.VK_2, KeyEvent.VK_F, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_G, KeyEvent.VK_5, KeyEvent.VK_H, KeyEvent.VK_6, KeyEvent.VK_J,     KeyEvent.VK_7,
			KeyEvent.VK_8
			};
	
	private HashMap<Integer, SequenceEvent> activeNotes = new HashMap<>();
	private boolean foldNotes = true;
	private Robot robot;
	
	private static int[] activeBinds = fullTBBardKeyboardBinds.clone();
	
	public IngameSynth() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void consumeEvent(SequenceEvent evt) {
		if (robot==null) return;
		switch(evt.type()) {
		case NOTE_ON: {
			int noteToPlay = evt.arg1();
			
			if (foldNotes) {
				while (noteToPlay<Note.LOWEST_REPRESENTABLE_NOTE) noteToPlay += 12;
				while(noteToPlay>Note.HIGHEST_REPRESENTABLE_NOTE) noteToPlay -= 12;
			}
			
			int keyToPlay = getKeyForNote(noteToPlay);
			//if (keyToPlay==-1) {
			//	System.out.println("Note: "+evt.arg1()+" NoteToPlay: "+noteToPlay+" UNBOUND");
			//	return;
			//}
			
			/*SequenceEvent priorNote = activeNotes.get(noteToPlay);
			if (priorNote!=null) {
				robot.keyRelease(keyToPlay);
			}*/
			stopAll();
			robot.waitForIdle();
			
			System.out.println(evt);
			
			robot.keyPress(keyToPlay);
			activeNotes.put(noteToPlay, evt);
			robot.waitForIdle();
			break;
		}
		case NOTE_OFF: {
			int noteToPlay = evt.arg1();
			
			if (foldNotes) {
				while (noteToPlay<Note.LOWEST_REPRESENTABLE_NOTE) noteToPlay += 12;
				while(noteToPlay>Note.HIGHEST_REPRESENTABLE_NOTE) noteToPlay -= 12;
			}
			
			int keyToPlay = getKeyForNote(noteToPlay);
			if (keyToPlay==-1) return;
			
			SequenceEvent priorNote = activeNotes.get(noteToPlay);
			if (priorNote!=null) {
				robot.keyRelease(keyToPlay);
			}
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void stopAll() {
		if (robot==null) return;
		
		for(Map.Entry<Integer, SequenceEvent> entry : activeNotes.entrySet()) {
			int keyToRelease = getKeyForNote(entry.getKey());
			if (keyToRelease!=-1) robot.keyRelease(keyToRelease);
		}
		activeNotes.clear();
	}
	
	
	private int getKeyForNote(int note) {
		if (foldNotes) {
			while (note<Note.LOWEST_REPRESENTABLE_NOTE) note += 12;
			while (note>Note.HIGHEST_REPRESENTABLE_NOTE) note -= 12;
		}
		
		note -= Note.LOWEST_REPRESENTABLE_NOTE;
		
		if (note<0 || note>=activeBinds.length) return -1;
		return activeBinds[note];
	}
}
