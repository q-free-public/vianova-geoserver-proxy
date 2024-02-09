package com.qfree.its.vianova_geoserver.json;

import java.util.ArrayList;

public class VmsStatusInner {
	public Boolean flashingLightsOn;
	public ArrayList<VmsMessage> vmsMessage;
	
	public String toString() {
		String ret = "";
//		ret += " flashingLightsOn:" + flashingLightsOn;
		if (vmsMessage != null) {
//			ret += " vmsMessage.cnt:" + vmsMessage.size();
			for (VmsMessage m : vmsMessage)
				ret += " " + m;
		} else {
			ret += " vmsMessage:null";
		}
			
		return ret;
	}
}
