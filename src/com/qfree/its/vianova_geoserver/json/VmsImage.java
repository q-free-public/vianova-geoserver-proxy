package com.qfree.its.vianova_geoserver.json;

public class VmsImage {
	public ValueExtendedValue  imageFormat;
	public String               imageData;
	
	public String toString() {
		String ret = "";
		ret += "imageFormat:" + imageFormat.value;
		ret += " image:" + imageData.length() + " bytes";
		return ret;
	}
}
