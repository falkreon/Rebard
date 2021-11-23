package blue.endless.rebard;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Notes {
	private Robot robot;
	private int heldKey = -1;
	private int heldMod = -1;
	
	//String lastNote = "null";

	//double waitMultiplier = 1;

	//double lastTimestamp = 0;

	//long waitTime = 0;

	//int slowdownConstant = 0;

	//boolean running = true;
	//boolean holdNotes = true;

	private boolean fullKeyboard = false;
	//Keyboard kbrd = new Keyboard();

	//int fps = 0;

	private String[] notes = {
			"C(-1)", "C", "C(+1)", 
			"C#(-1)", "C#", "C#(+1)", 
			"D(-1)", "D", "D(+1)", 
			"Eb(-1)", "Eb", "Eb(+1)",
			"E(-1)", "E", "E(+1)",
			"F(-1)", "F", "F(+1)", 
			"F#(-1)", "F#", "F#(+1)",
			"G(-1)", "G", "G(+1)", 
			"G#(-1)", "G#", "G#(+1)",
			"A(-1)", "A", "A(+1)", 
			"Bb(-1)", "Bb", "Bb(+1)",
			"B(-1)", "B", "B(+1)",
			"C(+2)"
	};
	
	private int[] smallKeyboardKeys = {
			KeyEvent.VK_Q, KeyEvent.VK_Q, KeyEvent.VK_I, //C - note that high C is hit using I without the modifier
			KeyEvent.VK_2, KeyEvent.VK_2, KeyEvent.VK_2, //C#
			KeyEvent.VK_W, KeyEvent.VK_W, KeyEvent.VK_W, //D
			KeyEvent.VK_3, KeyEvent.VK_3, KeyEvent.VK_3, //Eb
			KeyEvent.VK_E, KeyEvent.VK_E, KeyEvent.VK_E, //E
			KeyEvent.VK_R, KeyEvent.VK_R, KeyEvent.VK_R, //F
			KeyEvent.VK_5, KeyEvent.VK_5, KeyEvent.VK_5, //F#
			KeyEvent.VK_T, KeyEvent.VK_T, KeyEvent.VK_T, //G
			KeyEvent.VK_6, KeyEvent.VK_6, KeyEvent.VK_6, //G#
			KeyEvent.VK_Y, KeyEvent.VK_Y, KeyEvent.VK_Y, //A
			KeyEvent.VK_7, KeyEvent.VK_7, KeyEvent.VK_7, //Bb
			KeyEvent.VK_U, KeyEvent.VK_U, KeyEvent.VK_U, //B
			KeyEvent.VK_I //C+2
	};
	
	private int[] smallKeyboardModifiers = {
			KeyEvent.VK_CONTROL, -1, -1,                 //C - note that high C is hit using I without the modifier
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //C#
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //D
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //Eb
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //E
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //F
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //F#
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //G
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //G#
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //A
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //Bb
			KeyEvent.VK_CONTROL, -1, KeyEvent.VK_SHIFT,  //B
			KeyEvent.VK_SHIFT //C+2
	};
	
	private int[] fullKeyboardKeys = {
			KeyEvent.VK_Y,     KeyEvent.VK_9, KeyEvent.VK_1, //C
			KeyEvent.VK_V,     KeyEvent.VK_K, KeyEvent.VK_D, //C#
			KeyEvent.VK_U,     KeyEvent.VK_0, KeyEvent.VK_2, //D
			KeyEvent.VK_B,     KeyEvent.VK_L, KeyEvent.VK_F, //Eb
			KeyEvent.VK_I,     KeyEvent.VK_Q, KeyEvent.VK_3, //E
			KeyEvent.VK_O,     KeyEvent.VK_W, KeyEvent.VK_4, //F
			KeyEvent.VK_N,     KeyEvent.VK_Z, KeyEvent.VK_G, //F#
			KeyEvent.VK_P,     KeyEvent.VK_E, KeyEvent.VK_5, //G
			KeyEvent.VK_M,     KeyEvent.VK_X, KeyEvent.VK_H, //G#
			KeyEvent.VK_A,     KeyEvent.VK_R, KeyEvent.VK_6, //A
			KeyEvent.VK_COMMA, KeyEvent.VK_C, KeyEvent.VK_J, //Bb
			KeyEvent.VK_S,     KeyEvent.VK_T, KeyEvent.VK_7, //B
			KeyEvent.VK_8 //C+2
			};


	public Notes(int fps) {
		//this.fps = fps;
		try {
			robot = new Robot();
		} catch (AWTException e) {} //Failure can be queried later
	}
	
	public boolean canPlayNotes() {
		return robot!=null;
	}

	public void play(String note, String nextNote) {
		if (!canPlayNotes()) return;

		//if(running == false) return;
		
		note = note.toLowerCase();
		if(nextNote != null) nextNote = nextNote.toLowerCase();
		
		System.out.println("\n--- " + note + " ---");
		System.out.println("NextNote: " + nextNote);
		//System.out.println("LastNote: " + lastNote);

		
		boolean hold = false;

		Pattern pattern = Pattern.compile("(r|release)");
		Matcher matcher = pattern.matcher(note.toLowerCase());
		if(matcher.matches()){
			pressKey(-1, -1, hold);
			System.out.println("\nReleasing key.\n");
			return;
		}
		
		//TODO: Parse wait time instead of pattern match
		//pattern = Pattern.compile("(w|wait) ?(\\d+)");
		//matcher = pattern.matcher(note.toLowerCase());
		//if(matcher.matches()){
			//waitTime = Long.parseLong((matcher.group(2)));

			//waitTime = (long) (waitTime*waitMultiplier);
//			if(waitTime < slowdownConstant){
//				System.out.println("Playback faster than framerate. Increasing wait.");
//				waitTime = slowdownConstant;
//			}
			/*
			boolean nextNoteIsSame = false;
			if(nextNote != null && lastNote.equals(nextNote)){
				System.out.println("The next note is the same as the held note. Adjusting wait time by " + slowdownConstant*2 + " ms (your fps delay), and releasing held key.");
				waitTime -= slowdownConstant*2;
				nextNoteIsSame = true;
			}

			System.out.println("Waiting for " + (waitTime));
			try {
				if(waitTime < 0){
					System.out.println("WaitTime is less than 0. Ignoring...");
				} else {
					Thread.sleep((long) (waitTime));
					if(nextNoteIsSame){
						releaseHeldKey();
						Thread.sleep((long) (slowdownConstant*2));
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			waitTime = 0;
			return;
		}*/
		
		//TODO: Parse holds instead of pattern match
		/*
		pattern = Pattern.compile("(h|hold) ?(.b?#?[+-]?[12]?)");
		matcher = pattern.matcher(note.toLowerCase());
		if(matcher.matches()){
			if(holdNotes){
				hold = true;
				System.out.println("Hold note.");
			}
			lastNote = note;
			note = matcher.group(2);
			
		} else {
			if(holdNotes){
				System.out.println("Don't hold note.");
			}
		}*/


		pattern = Pattern.compile("(.b?#?)([+-][12])");
		matcher = pattern.matcher(note.toLowerCase());
		if(matcher.matches()){
			note = matcher.group(1) + "(" + matcher.group(2) + ")";
			//lastNote = note;
		}

		System.out.println("Playing: " + note);
		
		releaseHeldKey();
		
		int noteIndex = -1;
		for(int i=0; i<notes.length; i++) {
			String s = notes[i];
			if(s.toLowerCase().equals(note.toLowerCase())) {
				noteIndex = i;
			}
		}
		
		if (noteIndex!=-1) {
			if(fullKeyboard) {
				pressKey(fullKeyboardKeys[noteIndex], -1, hold);
			} else {
				pressKey(smallKeyboardKeys[noteIndex], smallKeyboardModifiers[noteIndex], hold);
				/*
				for(int i=0; i<notes.length; i++) {
					String s = notes[i];
					if(s.toLowerCase().equals(note.toLowerCase())){
	
						pressButton(i, hold);
						break;
					}
				}*/
			}
		}
	}


	private void pressKey(int key, int modifier, boolean hold) {
		if(key == -1){
			System.out.println("Key does not exist.");
			return;
		}
		robot.keyPress(key);
		robot.keyRelease(KeyEvent.VK_SHIFT);
		//r.keyPress(keyToPress);
		robot.delay(1);
		if(hold) {
			//heldKey = keyToPress;
		} else {
			robot.keyRelease(key);
		}

		//lastTimestamp = System.currentTimeMillis();
	}

	public void releaseHeldKey(){
		if(heldKey != -1){
			robot.keyRelease(heldKey);
			heldKey = -1;
		}
		if(heldMod != -1){
			robot.keyRelease(heldMod);
			heldMod = -1;
		}
		robot.waitForIdle();
	}

	/*
	private void checkWaitTime(){

		waitTime = (long) (waitTime*waitMultiplier);

		try {
			Thread.sleep((long) (waitTime));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		waitTime = 0;
	}*/
}
