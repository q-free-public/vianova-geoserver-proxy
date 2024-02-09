package com.qfree.its.vianova_geoserver.proxy;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;

public abstract class ServletQfree extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public ServletQfree() {
		super();
	}

	static public void writeTitle(PrintWriter pw, String title) {
		pw.println("<table class=\"headermain\">");
		pw.println("<tbody><tr>");
		pw.println("<td class=\"logo-ts\">");
		pw.println("&nbsp;");
		pw.println("</td>");
		pw.println("<td class=\"logo\"><img src=\"images/q-free-logo-120x120.png\"></td>");
		pw.println("<td class=\"topic\">" + title + "</td>");
		pw.println("</tr>");
		pw.println("</tbody></table>");
	}

	static public void writeStyles(PrintWriter pw) {
		pw.println("<style>");
		pw.println("  CAPTION.MYTABLE");
		pw.println("  {");
		pw.println("     border-style:solid;");
		pw.println("     border-width:2px;");
		pw.println("     border-color:black;");
		pw.println("  }");

		pw.println("  TABLE.MYTABLE");
		pw.println("  { ");
		pw.println("     font-family:arial;");
		pw.println("     font-size:10pt;");
		pw.println("     border-style:solid;");
		pw.println("     border-color:black;");
		pw.println("     border-width:1px;");
		pw.println("  }");

		pw.println("  TH.MYTABLE");
		pw.println("  {");
		pw.println("     font-size:11pt;");
		pw.println("     color:white;");
		pw.println("  }");

		pw.println("  TD.MYTABLE");
		pw.println("  {  ");
		pw.println("     font-size:10pt;");
		pw.println("     border-style:solid;");
		pw.println("     border-width:1px;");
		pw.println("     text-align:center;");
		pw.println("  }");

		pw.println("table.headermain {");
		pw.println("  margin-top:7px;");
		pw.println("  border-collapse:collapse;");
		pw.println("  width:95%;");
		pw.println("}");

		pw.println("td.logo {");
		pw.println("  background-color:#e73424;");
		pw.println("  border-bottom:10px solid #e73424;");
		pw.println("  width:78px;");
		pw.println("}");

		pw.println("td.logo-ts {");
		pw.println("  padding-top:7px;");
		pw.println("  width:5%;");
		pw.println("  text-align:center;");
		pw.println("  vertical-align:middle;");
		pw.println("}");

		pw.println("td.topic {");
		pw.println("  text-align:center;");
		pw.println("  font-size:40px;");
		pw.println("  font-weight:bold;");
		pw.println("  font-family:Arial;");
		pw.println("  border-bottom:10px solid #e73424;");
		pw.println("}");

		pw.println("</style>");
	}
}
