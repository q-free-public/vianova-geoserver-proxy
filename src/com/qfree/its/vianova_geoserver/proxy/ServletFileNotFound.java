package com.qfree.its.vianova_geoserver.proxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ServletFileNotFound extends HttpServlet implements ServiceTrackerCustomizer<HttpService, ServletFileNotFound> {

	private static final long serialVersionUID = 1L;
	BundleContext bundleContext;
	final static String servletBaseDir = "/";
	
	public ServletFileNotFound(BundleContext context) {
		this.bundleContext = context;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURL().toString();
		System.out.println("GeoserverProxy.FileNotFound.doGet " + url);

		if (!resp.isCommitted())
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURL().toString();
		System.out.println("GeoserverProxy.FileNotFound.doPost " + url);

		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	public ServletFileNotFound addingService(ServiceReference<HttpService> reference) {
		try {
			// Register Java Servlet
			HttpService srv = bundleContext.getService(reference);
			srv.registerServlet(servletBaseDir, this, null, null);
			return this;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void modifiedService(ServiceReference<HttpService> reference, ServletFileNotFound service) {
		// Do nothing
	}

	@Override
	public void removedService(ServiceReference<HttpService> reference, ServletFileNotFound service) {
		// Unregister Java Servlet
		HttpService srv = bundleContext.getService(reference);
		srv.unregister(servletBaseDir);
	}
}
