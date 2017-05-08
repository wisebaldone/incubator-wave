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

package org.waveprotocol.wave.client.wavepanel.block.pojo;


import org.waveprotocol.wave.client.wavepanel.block.BlockStructureTestBase;
import org.waveprotocol.wave.client.wavepanel.view.ModelIdMapperImpl;
import org.waveprotocol.wave.client.wavepanel.view.ViewIdMapper;
import org.apache.wave.server.model.conversation.ConversationView;

/**
 * Extends an {@link BlockStructureTestBase}, and runs it against a
 * {@link PojoStructure}.
 *
 */

public final class PojoStructureTest extends BlockStructureTestBase {

  @Override
  protected PojoStructure create(ConversationView model) {
    return PojoRenderer.render(new ViewIdMapper(ModelIdMapperImpl.create(model, "empty")), model);
  }
}
