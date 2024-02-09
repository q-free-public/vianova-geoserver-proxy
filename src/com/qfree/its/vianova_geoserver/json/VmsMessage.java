package com.qfree.its.vianova_geoserver.json;

public class VmsMessage {
	public int messageIndex;
	public VmsMessageInner vmsMessage;
	
	public String toString() {
		String ret = "";
//		ret += " messageIndex:" + messageIndex;
//		ret += " vmsMessage:" + vmsMessage;
		ret += vmsMessage;
		return ret;
	}
}
