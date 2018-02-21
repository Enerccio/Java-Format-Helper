package com.github.enerccio.fmthelper;

import java.util.Locale;

public class FixedFormatElement implements FormatElement {
	
	private final String fixedValue;
	
	public FixedFormatElement(String fixedValue) {
		this.fixedValue = fixedValue;
	}

	public String getStringValue() {
		return fixedValue;
	}

	public String print(Object arg) throws Exception {
		return fixedValue;
	}

	public String print(Object arg, Locale l) throws Exception {
		return fixedValue;
	}

}
