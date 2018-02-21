package com.github.enerccio.fmthelper.helpers;

import java.util.Locale;

public interface PrintInvoker {

	String print(Object arg) throws Exception;
	String print(Object arg, Locale l) throws Exception;
	
}
