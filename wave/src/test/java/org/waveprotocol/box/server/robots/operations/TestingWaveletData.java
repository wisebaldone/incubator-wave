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

package org.waveprotocol.box.server.robots.operations;

import com.google.common.collect.ImmutableList;

import org.waveprotocol.box.server.util.WaveletDataUtil;
import org.apache.wave.server.model.conversation.Conversation;
import org.apache.wave.server.model.conversation.ConversationBlip;
import org.apache.wave.server.model.conversation.ConversationView;
import org.apache.wave.server.model.conversation.TitleHelper;
import org.apache.wave.server.model.conversation.WaveBasedConversationView;
import org.apache.wave.server.model.conversation.WaveletBasedConversation;
import org.apache.wave.server.model.document.util.LineContainers;
import org.apache.wave.server.model.document.util.XmlStringBuilder;
import org.apache.wave.server.model.id.WaveId;
import org.apache.wave.server.model.id.WaveletId;
import org.apache.wave.server.model.operation.SilentOperationSink;
import org.apache.wave.server.model.operation.wave.BasicWaveletOperationContextFactory;
import org.apache.wave.server.model.operation.wave.WaveletOperation;
import org.apache.wave.server.model.testing.BasicFactories;
import org.apache.wave.server.model.testing.FakeIdGenerator;
import org.apache.wave.server.model.version.HashedVersion;
import org.apache.wave.server.model.wave.ParticipantId;
import org.apache.wave.server.model.wave.ParticipationHelper;
import org.apache.wave.server.model.wave.ReadOnlyWaveView;
import org.apache.wave.server.model.wave.data.ObservableWaveletData;
import org.apache.wave.server.model.wave.data.WaveViewData;
import org.apache.wave.server.model.wave.data.WaveletData;
import org.apache.wave.server.model.wave.data.impl.WaveViewDataImpl;
import org.apache.wave.server.model.wave.data.impl.WaveletDataImpl;
import org.apache.wave.server.model.wave.opbased.OpBasedWavelet;

import java.util.List;

/**
 * Builds a wavelet and provides direct access to the various layers of
 * abstraction.
 * 
 * @author josephg@gmail.com (Joseph Gentle)
 */
public class TestingWaveletData {
  private final ObservableWaveletData waveletData;
  private final ObservableWaveletData userWaveletData;
  private final Conversation conversation;
  private final WaveViewData waveViewData;

  public TestingWaveletData(
      WaveId waveId, WaveletId waveletId, ParticipantId author, boolean isConversational) {
    waveletData =
        new WaveletDataImpl(waveletId, author, 1234567890, 0, HashedVersion.unsigned(0), 0,
            waveId, BasicFactories.observablePluggableMutableDocumentFactory());
    userWaveletData =
        new WaveletDataImpl(WaveletId.of("example.com", "user+foo@example.com"), author,
            1234567890, 0, HashedVersion.unsigned(0), 0,
          waveId, BasicFactories.observablePluggableMutableDocumentFactory());
    
    OpBasedWavelet wavelet =
      new OpBasedWavelet(waveId, waveletData, new BasicWaveletOperationContextFactory(author),
          ParticipationHelper.DEFAULT,
          SilentOperationSink.Executor.<WaveletOperation, WaveletData>build(waveletData),
          SilentOperationSink.VOID);
    ReadOnlyWaveView waveView = new ReadOnlyWaveView(waveId);
    waveView.addWavelet(wavelet);
    
    if (isConversational) {
      ConversationView conversationView = WaveBasedConversationView.create(waveView, FakeIdGenerator.create());
      WaveletBasedConversation.makeWaveletConversational(wavelet);
      conversation = conversationView.getRoot();

      conversation.addParticipant(author);
    } else {
      conversation = null;
    }

    waveViewData = WaveViewDataImpl.create(waveId, ImmutableList.of(waveletData, userWaveletData));
  }

  public void appendBlipWithText(String text) {
    ConversationBlip blip = conversation.getRootThread().appendBlip();
    LineContainers.appendToLastLine(blip.getContent(), XmlStringBuilder.createText(text));
    TitleHelper.maybeFindAndSetImplicitTitle(blip.getContent());
  }

  public List<ObservableWaveletData> copyWaveletData() {
    // This data object already has an op-based owner on top. Must copy it.
    return ImmutableList.of(WaveletDataUtil.copyWavelet(waveletData),
        WaveletDataUtil.copyWavelet(userWaveletData));
  }

  public WaveViewData copyViewData() {
    return WaveViewDataImpl.create(waveViewData.getWaveId(),copyWaveletData());
  }
}
