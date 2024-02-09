package com.qfree.its.vianova_geoserver.proxy;

import java.util.ArrayList;

public class SimpleReply {
	public String                   retreiveDate;
	public String                   url;
	public ArrayList<SimpleVmsSign> signList;
	
	public String toString() {
		String ret = "";
		ret += "retreiveDate: " + retreiveDate + "\r\n";
		ret += "url:          " + url + "\r\n";
		ret += "signList.cnt: " + signList.size() + "\r\n";
		for (SimpleVmsSign s : signList) {
			ret += s + "\r\n";
		}
		return ret;
	}
}
