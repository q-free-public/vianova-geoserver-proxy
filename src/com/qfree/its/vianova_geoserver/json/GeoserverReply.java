package com.qfree.its.vianova_geoserver.json;

import java.util.ArrayList;

public class GeoserverReply {
	public String 				type;
	public ArrayList<Feature>  features;
	
	public String toString() {
		String ret = "";
		ret += "type: " + type;
		if (features != null) {
			ret += "feature.cnt: " + features.size();
			for (Feature f : features) {
				ret += "\r\n";
				ret += f.toString();
			}
		}
		
		return ret;
	}
}
