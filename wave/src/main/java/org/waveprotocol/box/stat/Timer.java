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
package org.waveprotocol.box.stat;

import org.apache.wave.server.model.util.Preconditions;

/**
 * A timer for call.
 * Stores node in execution tree ant start time.
 *
 * @author akaplanov@gmail.com (A. Kaplanov)
 */
public class Timer {
  private final ExecutionNode node;
  private long startTime;
  private long stopTime = 0;

  public Timer(ExecutionNode node) {
    this.node = node;
  }

  public void start() {
    start(System.currentTimeMillis());
  }

  public void start(long startTime) {
    this.startTime = startTime;
  }

  public boolean isActive() {
    return stopTime == 0;
  }

  public void stop(ExecutionTree tree) {
    stop(tree, System.currentTimeMillis());
  }

  public void stop(ExecutionTree tree, long stopTime) {
    Preconditions.checkArgument(isActive(), "Timer is not active");
    this.stopTime = stopTime;
    int interval = (int)(stopTime - startTime);
    tree.pop(node);
    tree.record(node, interval);
  }
}
