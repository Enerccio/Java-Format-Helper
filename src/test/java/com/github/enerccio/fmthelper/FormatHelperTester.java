package com.github.enerccio.fmthelper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class FormatHelperTester extends TestCase {
	public FormatHelperTester(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(FormatHelperTester.class);
	}

	public void testFormatParse() throws Exception {
		FormatParseResult r;
		
		r = FormatUtils.checkFormatString("");		
		assertEquals(r.getTotalElements(), 0);
		assertEquals(r.getFixedElements(), 0);
		assertEquals(r.getParameterElements(), 0);
		
		r = FormatUtils.checkFormatString("constant string");		
		assertEquals(r.getTotalElements(), 1);
		assertEquals(r.getFixedElements(), 1);
		assertEquals(r.getParameterElements(), 0);
		assertTrue(r.getElements().get(0) instanceof FixedFormatElement);
		assertEquals(r.getElements().get(0).getStringValue(), "constant string");
		
		r = FormatUtils.checkFormatString("%s");		
		assertEquals(r.getTotalElements(), 1);
		assertEquals(r.getFixedElements(), 0);
		assertEquals(r.getParameterElements(), 1);
		assertTrue(r.getElements().get(0) instanceof ParameterFormatElement);
		assertEquals(r.getElements().get(0).getStringValue(), "%s");
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getIndex(), 0);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getPrecision(), -1);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getWidth(), -1);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getType(), FormatType.STRING);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getSimpleType(), SimpleType.STRING);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).print("test"), "test");
		
		r = FormatUtils.checkFormatString("%s %2f");		
		assertEquals(r.getTotalElements(), 3);
		assertEquals(r.getFixedElements(), 1);
		assertEquals(r.getParameterElements(), 2);
		assertTrue(r.getElements().get(0) instanceof ParameterFormatElement);
		assertEquals(r.getElements().get(0).getStringValue(), "%s");
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getIndex(), 0);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getPrecision(), -1);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getWidth(), -1);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getType(), FormatType.STRING);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getSimpleType(), SimpleType.STRING);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).print("test"), "test");
		assertTrue(r.getElements().get(1) instanceof FixedFormatElement);
		assertEquals(r.getElements().get(1).getStringValue(), " ");
		assertTrue(r.getElements().get(2) instanceof ParameterFormatElement);
		assertEquals(r.getElements().get(2).getStringValue(), "%2f");
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).getIndex(), 0);
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).getPrecision(), -1);
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).getWidth(), 2);
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).getType(), FormatType.REAL_DECIMAL);
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).getSimpleType(), SimpleType.REAL);
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).print(1.2f), "1.200000");
		
		r = FormatUtils.checkFormatString("%s %.2f");		
		assertEquals(r.getTotalElements(), 3);
		assertEquals(r.getFixedElements(), 1);
		assertEquals(r.getParameterElements(), 2);
		assertTrue(r.getElements().get(0) instanceof ParameterFormatElement);
		assertEquals(r.getElements().get(0).getStringValue(), "%s");
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getIndex(), 0);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getPrecision(), -1);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getWidth(), -1);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getType(), FormatType.STRING);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).getSimpleType(), SimpleType.STRING);
		assertEquals(((ParameterFormatElement)r.getElements().get(0)).print("test"), "test");
		assertTrue(r.getElements().get(1) instanceof FixedFormatElement);
		assertEquals(r.getElements().get(1).getStringValue(), " ");
		assertTrue(r.getElements().get(2) instanceof ParameterFormatElement);
		assertEquals(r.getElements().get(2).getStringValue(), "%.2f");
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).getIndex(), 0);
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).getPrecision(), 2);
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).getWidth(), -1);
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).getType(), FormatType.REAL_DECIMAL);
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).getSimpleType(), SimpleType.REAL);
		assertEquals(((ParameterFormatElement)r.getElements().get(2)).print(1.2f), "1.20");
	}
}
