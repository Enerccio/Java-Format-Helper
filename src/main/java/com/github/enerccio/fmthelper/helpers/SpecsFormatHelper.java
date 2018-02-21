package com.github.enerccio.fmthelper.helpers;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.IllegalFormatPrecisionException;
import java.util.IllegalFormatWidthException;
import java.util.List;
import java.util.Locale;
import java.util.UnknownFormatConversionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.enerccio.fmthelper.FixedFormatElement;
import com.github.enerccio.fmthelper.Flags;
import com.github.enerccio.fmthelper.FormatElement;
import com.github.enerccio.fmthelper.FormatParseResult;
import com.github.enerccio.fmthelper.FormatType;
import com.github.enerccio.fmthelper.ParameterFormatElement;
import com.github.enerccio.fmthelper.SimpleType;

// based on http://grepcode.com/file_/repository.grepcode.com/java/root/jdk/openjdk/6-b14/java/util/Formatter.java/?v=source

public class SpecsFormatHelper implements FormatHelper {

	private static final char DECIMAL_INTEGER = 'd';
	private static final char OCTAL_INTEGER = 'o';
	private static final char HEXADECIMAL_INTEGER = 'x';
	private static final char HEXADECIMAL_INTEGER_UPPER = 'X';
	private static final char SCIENTIFIC = 'e';
	private static final char SCIENTIFIC_UPPER = 'E';
	private static final char GENERAL = 'g';
	private static final char GENERAL_UPPER = 'G';
	private static final char DECIMAL_FLOAT = 'f';
	private static final char HEXADECIMAL_FLOAT = 'a';
	private static final char HEXADECIMAL_FLOAT_UPPER = 'A';
	private static final char CHARACTER = 'c';
	private static final char CHARACTER_UPPER = 'C';
	private static final char DATE_TIME = 't';
	private static final char DATE_TIME_UPPER = 'T';
	private static final char BOOLEAN = 'b';
	private static final char BOOLEAN_UPPER = 'B';
	private static final char STRING = 's';
	private static final char STRING_UPPER = 'S';
	private static final char HASHCODE = 'h';
	private static final char HASHCODE_UPPER = 'H';
	private static final char LINE_SEPARATOR = 'n';
	private static final char PERCENT_SIGN = '%';

	private static final String formatSpecifier = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";
	private static Pattern fsPattern = Pattern.compile(formatSpecifier);

	public FormatParseResult parseFormat(String s) throws Exception {
		FormatParseResult r = new FormatParseResult();
		List<FormatElement> al = new ArrayList<FormatElement>();

		Matcher m = fsPattern.matcher(s);
		int i = 0;
		while (i < s.length()) {
			if (m.find(i)) {
				if (m.start() != i) {
					checkText(s.substring(i, m.start()));
					al.add(fixedString(s.substring(i, m.start())));
				}

				String[] sa = new String[6];
				for (int j = 0; j < m.groupCount(); j++) {
					sa[j] = m.group(j + 1);
				}
				al.add(formatSpecifier(sa));
				i = m.end();
			} else {
				checkText(s.substring(i));
				al.add(fixedString(s.substring(i)));
				break;
			}
		}

		r.setElements(al);
		return r;
	}

	private void checkText(String s) {
		int idx;
		if ((idx = s.indexOf('%')) != -1) {
			char c = (idx > s.length() - 2 ? '%' : s.charAt(idx + 1));
			throw new UnknownFormatConversionException(String.valueOf(c));
		}
	}

	private FixedFormatElement fixedString(String s) {
		return new FixedFormatElement(s);
	}

	private ParameterFormatElement formatSpecifier(String[] sa) throws Exception {
		int idx = 0;

		boolean dt = false;
		char c;
		int index = -1;
		int width = -1;
		int precision = -1;
		String s;

		s = sa[idx++];
		if (s != null) {
			try {
				index = Integer.parseInt(s.substring(0, s.length() - 1));
			} catch (NumberFormatException x) {
				assert (false);
			}
		} else {
			index = 0;
		}

		s = sa[idx++];
		Flags f = Flags.parse(s);
		if (f.contains(Flags.PREVIOUS))
			index = -1;

		s = sa[idx++];
		if (s != null) {
			try {
				width = Integer.parseInt(s);
				if (width < 0)
					throw new IllegalFormatWidthException(width);
			} catch (NumberFormatException x) {
				assert (false);
			}
		}

		s = sa[idx++];
		if (s != null) {
			try {
				// remove the '.'
				precision = Integer.parseInt(s.substring(1));
				if (precision < 0)
					throw new IllegalFormatPrecisionException(precision);
			} catch (NumberFormatException x) {
				assert (false);
			}
		}

		if (sa[idx] != null) {
			dt = true;
			if (sa[idx].equals("T"))
				f.add(Flags.UPPERCASE);
		}

		s = sa[++idx];
		c = s.charAt(0);
		if (!dt) {
			if (!isValid(c))
				throw new UnknownFormatConversionException(String.valueOf(c));
			if (Character.isUpperCase(c))
				f.add(Flags.UPPERCASE);
			c = Character.toLowerCase(c);
			if (isText(c))
				index = -2;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("%");
		// Flags.UPPERCASE is set internally for legal conversions.
		Flags dupf = f.dup().remove(Flags.UPPERCASE);
		sb.append(dupf.toString());
		if (index > 0)
			sb.append(index).append('$');
		if (width != -1)
			sb.append(width);
		if (precision != -1)
			sb.append('.').append(precision);
		if (dt)
			sb.append(f.contains(Flags.UPPERCASE) ? 'T' : 't');
		sb.append(f.contains(Flags.UPPERCASE) ? Character.toUpperCase(c) : c);

		final String textRepresentation = sb.toString();

		FormatType type = getType(c);
		SimpleType stype = getSimpleType(type);

		return new ParameterFormatElement(index, textRepresentation, f, width, precision, type, stype, new PrintInvoker() {

			public String print(Object arg, Locale l) throws Exception {
				Formatter fmt = new Formatter(l);
				String v = fmt.format(l, textRepresentation, arg).toString();
				fmt.close();
				return v;
			}

			public String print(Object arg) throws Exception {
				return print(arg, Locale.getDefault());
			}
		});
	}

	protected FormatType getType(char c) throws Exception {
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

	static boolean isValid(char c) {
		return (isGeneral(c) || isInteger(c) || isFloat(c) || isText(c) || c == 't' || isCharacter(c));
	}

	static boolean isGeneral(char c) {
		switch (c) {
		case BOOLEAN:
		case BOOLEAN_UPPER:
		case STRING:
		case STRING_UPPER:
		case HASHCODE:
		case HASHCODE_UPPER:
			return true;
		default:
			return false;
		}
	}

	static boolean isCharacter(char c) {
		switch (c) {
		case CHARACTER:
		case CHARACTER_UPPER:
			return true;
		default:
			return false;
		}
	}

	static boolean isInteger(char c) {
		switch (c) {
		case DECIMAL_INTEGER:
		case OCTAL_INTEGER:
		case HEXADECIMAL_INTEGER:
		case HEXADECIMAL_INTEGER_UPPER:
			return true;
		default:
			return false;
		}
	}

	static boolean isFloat(char c) {
		switch (c) {
		case SCIENTIFIC:
		case SCIENTIFIC_UPPER:
		case GENERAL:
		case GENERAL_UPPER:
		case DECIMAL_FLOAT:
		case HEXADECIMAL_FLOAT:
		case HEXADECIMAL_FLOAT_UPPER:
			return true;
		default:
			return false;
		}
	}

	static boolean isText(char c) {
		switch (c) {
		case LINE_SEPARATOR:
		case PERCENT_SIGN:
			return true;
		default:
			return false;
		}
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

}
