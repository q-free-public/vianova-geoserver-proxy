package com.qfree.its.vianova_geoserver.json;

import java.util.ArrayList;

public class Pictogram {
	public boolean presenceOfRedTriangle;
	public ArrayList<DisplayedNumericalInformation> displayedNumericalInformation;
	
	public String toString() {
		String ret = "";
//		ret += " presenceOfRedTriangle:" + presenceOfRedTriangle;
		ret += " displayedNumericalInformation:" + displayedNumericalInformation;
		return ret;
	}
}
