package blue.endless.rebard.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class SimpleLabel extends JComponent implements Themeable {
	private static final long serialVersionUID = 1L;
	
	private ColorTheme theme;
	//private Font font = new Font("Arial", Font.PLAIN, 12);
	private String text = "";
	private double minimumRatio = 0.5;
	
	@Override
	public void paint(Graphics g) {
		String textToDisplay = text;
		Font fontToDisplay = this.getFont();
		int tries = 0;
		while(tries<100 && !canStringFit((Graphics2D) g, textToDisplay, fontToDisplay, this.getWidth())) {
			//figure out if we're already at the minimum ratio
			double originalSize = this.getFont().getSize2D();
			double smallerSize = fontToDisplay.getSize2D()-1.0;
			double smallerRatio = smallerSize / originalSize;
			if (smallerRatio >= minimumRatio) {
				fontToDisplay = fontToDisplay.deriveFont((float) smallerSize);
			} else {
				// We can't shrink the font any further, so let's truncate
				if (textToDisplay.length()>0) textToDisplay = textToDisplay.substring(0, textToDisplay.length()-1);
				if (textToDisplay.length()>0) textToDisplay = textToDisplay.substring(0, textToDisplay.length()-1);
				if (textToDisplay.length()>0) textToDisplay = textToDisplay.substring(0, textToDisplay.length()-1);
				if (textToDisplay.length()>0) textToDisplay = textToDisplay.substring(0, textToDisplay.length()-1);
				//textToDisplay += '.';//0x2026; //ellipsis
				
				//TODO: Insert ellipsis if this was truncated?
			}
			tries++;
		}
		if (tries>=100) System.out.println("Failed to truncate");
		//g.setColor(Color.RED);
		//g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
		
		Color color = (theme==null) ? Color.BLUE : ColorTheme.argb(theme.panelText);
		g.setFont(fontToDisplay);
		g.setColor(color);
		Point2D.Double topleft = startPosition((Graphics2D) g, textToDisplay, fontToDisplay);
		
		g.drawString(text, (int) -topleft.getX(), (int) -topleft.getY()); //(int) this.getHeight() - fontToDisplay.getSize());
	}

	@Override
	public void setTheme(ColorTheme theme) {
		this.theme = theme;
	}
	
	public void setText(String text) {
		this.text = text;
		this.repaint();
	}
	
	private static boolean canStringFit(Graphics2D g, String s, Font font, int maxWidth) {
		Rectangle2D rect = font.getStringBounds(s, ((Graphics2D) g).getFontRenderContext());
		return rect.getWidth() <= maxWidth;
	}
	
	private static Point2D.Double startPosition(Graphics2D g, String s, Font font) {
		Rectangle2D rect = font.getStringBounds(s, ((Graphics2D) g).getFontRenderContext());
		return new Point2D.Double(rect.getMinX(), rect.getMinY());
	}
}
