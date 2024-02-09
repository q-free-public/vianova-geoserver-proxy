package com.qfree.its.vianova_geoserver.json;

import java.util.ArrayList;

public class VmsMessageInner {
	public ArrayList<DisplayAreaSettings> displayAreaSettings;
	
	public String toString() {
		String ret = "";
		if (displayAreaSettings != null) {
//			ret += " displayAreaSettings:" + displayAreaSettings.size();
			for (DisplayAreaSettings s : displayAreaSettings)
				ret += " " + s;
		} else
			ret += " null";
		return ret;
	}
}
