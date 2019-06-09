/*
 * @file DemHandler.java
 *
 * Copyright 2019 eduvax. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.dem.www;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Locale;

import net.eduvax.dem.Session;
import net.eduvax.dem.DiveSheet;
import net.eduvax.dem.Diver;
import net.eduvax.dem.Dive;

/**
 *
 */
public class DemHandler extends AbstractHandler {
    public DemHandler(Session session) {
        _session=session;
        _sessionJson=sessionToJson(session);
    }
    public void handle(String target,
                    Request baseRequest,
                    HttpServletRequest request,
                    HttpServletResponse response) 
                    throws IOException, ServletException {
        if ("/sessionInfo".equals(target)) {
            response.setContentType("text/json; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out=response.getWriter();
            out.println(_sessionJson);
            baseRequest.setHandled(true);
        }
    }
    public String sessionToJson(Session s) {
        String res="{\"round\":"+s.getRound()
            +",\"sheets\":[";
        Enumeration<DiveSheet> sheets=s.elements();
        while (sheets.hasMoreElements()) {
            res+=diveSheetToJson(sheets.nextElement());
            if (sheets.hasMoreElements()) {
                res+=",";
            }
        }
        res+="]}";
        return res;
    }
    public String diveSheetToJson(DiveSheet sheet) {
        String res="{\"diver\":\""+sheet.getDiver()
            +"\",\"score\":"+Session.str2digit(sheet.getScore(),Locale.US)
            +",\"dives\":[";
        Enumeration<Dive> dives=sheet.elements();
        while (dives.hasMoreElements()) {
            res+=diveToJson(dives.nextElement());
            if (dives.hasMoreElements()) {
                res+=",";
            }
        }
        res+="]}";
        return res;
    }
    public String diveToJson(Dive d) {
        String res="{\"dive\":\""+d.getName()
            +"\",\"dd\":"+d.getDD()
            +",\"sum\":"+Session.str2digit(d.getSum(),Locale.US)
            +",\"total\":"+Session.str2digit(d.getTotal(),Locale.US)
            +",\"current\":"+(d==_session.getCurrentDive()?"true":"false")
            +",\"score\":[";
        double[] score=d.getScore();
        for (int i=0;i<score.length;i++) {
            res+=Session.str2digit(score[i],Locale.US);
            if (i<score.length-1) {
                res+=",";
            }
        }
        res+="]}";
        return res;
    }
    private Session _session; 
    private String _sessionJson;
}
