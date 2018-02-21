package com.github.enerccio.fmthelper;

import java.util.List;

public class FormatParseResult {
	
	private List<FormatElement> elements;

	public List<FormatElement> getElements() {
		return elements;
	}

	public void setElements(List<FormatElement> elements) {
		this.elements = elements;
	} 
	
	public int getTotalElements() {
		return elements.size();
	}
	
	public int getFixedElements() {
		int total = 0;
		for (FormatElement e : elements)
			if ((e instanceof FixedFormatElement))
				++total;
		return total;
	}
	
	public int getParameterElements() {
		int total = 0;
		for (FormatElement e : elements)
			if ((e instanceof ParameterFormatElement))
				++total;
		return total;
	}

}
