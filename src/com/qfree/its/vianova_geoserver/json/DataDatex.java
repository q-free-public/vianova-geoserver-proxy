package com.qfree.its.vianova_geoserver.json;

import java.util.ArrayList;

public class DataDatex {
	public ArrayList<VmsStatus> vmsStatus;

	public String toString() {
		String ret = "";
//		ret += " vmsStatus:[" + vmsStatus + "]";
		ret += vmsStatus;
		return ret;
	}
}
