package blue.endless.rebard.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import blue.endless.rebard.score.Note;
import blue.endless.rebard.score.ScoreSequence;
import blue.endless.rebard.score.SequenceEvent;

public class ScoreView extends JComponent implements Themeable {
	private static final long serialVersionUID = 1L;
	
	private ColorTheme theme;
	private ScoreSequence sequence;
	private long timestamp = 0L;
	
	/*
	 * Key: MIDI note pitch
	 * Value: Array of NoteEvts in monotonic order
	 * 
	 * This happens in three steps: First they're sorted, then they're collapsed, with note-offs folding into the note-on
	 * preceding them. Finally, the lists are gathered one last time to find the truncated note lengths.
	 */
	private HashMap<Integer, ArrayList<NoteEvt>> processed = new HashMap<>();
	
	public ScoreView() {
		this.setMinimumSize(new Dimension(8,144));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 144));
		this.setPreferredSize(new Dimension(1024, 144));
		this.setBorder(BorderFactory.createLineBorder(Color.RED));
	}
	
	@Override
	public void paint(Graphics g) {
		//super.paint(g);
		
		Color bg = (theme!=null) ? ColorTheme.argb(theme.panelBackground) : Color.WHITE;
		Color fg = (theme!=null) ? ColorTheme.argb(theme.panelText) : Color.BLUE;
		
		int range = Note.HIGHEST_REPRESENTABLE_NOTE - Note.LOWEST_REPRESENTABLE_NOTE;
		//System.out.println(range*4);
		
		g.setColor(bg);
		g.fillRect(0,0,this.getWidth(), this.getHeight());
		
		if (sequence!=null) {
			//g.setColor(fg);
			
			long totalLength = sequence.timeLength();
			SortedSet<SequenceEvent> events = sequence.getMessages(0, totalLength+1);
			
			int ppq = sequence.getMetadata().getPPQ();
			double quarterWidth = (ppq / (double)totalLength) * this.getWidth();
			Color shadeColor = new Color(0x4F, 0x4F, 0xFF, 0x20);
			
			//boolean shade = true;
			if (quarterWidth>=2.0) {
				g.setColor(shadeColor);
				double prog = 0;
				while (prog<this.getWidth()) {
					g.fillRect((int)prog, 0, 1, 144);
					prog += quarterWidth;
				}
			}
			
			//shade = true;
			double measureWidth = quarterWidth * 4;
			Color measureShadeColor = new Color(0xFF, 0xFF, 0xFF, 0x20);
			if (measureWidth>=2.0) {
				g.setColor(measureShadeColor);
				double prog = 0;
				while (prog<this.getWidth()) {
					g.fillRect((int)prog, 0, 1, 144);
					prog += measureWidth;
				}
			}
			
			//Draw program/tempo changes as background
			for(SequenceEvent evt : events) {
				long timestamp = evt.timestamp();
				double fractionalTime = timestamp / (double)totalLength;
				int x = (int)(fractionalTime * this.getWidth());
				switch(evt.type()) {
				case NOTE_ON:
				case NOTE_OFF:
				case NOTE_CUT:
					break;
				default:
					g.setColor(Color.BLUE);
					g.fillRect(x, 0, 1, 144);
				}
			}
			
			/*
			//Draw note-offs behind note-ons
			for(SequenceEvent evt : events) {
				if (evt.type()==SequenceEvent.Type.NOTE_OFF || evt.type()==SequenceEvent.Type.NOTE_CUT) {
					long timestamp = evt.timestamp();
					double fractionalTime = timestamp / (double)totalLength;
					int x = (int)(fractionalTime * this.getWidth());
					int y = evt.arg1();
					boolean adjusted = false;
					while (y<Note.LOWEST_REPRESENTABLE_NOTE) {
						adjusted = true;
						y += 12;
					}
					while(y>Note.HIGHEST_REPRESENTABLE_NOTE) {
						adjusted = true;
						y -= 12;
					}
					y -= Note.LOWEST_REPRESENTABLE_NOTE;
					
					g.setColor(adjusted ? Color.MAGENTA : Color.RED);
					g.fillRect(x, (range*4)-(y*4), 2, 4);
				}
			}
			
			//Draw note-offs behind note-ons
			for(SequenceEvent evt : events) {
				if (evt.type()==SequenceEvent.Type.NOTE_ON) {
					long timestamp = evt.timestamp();
					double fractionalTime = timestamp / (double)totalLength;
					int x = (int)(fractionalTime * this.getWidth());
					int y = evt.arg1();
					boolean adjusted = false;
					while (y<Note.LOWEST_REPRESENTABLE_NOTE) {
						adjusted = true;
						y += 12;
					}
					while(y>Note.HIGHEST_REPRESENTABLE_NOTE) {
						adjusted = true;
						y -= 12;
					}
					y -= Note.LOWEST_REPRESENTABLE_NOTE;
					
					
					
					g.setColor(adjusted ? Color.ORANGE : Color.GREEN);
					g.fillRect(x, (range*4)-(y*4), 2, 4);
				}
			}*/
			
			for(Map.Entry<Integer, ArrayList<NoteEvt>> entry : processed.entrySet()) {
				int note = entry.getKey();
				if (note!=-1) {
					int y = note;
					boolean adjusted = false;
					while (y<Note.LOWEST_REPRESENTABLE_NOTE) {
						adjusted = true;
						y += 12;
					}
					while(y>Note.HIGHEST_REPRESENTABLE_NOTE) {
						adjusted = true;
						y -= 12;
					}
					y -= Note.LOWEST_REPRESENTABLE_NOTE;
					
					g.setColor(adjusted ? new Color(0x00, 0x80, 0x00) : Color.GREEN);
					
					for(NoteEvt evt : entry.getValue()) {
						int x = (int) ((evt.timestamp() / (double) totalLength) * this.getWidth());
						int width = (int) ((evt.length() / (double) totalLength) * this.getWidth());
						if (width<1) width = 1;
						
						g.fillRect(x, (range*4)-(y*4), width, 4);
					}
				}
			}
			
			
			if (timestamp!=0L) {
				int x = (int) ((timestamp / (double)totalLength) * this.getWidth());
				g.setColor(Color.WHITE);
				g.fillRect(x, 0, 1, this.getHeight());
			}
		}
	}

	@Override
	public void setTheme(ColorTheme theme) {
		this.theme = theme;
	}
	
	public void setSequence(ScoreSequence seq) {
		this.sequence = seq;
		
		processed.clear();
		
		//Sort events by type and note
		SortedSet<SequenceEvent> events = sequence.getMessages(0, Integer.MAX_VALUE);
		for(SequenceEvent event : events) {
			switch(event.type()) {
			case NOTE_ON:
			case NOTE_OFF:
			case NOTE_CUT:
				NoteEvt noteEvent = new NoteEvt(event.timestamp(), -1L, -1L, event.type(), event.arg1());
				ArrayList<NoteEvt> eventList = processed.get(event.arg1());
				if (eventList==null) {
					eventList = new ArrayList<>();
					processed.put(event.arg1(), eventList);
				}
				eventList.add(noteEvent);
				break;
			case TEMPO_CHANGE:
			case PROGRAM_CHANGE:
			case PARAMETER_CHANGE:
				NoteEvt progEvent = new NoteEvt(event.timestamp(), -1L, -1L, event.type(), event.arg2());
				ArrayList<NoteEvt> progList = processed.get(-1);
				if (progList==null) {
					progList = new ArrayList<>();
					processed.put(-1, progList);
				}
				progList.add(progEvent);
				break;
			}
		}
		
		//Go through each pitch and marry note-off messages to note-on messages
		for(Map.Entry<Integer, ArrayList<NoteEvt>> entry : processed.entrySet()) {
			int note = entry.getKey();
			if (note==-1) continue;
			NoteEvt lastNoteOn = null;
			for(NoteEvt evt : entry.getValue()) {
				if (evt.type()==SequenceEvent.Type.NOTE_OFF || evt.type()==SequenceEvent.Type.NOTE_CUT) {
					if (lastNoteOn!=null) {
						if (lastNoteOn.length==-1) {
							lastNoteOn.length = evt.timestamp() - lastNoteOn.timestamp();
						} else {
							System.out.println("WARN: Extra NOTE_OFFs for already-quiet channel - "+evt);
						}
					} else {
						System.out.println("WARN: NOTE_OFF before any NOTE_ONs on this channel - "+evt);
					}
				} else if (evt.type()==SequenceEvent.Type.NOTE_ON) {
					lastNoteOn = evt;
				}
			}
		}
		
		repaint();
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	private class NoteEvt {
		public long timestamp;
		public long length;
		public long truncatedLength;
		public SequenceEvent.Type type;
		public int note;
		
		public NoteEvt(long timestamp, long length, long truncatedLength, SequenceEvent.Type type, int note) {
			this.timestamp = timestamp;
			this.length = length;
			this.truncatedLength = truncatedLength;
			this.type = type;
			this.note = note;
		}
		
		public long timestamp() {
			return this.timestamp;
		}
		
		public SequenceEvent.Type type() {
			return this.type;
		}
		
		public long length() {
			return this.length;
		}
		
		public String toString() {
			return
				"{ "+
				"timestamp: "+timestamp+", "+
				"length: "+length+", "+
				"truncatedLength: "+truncatedLength+", "+
				"type: "+type+", "+
				"note: "+note+" "+
				"}";
		}
	}
}
