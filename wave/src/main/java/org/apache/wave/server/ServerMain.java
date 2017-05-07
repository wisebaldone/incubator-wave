/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.wave.server;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.io.File;
import org.apache.commons.configuration2.Configuration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wave.server.endpoints.*;


/**
 * Apache Wave Server entry point.
 *
 * @author wisebaldone@apache.org (Evan Hughes)
 */
public class ServerMain {

  private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

  public static void main(String... args) {


    run(coreSettings);
  }

  public static void run(Module coreSettings) throws Exception {
    Injector injector = Guice.createInjector(coreSettings);
    Config config = injector.getInstance(Config.class);

    Server server = new Server( 9898 );
    ServletHandler handler = new ServletHandler();
    server.setHandler(handler);


    initEndpoints(handler);

    logger.info("Starting server");
    server.start();
    server.join();
  }

  private static void initEndpoints(ServletHandler handler) {
    handler.addServletWithMapping(AvatarsServlet.class, "/api/v1/profile/avatar");
  }

}
