package com.qfree.its.vianova_geoserver.json;

public class Geometry {
	public String type;
	public double coordinates[];
	
	public String toString() {
		String ret = "";
		ret += type;
		ret += " ";
		if (coordinates != null && coordinates.length >= 2) {
			ret += String.format("%.6f,%.6f", coordinates[0], coordinates[1]);
		} else {
			ret += "no coord.";
		}
		return ret;
	}
}
