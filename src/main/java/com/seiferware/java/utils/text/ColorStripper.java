package com.seiferware.java.utils.text;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * A color parser that simply removes color markers.
 */
public class ColorStripper implements ColorParser {
	protected final char fg;
	protected final char bg;
	protected final Pattern pattern;
	protected final String fgid = UUID.randomUUID().toString();
	protected final String bgid = UUID.randomUUID().toString();
	protected final String bgbg;
	protected final String fgfg;
	
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
		StringBuilder sb = new StringBuilder();
		String fg1 = Pattern.quote(String.valueOf(fg));
		String bg1 = Pattern.quote(String.valueOf(bg));
		for(Color c : Color.values()) {
			sb.append(fg1).append(c.getCode()).append('|').append(bg1).append(c.getCode()).append('|');
		}
		sb.append(fg1).append("x|").append(bg1).append('x');
		pattern = Pattern.compile(sb.toString());
		bgbg = String.valueOf(bg) + String.valueOf(bg);
		fgfg = String.valueOf(fg) + String.valueOf(fg);
	}
	@NotNull
	@Override
	public String getResetSequence() {
		return "";
	}
	@NotNull
	@Override
	public String parseColors(@NotNull String data) {
		if(data.indexOf(fg) == data.indexOf(bg)) {
			return data;
		}
		data = data.replace(bgbg, bgid).replace(fgfg, fgid);
		data = pattern.matcher(data).replaceAll("");
		data = data.replace(bgid, String.valueOf(bg)).replace(fgid, String.valueOf(fg));
		return data;
		//StringBuilder sb = new StringBuilder(data);
		//String sfg = fg + "";
		//String sbg = bg + "";
		//int fgl = sb.indexOf(sfg);
		//int bgl = sb.indexOf(sbg);
		//int l = fgl < bgl && fgl != -1 || bgl == -1 ? fgl : bgl;
		//while(l != -1) {
		//	char code = (l == sb.length() - 1) ? ' ' : sb.charAt(l + 1);
		//	if(code == fg || code == bg) {
		//		sb.deleteCharAt(l);
		//	} else {
		//		sb.replace(l, l + 2, "");
		//	}
		//	fgl = sb.indexOf(sfg, l + 1);
		//	bgl = sb.indexOf(sbg, l + 1);
		//	l = fgl < bgl && fgl != -1 || bgl == -1 ? fgl : bgl;
		//}
		//return sb.toString();
	}
}
