package com.github.enerccio.fmthelper.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import com.github.enerccio.fmthelper.FixedFormatElement;
import com.github.enerccio.fmthelper.FormatElement;
import com.github.enerccio.fmthelper.FormatParseResult;
import com.github.enerccio.fmthelper.FormatType;
import com.github.enerccio.fmthelper.ParameterFormatElement;
import com.github.enerccio.fmthelper.SimpleType;

public class FormatHelper6 implements FormatHelper {

	protected Method parseAccessor;
	protected Method indexAccessor;
	protected Method widthAccessor;
	protected Method precisionAccessor;
	protected Method printAccessor;

	protected Field fmtResetField;
	protected Field getConversionField;

	protected Class<?> formatArgumentClass;
	protected Class<?> formatFixedClass;
	protected Class<?> formatParameterClass;

	static final char DECIMAL_INTEGER = 'd';
	static final char OCTAL_INTEGER = 'o';
	static final char HEXADECIMAL_INTEGER = 'x';
	static final char HEXADECIMAL_INTEGER_UPPER = 'X';
	static final char SCIENTIFIC = 'e';
	static final char SCIENTIFIC_UPPER = 'E';
	static final char GENERAL = 'g';
	static final char GENERAL_UPPER = 'G';
	static final char DECIMAL_FLOAT = 'f';
	static final char HEXADECIMAL_FLOAT = 'a';
	static final char HEXADECIMAL_FLOAT_UPPER = 'A';
	static final char CHARACTER = 'c';
	static final char CHARACTER_UPPER = 'C';
	static final char DATE_TIME = 't';
	static final char DATE_TIME_UPPER = 'T';
	static final char BOOLEAN = 'b';
	static final char BOOLEAN_UPPER = 'B';
	static final char STRING = 's';
	static final char STRING_UPPER = 'S';
	static final char HASHCODE = 'h';
	static final char HASHCODE_UPPER = 'H';
	static final char LINE_SEPARATOR = 'n';
	static final char PERCENT_SIGN = '%';

	public FormatHelper6() throws Exception {
		initialize6();
	}

	private void initialize6() throws Exception {
		formatArgumentClass = Class.forName("java.util.Formatter$FormatString");
		formatFixedClass = Class.forName("java.util.Formatter$FixedString");
		formatParameterClass = Class.forName("java.util.Formatter$FormatSpecifier");

		parseAccessor = Formatter.class.getDeclaredMethod("parse", String.class);
		parseAccessor.setAccessible(true);
		indexAccessor = formatParameterClass.getDeclaredMethod("index");
		indexAccessor.setAccessible(true);
		widthAccessor = formatParameterClass.getDeclaredMethod("width");
		widthAccessor.setAccessible(true);
		precisionAccessor = formatParameterClass.getDeclaredMethod("precision");
		precisionAccessor.setAccessible(true);
		printAccessor = formatParameterClass.getDeclaredMethod("print", Object.class, Locale.class);
		printAccessor.setAccessible(true);

		fmtResetField = formatParameterClass.getDeclaredField("this$0");
		fmtResetField.setAccessible(true);
		getConversionField = formatParameterClass.getDeclaredField("c");
		getConversionField.setAccessible(true);
	}

	public FormatParseResult parseFormat(String formatString) throws Exception {
		FormatParseResult r = new FormatParseResult();
		r.setElements(parse(formatString));
		return r;
	}

	protected List<FormatElement> parse(String formatString) throws Exception {
		List<FormatElement> elements = new ArrayList<FormatElement>();

		Formatter fmt = new Formatter();
		Object[] formatComponents = (Object[]) parseAccessor.invoke(fmt, formatString);
		for (Object fmtComponent : formatComponents) {
			if (formatFixedClass.isInstance(fmtComponent)) {
				elements.add(loadFixedStringElement(fmtComponent));
			}
			if (formatParameterClass.isInstance(fmtComponent)) {
				elements.add(loadParameterElement(fmtComponent));
			}
		}

		return elements;
	}

	protected FixedFormatElement loadFixedStringElement(Object c) throws Exception {
		return new FixedFormatElement(c.toString());
	}

	protected FormatElement loadParameterElement(Object c) throws Exception {
		FormatType type = getType(c);
		SimpleType stype = getSimpleType(type);
		return new ParameterFormatElement((Integer) indexAccessor.invoke(c), c.toString(),
				(Integer) widthAccessor.invoke(c), (Integer) precisionAccessor.invoke(c), type, stype, makeInvoker(c));
	}

	/**
	 * Converts type to simple type
	 * 
	 * @param type
	 * @return simplified type (useful for deciding on input formats)
	 */
	protected SimpleType getSimpleType(FormatType type) {
		switch (type) {
		case CHARACTER:
		case CHARACTER_UPPER:
			return SimpleType.CHARACTER;
		case DATE_TIME:
		case DATE_TIME_UPPER:
			return SimpleType.DATE_TIME;
		case HASHCODE:
		case HASHCODE_UPPER:
			return SimpleType.HASHCODE;
		case INTEGER_DECIMAL:
		case INTEGER_HEXADECIMAL:
		case INTEGER_HEXADECIMAL_UPPER:
		case INTEGER_OCTAL:
			return SimpleType.INTEGER;
		case LINE_SEPARATOR:
			return SimpleType.LINE_SEPARATOR;
		case PERCENT_SIGN:
			return SimpleType.PERC_SIGN;
		case REAL_GENERAL:
		case REAL_GENERAL_UPPER:
		case REAL_HEXADECIMAL_FLOAT:
		case REAL_HEXADECIMAL_FLOAT_UPPER:
		case REAL_SCIENTIFIC:
		case REAL_SCIENTIFIC_UPPER:
		case REAL_DECIMAL:
			return SimpleType.REAL;
		case BOOLEAN:
		case BOOLEAN_UPPER:
			return SimpleType.BOOLEAN;
		case STRING:
		case STRING_UPPER:
			return SimpleType.STRING;
		default:
			break;
		}
		return null;
	}

	protected FormatType getType(Object component) throws Exception {
		char c = (Character) getConversionField.get(component);
		switch (c) {
		case DECIMAL_INTEGER:
			return FormatType.INTEGER_DECIMAL;
		case OCTAL_INTEGER:
			return FormatType.INTEGER_OCTAL;
		case HEXADECIMAL_INTEGER:
			return FormatType.INTEGER_HEXADECIMAL;
		case HEXADECIMAL_INTEGER_UPPER:
			return FormatType.INTEGER_HEXADECIMAL_UPPER;
		case SCIENTIFIC:
			return FormatType.REAL_SCIENTIFIC;
		case SCIENTIFIC_UPPER:
			return FormatType.REAL_SCIENTIFIC_UPPER;
		case GENERAL:
			return FormatType.REAL_GENERAL;
		case GENERAL_UPPER:
			return FormatType.REAL_GENERAL_UPPER;
		case DECIMAL_FLOAT:
			return FormatType.REAL_DECIMAL;
		case HEXADECIMAL_FLOAT:
			return FormatType.REAL_HEXADECIMAL_FLOAT;
		case HEXADECIMAL_FLOAT_UPPER:
			return FormatType.REAL_HEXADECIMAL_FLOAT_UPPER;
		case CHARACTER:
			return FormatType.CHARACTER;
		case CHARACTER_UPPER:
			return FormatType.CHARACTER_UPPER;
		case DATE_TIME:
			return FormatType.DATE_TIME;
		case DATE_TIME_UPPER:
			return FormatType.DATE_TIME_UPPER;
		case BOOLEAN:
			return FormatType.BOOLEAN;
		case BOOLEAN_UPPER:
			return FormatType.BOOLEAN_UPPER;
		case STRING:
			return FormatType.STRING;
		case STRING_UPPER:
			return FormatType.STRING_UPPER;
		case HASHCODE:
			return FormatType.HASHCODE;
		case HASHCODE_UPPER:
			return FormatType.HASHCODE_UPPER;
		case LINE_SEPARATOR:
			return FormatType.LINE_SEPARATOR;
		case PERCENT_SIGN:
			return FormatType.PERCENT_SIGN;
		}
		return null;
	}

	private PrintInvoker makeInvoker(final Object c) {
		return new PrintInvoker() {

			public String print(Object arg, Locale l) throws Exception {
				synchronized (c) {
					Formatter f = new Formatter(l);
					fmtResetField.set(c, f);
					printAccessor.invoke(c, arg, l);
					return f.toString();
				}
			}

			public String print(Object arg) throws Exception {
				return print(arg, Locale.getDefault());
			}
		};
	}
}
