package blue.endless.rebard.score;

public record SequenceEvent(Type type, int column, int arg1, int arg2) {
	
	
	public static enum Type {
		/**
		 * Change instrument to the program number specified by arg1. Column and arg2 are ignored.
		 */
		PROGRAM_CHANGE,
		/**
		 * Change some parameter of the current program. arg1 is the parametr, and arg2 is the value.
		 */
		PARAMETER_CHANGE,
		/**
		 * Trigger a note. arg1 is the Note number, and arg2 is the 
		 */
		NOTE_ON,
		/**
		 * Explicitly end a note. The column is indicated by pitch, or -1 for all notes.
		 */
		NOTE_OFF,
		/**
		 * Abruptly stop a note from playing further. The column is indicated by pitch, or -1 for all notes.
		 */
		NOTE_CUT;
	}
}
