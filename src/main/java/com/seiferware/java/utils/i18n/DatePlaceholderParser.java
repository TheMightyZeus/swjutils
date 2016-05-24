package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * A common interface for parsers that handle {@link Date} objects.
 */
public interface DatePlaceholderParser extends PlaceholderParser {
	/**
	 * This method will be called by default when {@code parseObject} is called with a {@link Date} object as the return
	 * value of {@code request.getTarget()}.
	 *
	 * @param in
	 * 		The return value of {@code request.getTarget()}.
	 * @param request
	 * 		The request to parse.
	 *
	 * @return The appropriately formatted date/time, or the empty string if the placeholder cannot be understood by the
	 * parser.
	 */
	@NotNull String parseDate(@NotNull Date in, @NotNull PlaceholderRequest request);
	@NotNull
	@Override
	default String parseObject(@NotNull PlaceholderRequest request) {
		if(request.getTarget() instanceof Date) {
			return parseDate((Date) request.getTarget(), request);
		}
		return "";
	}
}
