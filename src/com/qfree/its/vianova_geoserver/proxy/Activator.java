/*
 * Created on Wed Dec 06 12:10:49 CET 2023
 */
package com.qfree.its.vianova_geoserver.proxy;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletResponse;

import org.cvis.service.webrequest.WebRequestQueue;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

import com.google.gson.Gson;
import com.qfree.its.vianova_geoserver.json.DisplayAreaSettingsInner;
import com.qfree.its.vianova_geoserver.json.Feature;
import com.qfree.its.vianova_geoserver.json.GeoserverReply;

public class Activator implements BundleActivator, EventHandler {

	public final static String url = "https://ogckart-proxy-utv.utv.atlas.vegvesen.no/svv_datex_3_1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=svv_datex_3_1%3AVms_OST&outputFormat=application%2Fjson";
	public final String notifyTopic = "geoserver/update";
	public final int    timeoutSeconds = 60;

	public static ServiceTracker<WebRequestQueue, WebRequestQueue>     stWebRequestQueue;
	public static ServiceTracker<EventAdmin, EventAdmin>               stEventAdmin;
	public static ServiceTracker<HttpService, ServletGeoserverProxy>   stHttpService;
	public static SimpleReply                                          simpleVmsList;
	public static HashMap<Integer, Long>                               httpStats = new HashMap<Integer, Long>();
	private ServletGeoserverProxy                                      proxyServlet;
    private Timer                                                      timerQueryServer = null;

	public void start(BundleContext context) throws Exception {
		System.out.println("Geoserver: starting");
		
		stWebRequestQueue = new ServiceTracker<>(context, WebRequestQueue.class.getName(), null);
		stWebRequestQueue.open();

		stEventAdmin = new ServiceTracker<EventAdmin,EventAdmin>(context, EventAdmin.class, null);
		stEventAdmin.open();

		// The HttpService service tracker will start the HttpServlet.
		proxyServlet = new ServletGeoserverProxy(context);
		stHttpService = new ServiceTracker<HttpService, ServletGeoserverProxy>(context, HttpService.class, proxyServlet);
		stHttpService.open();

		// Subscribe to location events
		Dictionary<String, String[]> servProps = new Hashtable<String, String[]>();
		String strTopics[] = new String[] {
				notifyTopic
		};
		servProps.put(EventConstants.EVENT_TOPIC, strTopics);
		context.registerService(EventHandler.class, this, servProps);

		// Get list from GeoServer every 2 minutes
	    TimerTask taskQueryServer = new TimerTask() {
	        public void run() {
	        	queryGeoserver();
	        }
	    };
	    timerQueryServer = new Timer("QueryGeoserverTimer");
	    long delay = timeoutSeconds * 1000L;
	    timerQueryServer.scheduleAtFixedRate(taskQueryServer, 0, delay);
	}

	protected void queryGeoserver() {
		WebRequestQueue webRequestQueue = Activator.stWebRequestQueue.getService();
		if (webRequestQueue != null) {
			if (webRequestQueue.isStalled(timeoutSeconds))
				return;
			
			// Query server
			webRequestQueue.setRetryInterval(timeoutSeconds * 1000); // 120 seconds, it will try forever.
			webRequestQueue.addGetRequest(url, 0, notifyTopic);
			System.out.println("Geoserver: Upload queued");
		} else {
			System.out.println("Geoserver: Upload failed: No WebReuestQueue");
		}
	}

	public void stop(BundleContext context) throws Exception {
		if (proxyServlet != null) {
			ServiceReference<HttpService> srHttpService = stHttpService.getServiceReference();
			proxyServlet.removedService(srHttpService, proxyServlet);
		}
		
		if (timerQueryServer != null) {
			timerQueryServer.cancel();
			timerQueryServer = null;
		}
	}
	
	@Override
	public void handleEvent(Event event) {
		System.out.println("Geoserver: Event: " + event.getTopic());
		
		try {
			int code = (Integer) event.getProperty(WebRequestQueue.HTTP_RESPONSE_CODE);
			if (httpStats.containsKey(code)) {
				httpStats.put(code, httpStats.get(code) + 1);
			} else {
				httpStats.put(code, 1L);
			}
			
			if (code == HttpServletResponse.SC_OK || code == HttpServletResponse.SC_ACCEPTED) {
				// HTTP Get success
				System.out.println("HTTP get success: code = " + code);
				ByteArrayOutputStream body = (ByteArrayOutputStream) event.getProperty(WebRequestQueue.CONTENT_BODY);
				String json = body.toString("UTF-8");
				processLargeJson(json);
			} else {
				// Server returned error code. If no network, it will retry forever.
				System.out.println("HTTP get failure: code = " + code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processLargeJson(String json) {
		//System.out.println(json);
		System.out.println("Big JSON string with " + json.length() + " chars.");
		Gson gson = new Gson();
		GeoserverReply reply = gson.fromJson(json, GeoserverReply.class);
//		System.out.println(reply);
		SimpleReply simpleReply = createSimpleReply(reply);
		//System.out.println(simpleReply);
		
//		for (SimpleVmsSign i : simpleReply.signList) i.imageData = null;
		String simpleJson = gson.toJson(simpleReply);
//		System.out.println(simpleJson);
		System.out.println("SimpleJson is " + simpleJson.length() + " bytes");
		System.out.println("LargeJson was " + json.length() + " bytes");
		simpleVmsList = simpleReply;
	}

	private SimpleReply createSimpleReply(GeoserverReply reply) {
		SimpleReply ret = new SimpleReply();
		ret.retreiveDate = new Date().toString();
		ret.url = url;
		ret.signList = new ArrayList<SimpleVmsSign>();
		for (Feature f : reply.features) {
			SimpleVmsSign simple = new SimpleVmsSign();
			try {
				simple.isBlank = true;
				simple.latitude = f.geometry.coordinates[1];
				simple.longitude = f.geometry.coordinates[0];
				simple.pubTime = f.properties.publicationTime;
				simple.id = f.id;
				simple.signId = f.properties.metaData.datex.id;
				if (f.properties.data != null && !f.properties.data.isEmpty() && f.properties.data.get(0).datex.vmsStatus != null && !f.properties.data.get(0).datex.vmsStatus.isEmpty()) {
					simple.vmsIndex = f.properties.data.get(0).datex.vmsStatus.get(0).vmsIndex;
					DisplayAreaSettingsInner dispArea = f.properties.data.get(0).datex.vmsStatus.get(0).vmsStatus.vmsMessage.get(0).vmsMessage.displayAreaSettings.get(0).displayAreaSettings;
					simple.isBlank = dispArea.isBlank;
					if (!simple.isBlank) {
						simple.imageData = dispArea.image.imageData;
						simple.imageFormat = dispArea.image.imageFormat.value;
						simple.hasSpeed = dispArea.pictogram != null && dispArea.pictogram.displayedNumericalInformation != null && !dispArea.pictogram.displayedNumericalInformation.isEmpty();
						if (simple.hasSpeed && "speed".equals(dispArea.pictogram.displayedNumericalInformation.get(0).numericalInformationType.value)) {
							simple.speedLimitValue = dispArea.pictogram.displayedNumericalInformation.get(0).numericValue;
						}
					}
				} else {
					simple.error = "properties.data is missing or empty";
				}
			} catch (Exception e) {
				simple.error = e.getClass().getSimpleName();
				e.printStackTrace();
			}
			ret.signList.add(simple);
		}
		return ret;
	}
}
