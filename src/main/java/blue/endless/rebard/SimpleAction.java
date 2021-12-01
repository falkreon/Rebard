package blue.endless.rebard;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class SimpleAction extends AbstractAction {
	private static final long serialVersionUID = -1L;
	
	private final String name;
	private final Consumer<ActionEvent> event;
	
	public SimpleAction(String name, Consumer<ActionEvent> event) {
		this.name = name;
		this.event = event;
	}
	
	@Override
	public Object getValue(String key) {
		if (key.equals(Action.NAME)) {
			return this.name;
		} else {
			return super.getValue(key);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		event.accept(e);
	}

}
