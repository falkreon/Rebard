package blue.endless.rebard.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Score {
	private HashMap<Integer, String> patches = new HashMap<>();
	private ArrayList<ScoreSequence> sequences = new ArrayList<>();
	
	public Score(Map<Integer, String> patches, List<ScoreSequence> sequences) {
		this.patches.putAll(patches);
		this.sequences.addAll(sequences);
	}
	
	public int sequenceCount() { return sequences.size(); }
	
	public ScoreSequence getSequence(int index) { return sequences.get(index); }
	
	public Set<Integer> getPatchNumbers() { return patches.keySet(); }
	
	public String getPatch(int index) {
		return patches.get(index);
	}
}
