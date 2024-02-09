package com.qfree.its.vianova_geoserver.json;

public class DisplayAreaSettingsInner {
	public boolean isBlank;
	public Pictogram pictogram;
	public VmsImage image;
	
	public String toString() {
		String ret = "";
		ret += "isBlank:" + isBlank;
		ret += " pict:" + pictogram;
		ret += " image:" + image;
		return ret;
	}
}
