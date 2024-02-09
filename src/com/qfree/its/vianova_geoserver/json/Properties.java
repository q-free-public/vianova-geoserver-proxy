package com.qfree.its.vianova_geoserver.json;

import java.util.ArrayList;

public class Properties {
	public String publicationTime;
	public PropMetaData metaData;
	public ArrayList<PropData> data;
	
	public String toString() {
		String ret = "";
		ret += " pubTime:" + publicationTime;
		ret += " metaData:[" + metaData + "]";
		if (data != null) {
//			ret += " data.cnt:" + data.size();
			for (PropData i : data) {
				ret += " " + i;
			}
		} else {
			ret += " data:null";
		}
		return ret;
	}
}
