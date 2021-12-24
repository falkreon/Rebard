package blue.endless.rebard.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class LinearPanel extends JPanel implements Themeable {
	private static final long serialVersionUID = 1L;
	
	public static final int HORIZONTAL = BoxLayout.X_AXIS;
	public static final int VERTICAL = BoxLayout.Y_AXIS;
	
	private ColorTheme theme;
	private int borderThickness = 0;
	
	public LinearPanel(int layoutDirection) {
		this.setLayout(new BoxLayout(this, layoutDirection));
		
		
	}
	
	public void setBorderThickness(int thickness) {
		this.borderThickness = thickness;
		recreateBorder();
	}
	
	private void recreateBorder() {
		if (borderThickness==0) {
			this.setBorder(null);
		} else {
			if (theme!=null) {
				this.setBorder(BorderFactory.createLineBorder(ColorTheme.argb(theme.panelBorder), borderThickness));
			} else {
				this.setBorder(BorderFactory.createLineBorder(Color.BLACK, borderThickness));
			}
		}
	}

	@Override
	public void setTheme(ColorTheme theme) {
		for(Component comp : this.getComponents()) {
			if (comp instanceof Themeable) {
				((Themeable) comp).setTheme(theme);
			}
		}
		
		this.theme = theme;
		this.setBackground(ColorTheme.argb(theme.panelBackground));
		recreateBorder();
	}
}
