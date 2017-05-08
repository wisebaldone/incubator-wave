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

package org.apache.wave.server.model.schema.account;

import org.apache.wave.server.model.account.IndexDecision;
import org.apache.wave.server.model.account.Role;
import org.apache.wave.server.model.document.operation.automaton.DocumentSchema;
import org.apache.wave.server.model.id.WaveletId;
import org.apache.wave.server.model.util.StringMap;
import org.apache.wave.server.model.account.DocumentBasedIndexability;
import org.apache.wave.server.model.id.IdConstants;
import org.apache.wave.server.model.id.IdUtil;
import org.apache.wave.server.model.schema.AbstractXmlSchemaConstraints;
import org.apache.wave.server.model.schema.SchemaProvider;
import org.apache.wave.server.model.util.CollectionUtils;
import org.apache.wave.server.model.util.Serializer;

/**
 * Hard-coded schema constraints for account models.
 *
 */
public final class AccountSchemas implements SchemaProvider {

  private final StringMap<DocumentSchema> schemas;

  public AccountSchemas() {
    schemas = CollectionUtils.createStringMap();
    schemas.put(IdConstants.ROLES_DATA_DOC_ID, ROLES_SCHEMA_CONSTRAINTS);
  }

  @Override
  public DocumentSchema getSchemaForId(WaveletId waveletId, String documentId) {
    if (IdUtil.isConversationalId(waveletId)) {
      if (schemas.containsKey(documentId)) {
        return schemas.get(documentId);
      }
    }
    return DocumentSchema.NO_SCHEMA_CONSTRAINTS;
  }

  /**
   * Enforces the schema on the "roles" data document.
   *
   * Example of a valid instance (there is no root tag):
   *
   * <pre>&lt;assign address=&quot;someaddress@example.com&quot; role=&quot;READ_ONLY&quot;/&gt;</pre>
   */
  public static final DocumentSchema ROLES_SCHEMA_CONSTRAINTS = new AbstractXmlSchemaConstraints() {
    {
      addChildren(null, "assign");
      addAttrs("assign", "address");

      String[] roles = new String[Role.values().length];
      for (int i = 0; i < roles.length; i++) {
        roles[i] = Role.values()[i].name();
      }
      addAttrWithValues("assign", "role", roles);
    }
  };

  /**
   * Enforces the schema on the "indexability" data document.
   *
   * Example of a valid instance (there is no root tag):
   *
   * <pre>&lt;index address=&quot;@example.com&quot; i=&quot;YES&quot;/&gt;</pre>
   */
  public static final DocumentSchema INDEXABILITY_SCHEMA_CONSTRAINTS =
      new AbstractXmlSchemaConstraints() {
        {
          addChildren(null, DocumentBasedIndexability.INDEX_TAG);
          addAttrs(DocumentBasedIndexability.INDEX_TAG, DocumentBasedIndexability.ADDRESS_ATTR);

          String[] values = new String[IndexDecision.values().length];
          Serializer<IndexDecision> serializer =
              new Serializer.EnumSerializer<IndexDecision>(IndexDecision.class);
          for (int i = 0; i < values.length; i++) {
            values[i] = serializer.toString(IndexDecision.values()[i]);
          }
          addAttrWithValues(
              DocumentBasedIndexability.INDEX_TAG, DocumentBasedIndexability.VALUE_ATTR, values);
        }
      };
}
