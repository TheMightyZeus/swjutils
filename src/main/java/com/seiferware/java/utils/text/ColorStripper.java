package com.seiferware.java.utils.text;

/**
 * A color parser that simply removes color markers.
 */
public class ColorStripper implements ColorParser {
	protected final char fg;
	protected final char bg;
	
	/**
	 * Creates the parser.
	 * 
	 * @param fg
	 *            The character used to indicate the character that follows
	 *            represents the intended foreground color.
	 * @param bg
	 *            The character used to indicate the character that follows
	 *            represents the intended background color.
	 */
	public ColorStripper(char fg, char bg) {
		this.fg = fg;
		this.bg = bg;
	}
	@Override
	public String parseColors(String data) {
		StringBuilder sb = new StringBuilder(data);
		String sfg = fg + "";
		String sbg = bg + "";
		int fgl = sb.indexOf(sfg);
		int bgl = sb.indexOf(sbg);
		int l = fgl < bgl && fgl != -1 || bgl == -1 ? fgl : bgl;
		while(l != -1) {
			char code = sb.charAt(l + 1);
			if(code == fg || code == bg) {
				sb.deleteCharAt(l);
			} else {
				sb.replace(l, l + 2, "");
			}
			fgl = sb.indexOf(sfg, l + 1);
			bgl = sb.indexOf(sbg, l + 1);
			l = fgl > bgl || fgl == -1 ? bgl : fgl;
		}
		return sb.toString();
	}
}
