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
package org.apache.wave.api.v1.account;

import com.google.inject.Inject;
import com.google.common.base.Preconditions;

import org.waveprotocol.box.server.authentication.SessionManager;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A servlet for logging out the user using a Post request.
 *
 * @author wisebaldone@apache.org (Evan Hughes)
 */
@SuppressWarnings("serial")
@Singleton
public class LogoutServlet extends HttpServlet {
    private final SessionManager sessionManager;

    /**
     * @param sessionManager singleton for server instance.
     * @requires sessionManager != Null
     */
    @Inject
    public LogoutServlet(SessionManager sessionManager) {
        Preconditions.checkNotNull(sessionManager, "Session manager is null");
        this.sessionManager = sessionManager;
    }

    /**
     * On Post, logs the user out if a session exists.
     * @ensures Gives 404 if no session was found, otherwise Logs the user out and returns a 200.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            sessionManager.logout(session);
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
