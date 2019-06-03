/*
 * @file WebServer.java
 *
 * Copyright 2019 eduvax. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.dem.www;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import java.io.File;
import net.eduvax.dem.Session;

/**
 *
 */
public class WebServer {
    public WebServer(int port,Session session) throws Exception {
        _server=new Server(port);
        DemHandler dh=new DemHandler(session);
        ResourceHandler fileServer = new ResourceHandler();
        fileServer.setDirectoriesListed(true);
        fileServer.setWelcomeFiles(new String[]{ "index.html" });
        File f=new File(WebServer.class.getProtectionDomain()
                            .getCodeSource().getLocation()
                            .toURI());
        f=f.getParentFile();
        f=f.getParentFile();
        fileServer.setResourceBase(f.getCanonicalPath()+"/www");
        HandlerList handlers=new HandlerList();
        handlers.setHandlers(new Handler[] {
            dh, fileServer, new DefaultHandler()
        });
        _server.setHandler(handlers);
    }
    public void start() throws Exception {
        _server.start();
    }
    public void stop() throws Exception {
        _server.stop();
    }
    public void join() throws Exception {
        _server.join();
    }
    private Server _server;
}
