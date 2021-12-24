package blue.endless.rebard.gui;

import java.awt.Color;

public class ColorTheme {
	public int panelBackground = 0xFF_303030;
	public int panelBorder = 0xFF_505050;
	public int panelText = 0xFF_999999;
	public int menuBackground = 0xFF_404040;
	public int menuText = 0xFF_ACACAC;
			
	public static Color argb(int argb) {
		int a = (argb >> 24) & 0xFF;
		int r = (argb >> 16) & 0xFF;
		int g = (argb >>  8) & 0xFF;
		int b = (argb      ) & 0xFF;
		return new Color(r, g, b, a);
	}
}
