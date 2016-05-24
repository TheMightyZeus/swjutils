package com.seiferware.java.utils.i18n;

import com.seiferware.java.utils.text.TextUtils;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link PlaceholderParser} that accepts a string, numerical length, and optionally "left" or "right". The string is
 * padded to the provided length on the specified side, or right-padded if no side is specified.
 *
 * @see PlaceholderParser
 */
public class PadStringPlaceholderParser implements PlaceholderParser {
	@Override
	public @NotNull String parseObject(@NotNull PlaceholderRequest request) throws PlaceholderException {
		String start = request.getTarget().toString();
		int len;
		try {
			len = Integer.parseInt(request.getArgs()[0]);
		} catch (NumberFormatException e) {
			throw new PlaceholderException("Length argument is not a number.", e);
		}
		if(request.getArgs().length == 1) {
			return TextUtils.rightPad(start, len, true);
		} else if(request.getArgs().length == 2) {
			if(request.getArgs()[1].equalsIgnoreCase("left")) {
				return TextUtils.leftPad(start, len, true);
			} else {
				return TextUtils.rightPad(start, len, true);
			}
		}
		return start;
	}
}
