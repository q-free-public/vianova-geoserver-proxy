package com.qfree.its.vianova_geoserver.json;

public class DisplayedNumericalInformation {
	public int numericValue;
	public ValueExtendedValue numericalInformationType;
	public ValueExtendedValue unitOfMeasure;
	
	public String toString() {
		String ret = "";
		ret += " " + numericalInformationType;
		ret += " " + numericValue;
		ret += " " + unitOfMeasure;
		return ret;
	}
}
