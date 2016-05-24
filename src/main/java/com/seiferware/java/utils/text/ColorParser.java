package com.seiferware.java.utils.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for converting color indicators to implementation-specific color directives.
 */
public interface ColorParser {
	/**
	 * Gets the character sequence needed to clear all special formatting for this implementation. Returns the empty
	 * string if this implementation does not support formatting.
	 *
	 * @return The required character sequence, or the empty string.
	 */
	@NotNull String getResetSequence();
	/**
	 * Convert the provided string to an implementation-specific colored string.
	 *
	 * @param data
	 * 		The text to convert.
	 *
	 * @return The converted text.
	 */
	@NotNull String parseColors(@NotNull String data);
	/**
	 * An enumeration representing the 16 basic colors.
	 */
	enum Color {
		/**
		 * Black {@code 0x000000}
		 */
		BLACK('k', 0, false),
		/**
		 * Red {@code 0x800000}
		 */
		RED('r', 1, false),
		/**
		 * Green {@code 0x008000}
		 */
		GREEN('g', 2, false),
		/**
		 * Yellow {@code 0x808000}
		 */
		YELLOW('y', 3, false),
		/**
		 * Blue {@code 0x000080}
		 */
		BLUE('b', 4, false),
		/**
		 * Magenta {@code 0x800080}
		 */
		MAGENTA('m', 5, false),
		/**
		 * Cyan {@code 0x008080}
		 */
		CYAN('c', 6, false),
		/**
		 * White (light gray) {@code 0xC0C0C0}
		 */
		WHITE('w', 7, false),
		/**
		 * Bright black (dark gray) {@code 0x808080}
		 */
		BRIGHT_BLACK('K', 0, true),
		/**
		 * Bright Red {@code 0xFF0000}
		 */
		BRIGHT_RED('R', 1, true),
		/**
		 * Bright Green {@code 0x00FF00}
		 */
		BRIGHT_GREEN('G', 2, true),
		/**
		 * Bright Yellow {@code 0xFFFF00}
		 */
		BRIGHT_YELLOW('Y', 3, true),
		/**
		 * Bright Blue {@code 0x0000FF}
		 */
		BRIGHT_BLUE('B', 4, true),
		/**
		 * Bright Magenta {@code 0xFF00FF}
		 */
		BRIGHT_MAGENTA('M', 5, true),
		/**
		 * Bright Cyan {@code 0x00FFFF}
		 */
		BRIGHT_CYAN('C', 6, true),
		/**
		 * Bright White {@code 0xFFFFFF}
		 */
		BRIGHT_WHITE('W', 7, true);
		private char code;
		private int num;
		private boolean bright;
		Color(char code, int num, boolean bright) {
			this.code = code;
			this.num = num;
			this.bright = bright;
		}
		/**
		 * Uses the character code to retrieve the color.
		 *
		 * @param code
		 * 		The character code representing the color.
		 *
		 * @return The Color.
		 */
		@Nullable
		public static Color findColor(char code) {
			for(Color clr : Color.values()) {
				if(clr.getCode() == code) {
					return clr;
				}
			}
			return null;
		}
		/**
		 * Returns the character representing the color.
		 *
		 * @return The color code.
		 */
		public char getCode() {
			return code;
		}
		/**
		 * Gets the ANSI number representing the color.
		 *
		 * @return The number.
		 */
		public int getNum() {
			return num;
		}
		/**
		 * Whether the color is a "bright" color.
		 *
		 * @return {@code true} if the color is "bright".
		 */
		public boolean isBright() {
			return bright;
		}
	}
}
