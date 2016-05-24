package com.seiferware.java.utils.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A color parser suitable for sending color text to compatible telnet-style clients.
 */
public class AnsiColorParser implements ColorParser {
	protected static final char ESCAPE_CHAR = '\u001b';
	protected final char fg;
	protected final char bg;
	protected boolean allowbg = true;
	protected boolean allowfg = true;
	/**
	 * Creates the parser.
	 *
	 * @param fg
	 * 		The character used to indicate the character that follows represents the intended foreground color.
	 * @param bg
	 * 		The character used to indicate the character that follows represents the intended background color.
	 */
	public AnsiColorParser(char fg, char bg) {
		this.fg = fg;
		this.bg = bg;
	}
	@NotNull
	protected static String getColorReset() {
		return ESCAPE_CHAR + "[0m";
	}
	@NotNull
	protected static String getColorSequence(@Nullable Color clr) {
		return getColorSequence(clr, false);
	}
	@NotNull
	protected static String getColorSequence(@Nullable Color clr, boolean background) {
		if(clr == null) {
			return "";
		}
		return ESCAPE_CHAR + "[" + (clr.getNum() + (background ? 40 : 30)) + (background ? "" : (clr.isBright() ? ";1" : ";22")) + "m";
	}
	@NotNull
	@Override
	public String getResetSequence() {
		return fg + "x";
	}
	@NotNull
	@Override
	public String parseColors(@NotNull String data) {
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
				boolean isbg = l == bgl;
				if(isbg && allowbg || !isbg && allowfg) {
					if(code == 'x') {
						sb.replace(l, l + 2, getColorReset());
					} else {
						sb.replace(l, l + 2, getColorSequence(Color.findColor(code), isbg));
					}
				} else {
					sb.replace(l, l + 2, "");
				}
			}
			fgl = sb.indexOf(sfg, l + 1);
			bgl = sb.indexOf(sbg, l + 1);
			l = fgl < bgl && fgl != -1 || bgl == -1 ? fgl : bgl;
		}
		return sb.toString();
	}
}
