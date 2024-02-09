package com.qfree.its.vianova_geoserver.json;

public class Feature {
	public String type;
	public String id;
	public Geometry geometry;
	public Properties properties;

	public String toString() {
		String ret = "";
//		ret += type;  // "Feature"
//		ret += " ";
		ret += id;
		ret += " ";
		ret += geometry;
		ret += " ";
		ret += properties;

		return ret;
	}
}
