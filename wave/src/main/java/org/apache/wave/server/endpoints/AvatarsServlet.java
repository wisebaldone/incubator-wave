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

package org.apache.wave.server.endpoints;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A servlet for fetching the users Initials Avatar.
 *
 * @author wisebaldone@apache.org (Evan Hughes)
 */
@Singleton
public final class AvatarsServlet extends HttpServlet {
  private static final Logger logger = LoggerFactory.getLogger(AvatarsServlet.class);
  private BufferedImage DEFAULT;

  @Inject
  public AvatarsServlet() throws IOException {
    try {
      DEFAULT = ImageIO.read(Resources.getResource("static/images/avatar/unknown.jpg"));
    } catch (Exception e) {
      logger.warn("Default Avatar image could not be loaded from disc. " + e.toString());
      DEFAULT = ImageIO.read(Resources.getResource(
          "org/apache/wave/server/rpc/avatar/unknown.jpg"));
    }
  }

  /**
   * Create an http response to the fetch query. Main entrypoint for this class.
   */
  @Override
  @VisibleForTesting
  protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
    response.setContentType("image/jpg");
    ImageIO.write(DEFAULT, "JPG", response.getOutputStream());
  }
}
