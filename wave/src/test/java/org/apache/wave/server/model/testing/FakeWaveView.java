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

import org.apache.wave.server.model.id.IdGenerator;
import org.apache.wave.server.model.id.WaveletId;
import org.apache.wave.server.model.operation.SilentOperationSink;
import org.apache.wave.server.model.operation.wave.WaveletOperation;
import org.apache.wave.server.model.wave.ObservableWavelet;
import org.apache.wave.server.model.wave.ParticipantId;
import org.apache.wave.server.model.wave.WaveViewListener;
import org.apache.wave.server.model.wave.data.DocumentFactory;
import org.apache.wave.server.model.wave.data.impl.WaveletDataImpl;
import org.apache.wave.server.model.wave.opbased.ObservableWaveView;
import org.apache.wave.server.model.wave.opbased.OpBasedWavelet;
import org.apache.wave.server.model.wave.opbased.WaveViewImpl;
import org.apache.wave.server.model.wave.opbased.WaveViewImpl.WaveletConfigurator;
import org.apache.wave.server.model.id.WaveId;
import org.apache.wave.server.model.operation.wave.WaveletOperationContext;
import org.apache.wave.server.model.schema.SchemaProvider;

/**
 * Dummy implementation of a wave view.
 *
 */
public final class FakeWaveView implements ObservableWaveView, Factory<OpBasedWavelet> {

  public final static class Builder {
    private final SchemaProvider schemas;
    private IdGenerator idGenerator;
    private WaveId waveId;
    private ParticipantId viewer;
    private SilentOperationSink<? super WaveletOperation> sink;
    private WaveletConfigurator configurator;
    private DocumentFactory<?> docFactory;

    private Builder(SchemaProvider schemas) {
      this.schemas = schemas;
    }

    public Builder with(DocumentFactory<?> docFactory) {
      this.docFactory = docFactory;
      return this;
    }

    public Builder with(IdGenerator idGenerator) {
      this.idGenerator = idGenerator;
      return this;
    }

    public Builder with(WaveId wid) {
      this.waveId = wid;
      return this;
    }

    public Builder with(ParticipantId viewer) {
      this.viewer = viewer;
      return this;
    }

    public Builder with(SilentOperationSink<? super WaveletOperation> sink) {
      this.sink = sink;
      return this;
    }

    public Builder with(WaveletConfigurator configurator) {
      this.configurator = configurator;
      return this;
    }

    public FakeWaveView build() {
      if (idGenerator == null) {
        idGenerator = FakeIdGenerator.create();
      }
      if (waveId == null) {
        waveId = idGenerator.newWaveId();
      }
      if (viewer == null) {
        viewer = FAKE_PARTICIPANT;
      }
      if (sink == null) {
        sink = SilentOperationSink.VOID;
      }
      if (configurator == null) {
        configurator = WaveViewImpl.WaveletConfigurator.ADD_CREATOR;
      }
      if (docFactory == null) {
        // Document factory that accepts output-sink registrations.
        docFactory = FakeDocument.Factory.create(schemas);
      }

      // Wavelet factory that does all the work.
      OpBasedWaveletFactory waveletFactory = OpBasedWaveletFactory // \u2620
          .builder(schemas) // \u2620
          .with(WaveletDataImpl.Factory.create(docFactory)) // \u2620
          .with(sink) // \u2620
          .with(viewer) // \u2620
          .build();

      // And the view implementation using that factory.
      WaveViewImpl<OpBasedWavelet> view =
          WaveViewImpl.create(waveletFactory, waveId, idGenerator, viewer, configurator);

      return new FakeWaveView(waveletFactory, view);
    }
  }

  private static final ParticipantId FAKE_PARTICIPANT = new ParticipantId("fake@example.com");

  private final OpBasedWaveletFactory factory;
  private final WaveViewImpl<? extends OpBasedWavelet> view;

  /**
   * Creates a wave view.
   *
   * @param factory  factory exposing testing hacks
   * @param view     real view implementation
   */
  private FakeWaveView(OpBasedWaveletFactory factory, WaveViewImpl<? extends OpBasedWavelet> view) {
    this.factory = factory;
    this.view = view;
  }

  /**
   * @return a builder for a fake wave view.
   */
  public static Builder builder(SchemaProvider schemas) {
    return new Builder(schemas);
  }

  //
  // Expose as basic wavelet factory for wavelet-specific tests.
  //

  @Override
  public OpBasedWavelet create() {
    return createWavelet();
  }

  //
  // Testing hacks.
  //

  public MockParticipationHelper getLastAuthoriser() {
    return factory.getLastAuthoriser();
  }

  public WaveletOperationContext.Factory getLastContextFactory() {
    return factory.getLastContextFactory();
  }

  public OpBasedWavelet createWavelet(WaveletId id) {
    return view.createWavelet(id);
  }

  public void removeWavelet(ObservableWavelet wavelet) {
    view.removeWavelet(wavelet);
  }

  //
  // Delegate view implementation to view.
  //

  @Override
  public OpBasedWavelet createRoot() {
    return view.createRoot();
  }

  @Override
  public OpBasedWavelet createUserData() {
    return view.createUserData();
  }

  @Override
  public OpBasedWavelet createWavelet() {
    return view.createWavelet();
  }

  @Override
  public OpBasedWavelet getRoot() {
    return view.getRoot();
  }

  @Override
  public OpBasedWavelet getUserData() {
    return view.getUserData();
  }

  @Override
  public OpBasedWavelet getWavelet(WaveletId waveletId) {
    return view.getWavelet(waveletId);
  }

  @Override
  public Iterable<? extends OpBasedWavelet> getWavelets() {
    return view.getWavelets();
  }

  @Override
  public WaveId getWaveId() {
    return view.getWaveId();
  }

  @Override
  public void addListener(WaveViewListener listener) {
    view.addListener(listener);
  }

  @Override
  public void removeListener(WaveViewListener listener) {
    view.removeListener(listener);
  }
}
