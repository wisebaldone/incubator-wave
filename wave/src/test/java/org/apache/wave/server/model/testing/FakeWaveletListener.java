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

package org.apache.wave.server.model.testing;

import org.apache.wave.server.model.wave.ObservableWavelet;
import org.apache.wave.server.model.wave.ParticipantId;
import org.apache.wave.server.model.wave.WaveletListener;
import org.apache.wave.server.model.wave.opbased.WaveletListenerImpl;

/**
 * Stub implementation of {@link WaveletListener}.  Each notification method
 * saves the passed parameters for later inspection by accessors.
 *
 * @author zdwang@google.com (David Wang)
 */
public class FakeWaveletListener extends WaveletListenerImpl {
  /** The last participant received from 
   * {@link #onParticipantAdded(ObservableWavelet, ParticipantId)}
   */
  private ParticipantId participant;

  @Override
  public void onParticipantAdded(ObservableWavelet wavelet, ParticipantId participant) {
    this.participant = participant;
  }

  /**
   * @return the last {@code participant} received by 
   * {@link #onParticipantAdded(ObservableWavelet, ParticipantId)}.
   */
  public ParticipantId getParticipant() {
    return participant;
  }


  /** Resets all fields. */
  public void reset() {
    this.participant = null;
  }
}
