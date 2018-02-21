package com.github.enerccio.fmthelper;

import java.util.Locale;

import com.github.enerccio.fmthelper.helpers.PrintInvoker;

public class ParameterFormatElement implements FormatElement {

	private final int index;
	private final String identifier;
	private final int width;
	private final int precision;
	private final FormatType type;
	private final SimpleType stype;
	private final PrintInvoker invoker;
	
	public ParameterFormatElement(int index, String identifier, int width, int precision, FormatType type, SimpleType stype, PrintInvoker invoker) {
		this.index = index;
		this.identifier = identifier;
		this.width = width;
		this.precision = precision;
		this.type = type;
		this.stype = stype;
		this.invoker = invoker;
	}
	
	public String getStringValue() {
		return identifier;
	}

	public int getIndex() {
		return index;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getPrecision() {
		return precision;
	}
	
	public FormatType getType() {
		return type;
	}
	
	public SimpleType getSimpleType() {
		return stype;
	}

	public String print(Object arg) throws Exception {
		return invoker.print(arg);
	}

	public String print(Object arg, Locale l) throws Exception {
		return invoker.print(arg, l);
	}
}
