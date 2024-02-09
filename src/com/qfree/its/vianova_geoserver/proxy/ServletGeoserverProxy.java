package com.qfree.its.vianova_geoserver.proxy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.google.gson.Gson;

public class ServletGeoserverProxy extends ServletQfree implements ServiceTrackerCustomizer<HttpService, ServletGeoserverProxy> {

	private static final long serialVersionUID = 1L;
	public static final String SESSION_ID_TAG = "sessionid";

	private       BundleContext bundleContext;
	private final String        servletBaseDir = "/geoserver";
	private long cntJsonAll;
	private long cntJsonSpeed;

	public ServletGeoserverProxy(BundleContext context) {
		this.bundleContext = context;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURL().toString();
		if (req.getQueryString() != null)
			url += "?" + req.getQueryString();
		System.out.println("GeoserverProxy.doGet " + url);

		try {
			if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
				resp.sendRedirect(servletBaseDir.substring(1) + "/index.html");
				return;
			}

			if (req.getPathInfo().equals("/index.html")) {
//				if (Activator.optServletPreferHttps) {
//					if (url.startsWith("http://") && !url.contains("//localhost")) {
//						// Redirect to https
//						resp.sendRedirect(url.replace("http:", "https:"));
//						return;
//					}
//				}

				getIndexHtml(req, resp);
				return;
			}
			if (req.getPathInfo().equals("/img")) {
				getImage(req, resp, req.getQueryString());
				return;
			}
			if (req.getPathInfo().equals("/all.json")) {
				getAllJson(resp);
				cntJsonAll++;
				return;
			}
			if (req.getPathInfo().equals("/speed.json")) {
				getSpeedJson(resp);
				cntJsonSpeed++;
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
	
	private void getAllJson(HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json;charset=\"UTF-8\"");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("X-Robots-Tag", "noindex");
		PrintWriter pw = resp.getWriter();
		Gson gson = new Gson();
		String simpleJson = gson.toJson(Activator.simpleVmsList);
		pw.println(simpleJson);
	}

	private void getSpeedJson(HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json;charset=\"UTF-8\"");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("X-Robots-Tag", "noindex");
		PrintWriter pw = resp.getWriter();
		SimpleReply speeds = new SimpleReply();
		speeds.retreiveDate = Activator.simpleVmsList.retreiveDate;
		speeds.url = Activator.simpleVmsList.url;
		speeds.signList = new ArrayList<>();
		for (SimpleVmsSign sign : Activator.simpleVmsList.signList) {
			if (!sign.isBlank && sign.hasSpeed) {
				SimpleVmsSign simple = new SimpleVmsSign();
				simple.latitude = sign.latitude;
				simple.longitude = sign.longitude;
				simple.pubTime = sign.pubTime;
				simple.id = sign.id;
				simple.signId = sign.signId;
				simple.vmsIndex = sign.vmsIndex;
				simple.isBlank = sign.isBlank;
				simple.imageData = null;
				simple.imageFormat = null;
				simple.hasSpeed = sign.hasSpeed;
				simple.speedLimitValue = sign.speedLimitValue;
				speeds.signList.add(simple);
			}
		}
		Gson gson = new Gson();
		String simpleJson = gson.toJson(speeds);
		pw.println(simpleJson);
	}

	private void getImage(HttpServletRequest req, HttpServletResponse resp, String imgId) throws IOException {
		for (SimpleVmsSign sign : Activator.simpleVmsList.signList) {
			if (imgId.equalsIgnoreCase(sign.signId) && sign.imageData != null) {
				resp.setContentType("image/png");
				byte[] binImage = Base64.getDecoder().decode(sign.imageData.getBytes());
				ServletOutputStream os = resp.getOutputStream();
				os.write(binImage);
				return;
			}
		}
		
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private void getIndexHtml(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html;charset=\"UTF-8\"");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("X-Robots-Tag", "noindex");
		PrintWriter pw = resp.getWriter();

		pw.println("<html>");
		pw.println("<head>");
		pw.println("<meta name=\"robots\" content=\"noindex\" />");
		pw.println("<title>Geoserver Proxy</title>");
		writeStyles(pw);
		pw.println("<style>");
		pw.println("td.c, th.c {");
		pw.println("   border: 1px solid black;");
		pw.println("}");
		pw.println("table.t {");
		pw.println("   border-collapse: collapse;");
		pw.println("}");
		pw.println("</style>");
		pw.println("</head>");
		pw.println("<body>");

		writeTitle(pw, "Geoserver Proxy");
		pw.println("<br>");
		pw.println("URL: <a href=\"" + Activator.simpleVmsList.url + "\">" + Activator.simpleVmsList.url + "</a><br>");
		pw.println("RetreiveDate: " + Activator.simpleVmsList.retreiveDate + "<br>");
		pw.println("JSON: <a href=\"all.json\">json file with all objects</a><br>");
		pw.println("JSON: <a href=\"speed.json\">json file with only speed objects</a><br>");
		pw.println("<p>");
		pw.println("Server statistics:");
		pw.println("<table class=\"t\">");
		for (int code : Activator.httpStats.keySet()) {
			pw.print("<tr>");
			pw.print("<td class=\"c\">" + code);
			pw.println("<td class=\"c\">" + Activator.httpStats.get(code));
		}
		pw.print("<tr>");
		pw.print("<td class=\"c\">JSON complete");
		pw.println("<td class=\"c\">" + cntJsonAll);
		pw.print("<tr>");
		pw.print("<td class=\"c\">JSON only for signs");
		pw.println("<td class=\"c\">" + cntJsonSpeed);
		pw.println("</table>");
		pw.println("<p>");

		pw.println("<table class=\"t\">");
		pw.println("<tr>");
		pw.print("<td class=\"c\">Location");
		pw.print("<td class=\"c\">SignId");
		pw.print("<td class=\"c\">VSM Index");
		pw.print("<td class=\"c\">IsBlank");
		pw.print("<td class=\"c\">HasSpeed");
		pw.print("<td class=\"c\">Speed Limit");
		pw.print("<td class=\"c\">Image size");
		pw.print("<td class=\"c\">Image");
		
		for (SimpleVmsSign sign : Activator.simpleVmsList.signList) {
			pw.println("<tr>");
			if (sign.latitude == 0.0 || sign.longitude == 0.0) {
				pw.print("<td class=\"c\">" + String.format(Locale.US, "%.6f", sign.latitude) + ", " + String.format(Locale.US, "%.6f", sign.longitude));
			} else {
				pw.print("<td class=\"c\"><a href= 'http://maps.google.com/maps?q=loc:" + sign.latitude + "," + sign.longitude + "' target='_blank'>");
				pw.print(String.format(Locale.US, "%.6f", sign.latitude) + ", " + String.format(Locale.US, "%.6f", sign.longitude));
				pw.println("</a>");
			}
			pw.print("<td class=\"c\">" + sign.signId);
			pw.print("<td class=\"c\">" + sign.vmsIndex);
			pw.print("<td class=\"c\">" + sign.isBlank);
			if (sign.isBlank) {
				pw.print("<td class=\"c\">&nbsp;");
				pw.print("<td class=\"c\">&nbsp;");
				pw.print("<td class=\"c\">&nbsp;");
				pw.print("<td class=\"c\">&nbsp;");
			} else {
				pw.print("<td class=\"c\">" + sign.hasSpeed);
				if (sign.hasSpeed) {
					pw.print("<td class=\"c\">" + sign.speedLimitValue);
				} else {
					pw.print("<td class=\"c\">&nbsp;");
				}
				if (sign.imageData != null) {
					pw.print("<td class=\"c\">" + sign.imageData.length());
					pw.print("<td class=\"c\"><img src=\"img?" + sign.signId + "\">");
				} else {
					pw.print("<td class=\"c\">&nbsp;");
					pw.print("<td class=\"c\">&nbsp;");
				}
			}
		}
		pw.println("</table>");
		
		pw.println("</body>");
		pw.println("</html>");
	}

	@Override
	public ServletGeoserverProxy addingService(ServiceReference<HttpService> reference) {
		System.out.println("GeoserverProxy.addingService: " + servletBaseDir);
		try {
			// Register Java Servlet
			HttpService srv = bundleContext.getService(reference);
			srv.registerServlet(servletBaseDir, this, null, null);
			System.out.println("GeoserverProxy.addingService: DONE");
			srv.registerResources(servletBaseDir + "/images", "/images", null);

			try {
				// Register Java Servlet
				srv.registerServlet(ServletFileNotFound.servletBaseDir, new ServletFileNotFound(bundleContext), null, null);
			} catch (NamespaceException ne) {
				// ignore
				System.out.println("GeoserverProxy.addingService: " + ServletFileNotFound.servletBaseDir + " already registered (no error)");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return this;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void modifiedService(ServiceReference<HttpService> reference, ServletGeoserverProxy service) {
		// Do nothing
	}

	@Override
	public void removedService(ServiceReference<HttpService> reference, ServletGeoserverProxy service) {
		// Unregister Java Servlet
		HttpService srv = bundleContext.getService(reference);
		srv.unregister(servletBaseDir);
	}
}
