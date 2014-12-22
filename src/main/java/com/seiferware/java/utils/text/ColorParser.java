package com.seiferware.java.utils.text;

/**
 * An interface for converting color indicators to implementation-specific color
 * directives.
 */
public interface ColorParser {
	/**
	 * Convert the provided string to an implementation-specific colored string.
	 * 
	 * @param data
	 *            The text to convert.
	 * @return The converted text.
	 */
	public String parseColors(String data);
	
	/**
	 * An enumeration representing the 16 basic colors.
	 */
	public static enum Color {
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
		private char name;
		private int num;
		private boolean bright;
		
		private Color(char name, int num, boolean bright) {
			this.name = name;
			this.num = num;
			this.bright = bright;
		}
		/**
		 * Returns the character representing the color.
		 * 
		 * @return The color code.
		 */
		public char getName() {
			return name;
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
		/**
		 * Uses the character code to retrieve the color.
		 * 
		 * @param name
		 *            The character code representing the color.
		 * @return The Color.
		 */
		public static Color findColor(char name) {
			for(Color clr : Color.values()) {
				if(clr.getName() == name) {
					return clr;
				}
			}
			return null;
		}
	}
}
