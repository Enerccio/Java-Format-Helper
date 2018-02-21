package com.github.enerccio.fmthelper;

import com.github.enerccio.fmthelper.helpers.FormatHelper;
import com.github.enerccio.fmthelper.helpers.SpecsFormatHelper;

public class FormatUtils {
	
	private FormatUtils() {
		/* static utility class */
	}
	
	private static final FormatHelper helper;

	static {
		try {
			helper = new SpecsFormatHelper();
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize format utils");
		}
	}
	
	public static FormatParseResult checkFormatString(String formatString) throws Exception {
		return helper.parseFormat(formatString);
	}
}
