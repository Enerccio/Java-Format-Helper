package com.github.enerccio.fmthelper;

import com.github.enerccio.fmthelper.helpers.FormatHelper;
import com.github.enerccio.fmthelper.helpers.FormatHelper6;
import com.github.enerccio.fmthelper.helpers.FormatHelper7;
import com.github.enerccio.fmthelper.helpers.FormatHelper8;

public class FormatUtils {
	
	private FormatUtils() {
		/* static utility class */
	}
	
	private static final FormatHelper helper;

	static {
		try {
			String javaVersion = System.getProperty("java.version");
			if (javaVersion.startsWith("1.6")) {
				helper = new FormatHelper6();
			} else if (javaVersion.startsWith("1.7")) {
				helper = new FormatHelper7();
			} else if (javaVersion.startsWith("1.8")) {
				helper = new FormatHelper8();
			} else {
				helper = new FormatHelper8();
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize format utils");
		}
	}
	
	public static FormatParseResult checkFormatString(String formatString) throws Exception {
		return helper.parseFormat(formatString);
	}
}
