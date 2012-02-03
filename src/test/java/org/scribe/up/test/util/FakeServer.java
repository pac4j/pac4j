/*
  Copyright 2012 Jérôme Leleu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.scribe.up.test.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * This class is an embedded server whose always returns HTTP 200 responses to handle callback calls.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class FakeServer {
    
    private static Server server = null;
    
    private static boolean started = false;
    
    private static class Return200Handler extends AbstractHandler {
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
        }
    }
    
    public static void start() throws Exception {
        String port = PrivateData.get("server.port");
        if (!started && port != null && !"".equals(port.trim())) {
            server = new Server(Integer.parseInt(port));
            server.setHandler(new Return200Handler());
            server.start();
            started = true;
        }
    }
}
