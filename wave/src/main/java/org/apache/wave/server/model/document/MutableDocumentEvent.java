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

package org.apache.wave.server.model.document;

import org.apache.wave.server.model.document.Doc.E;
import org.apache.wave.server.model.document.Doc.N;
import org.apache.wave.server.model.document.Doc.T;
import org.apache.wave.server.model.document.indexed.DocumentHandler.EventBundle;

/**
 * Data object that wraps a mutableDocument and its events in order to allow
 * users to match the same generic types across both objects.
 *
 */
public class MutableDocumentEvent {

  private final Document document;
  private final EventBundle<N, E, T> events;

  public MutableDocumentEvent(Document document,
      EventBundle<N, E, T> events) {
    this.document = document;
    this.events = events;
  }

  public Document getDocument() {
    return document;
  }

  public EventBundle<N, E, T> getEvents() {
    return events;
  }
}
