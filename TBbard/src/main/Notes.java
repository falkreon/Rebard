package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Notes {

	Robot r;

	static double waitMultiplier = 1;
	
	double lastTimestamp = 0;

	long waitTime = 0;

	int slowdownConstant = 0;

	boolean running = true;
	
	int fps = 0;

	private String[] notes = {"C(-1)", "C", "C(+1)", 
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
	
	/**
	 * Array to hold the keys corresponding to each note
	 * TODO Make this configurable by the user
	 */
	private int[] keys = {KeyEvent.VK_Q, 
			KeyEvent.VK_2, 
			KeyEvent.VK_W,
			KeyEvent.VK_3,
			KeyEvent.VK_E,
			KeyEvent.VK_R,
			KeyEvent.VK_5,
			KeyEvent.VK_T,
			KeyEvent.VK_6,
			KeyEvent.VK_Y,
			KeyEvent.VK_7,
			KeyEvent.VK_U,
			KeyEvent.VK_I
	};

	/**
	 * Array to hold the keys corresponding to the modifiers for the different
	 * TODO Make this configurable by the user
	 */
	private int[] octaveModifiers = {KeyEvent.VK_CONTROL,
			KeyEvent.VK_SHIFT
	};

	
	public Notes(int fps) {
		this.fps = fps;
		try {
			r = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void play(String note) {
		if(running == false) return;

		note = note.toLowerCase();

		Pattern pattern = Pattern.compile("(w|wait) ?(\\d+)");
		Matcher matcher = pattern.matcher(note.toLowerCase());
		if(matcher.matches()){
			waitTime = Long.parseLong((matcher.group(2)));
			System.out.println("Waiting for " + (waitTime*waitMultiplier + slowdownConstant));
			return;
		}

		pattern = Pattern.compile("(.b?#?)([+-][12])");
		matcher = pattern.matcher(note.toLowerCase());
		if(matcher.matches()){
			note = matcher.group(1) + "(" + matcher.group(2) + ")";
		}

		System.out.println("\n\nPlaying: " + note);
		
		int index = 0;
		
		for(String s : notes){
			if(s.toLowerCase().equals(note)){
				pressButton(index);
				break;
			}
			index++;
		}


	}

	private void pressButton(int i) {
		System.out.println("Pressing index: " + i);
		
		checkWaitTime();
		
		/* Doing a modulo operation on the index with 3 (Because there are 3 notes, one for each octave in the note table)
		 * With this we can get the index of the octave variation of the note the index points to */
		switch(i % 3) {
		case 0:
			r.keyPress(this.octaveModifiers[0]);
			System.out.println("Going down");
			break;
		case 2:
			r.keyPress(this.octaveModifiers[1]);
			System.out.println("Going Up");
			break;
		}
		
		/* Doing integer division with 3 (Because there are 3 notes, one for each octave in the note table we got the index from)
		 * With this we can get the index of the note of that pseudo-row the index points to */
		press(i / 3);
		
		try {
			Thread.sleep((long) Math.ceil((double) 1000/fps));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* Likewise as the first switch statement here */
		switch(i % 3) {
		case 0:
			r.keyRelease(this.octaveModifiers[0]);
			System.out.println("Releasing Down");
			break;
		case 2:
			r.keyRelease(this.octaveModifiers[1]);
			System.out.println("Releasing Up");
			break;
		}
	}

	private void press(int key) {
		r.keyPress(this.keys[key]);
		r.delay(1);
		r.keyRelease(this.keys[key]);

		lastTimestamp = System.currentTimeMillis();
	}

	private void checkWaitTime(){

		waitTime = (long) (waitTime*waitMultiplier + slowdownConstant);
		
		try {
			Thread.sleep((long) (waitTime));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		waitTime = 0;
	}
}
