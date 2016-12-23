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

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

/**
 * @author wisebaldone@apache.org (Evan Hughes)
 */
public class LoginServlet {

    public LoginServlet() {
        LoginProto.SearchRequest request = LoginProto.SearchRequest.newBuilder().setPageNumber(5).build();
        try {
            String string = JsonFormat.printer().print(request);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
