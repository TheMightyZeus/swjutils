package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link PlaceholderParser} that accepts an integer and one argument, which is the length to which to zero-pad the
 * number.
 *
 * @see PlaceholderParser
 */
public class IntegerPlaceholderParser implements PlaceholderParser {
	@Override
	public @NotNull String parseObject(@NotNull PlaceholderRequest request) throws PlaceholderException {
		if(request.getArgs().length == 0) {
			return request.getTarget().toString();
		}
		if(request.getTarget() instanceof Integer) {
			int val = (int) request.getTarget();
			try {
				int len = Integer.parseInt(request.getArgs()[0]);
				return String.format(request.getLocale(), "%" + len + "d", val);
			} catch (NumberFormatException e) {
				throw new PlaceholderException("Length argument is not a number.", e);
			}
		}
		return request.getTarget().toString();
	}
}
