package com.qfree.its.vianova_geoserver.json;

public class VmsStatus {
	public int vmsIndex;
	public VmsStatusInner vmsStatus;

	public String toString() {
		String ret = "";
		ret += " vmsIndex:" + vmsIndex;
		ret += " vmsStatus:" + vmsStatus;
		return ret;
	}
}
