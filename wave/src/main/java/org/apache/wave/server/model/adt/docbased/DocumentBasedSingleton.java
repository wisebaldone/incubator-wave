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

package org.apache.wave.server.model.adt.docbased;

import org.apache.wave.server.model.document.Doc.E;
import org.apache.wave.server.model.document.Doc.N;
import org.apache.wave.server.model.document.ObservableDocument;
import org.apache.wave.server.model.document.util.DocHelper;
import org.apache.wave.server.model.util.CopyOnWriteSet;
import org.apache.wave.server.model.util.ValueUtils;
import org.apache.wave.server.model.adt.ObservableSingleton;
import org.apache.wave.server.model.document.util.DocEventRouter;
import org.apache.wave.server.model.document.util.Point;
import org.apache.wave.server.model.util.ElementListener;

import java.util.Map;

/**
 * Document-based implementation of a singleton.
 *
 * @author anorth@google.com (Alex North)
 * @param <V> type of the value
 * @param <I> type of a value initializer
 */
public final class DocumentBasedSingleton<V, I> implements ObservableSingleton<V, I>,
    ElementListener<E> {
  /**
   * Creates a singleton.
   *
   * @param router event router for the document holding the object
   * @param container container in which the singleton is stored
   * @param valueTagName tag name of a value instance
   */
  public static <V, I> DocumentBasedSingleton<V, I> create(DocEventRouter router,
      E container, String valueTagName, Factory<E, V, I> valueFactory) {
    DocumentBasedSingleton<V, I> singleton =
      new DocumentBasedSingleton<V, I>(router, container, valueTagName, valueFactory);
    router.addChildListener(container, singleton);
    return singleton;
  }

  private final DocEventRouter router;
  private final E container;
  private final String valueTagName;
  private final Factory<E, V, I> valueFactory;
  private final CopyOnWriteSet<Listener<? super V>> listeners = CopyOnWriteSet.create();

  /** The canonical value-providing document element. */
  private E currentElement;
  /** The abstract value, initialized lazily. */
  private V currentValue = null;

  private DocumentBasedSingleton(DocEventRouter router, E container,
      String valueTagName, Factory<E, V, I> valueFactory) {
    this.router = router;
    this.container = container;
    this.valueTagName = valueTagName;
    this.valueFactory = valueFactory;
    this.currentElement = findCanonicalElement();
  }

  @Override
  public boolean hasValue() {
    return (currentElement != null);
  }

  @Override
  public V get() {
    maybeInitCurrentValue();
    return currentValue;
  }

  @Override
  public V set(I initialState) {
    final Map<String, String> attributes =
      Initializer.Helper.buildAttributes(initialState, valueFactory);
    // Insert a new first-child element of the container.
    Point<N> insertionPoint = Point.inElement(container,
        getDocument().getFirstChild(container));
    E element = getDocument().createElement(insertionPoint, valueTagName, attributes);
    // onElementAdded will create the value object.
    assert currentElement != null;
    assert currentValue != null;

    cleanup();
    return currentValue;
  }

  @Override
  public void clear() {
    // Delete non-canonical elements first so no events are generated.
    cleanup();
    // Then delete the canonical, which will remove currentValue.
    E element = findCanonicalElement();
    if (element != null) {
      getDocument().deleteNode(element);
    }
    assert currentElement == null;
    assert currentValue == null;
  }

  @Override
  public void addListener(Listener<? super V> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(Listener<? super V> listener) {
    listeners.remove(listener);
  }

  @Override
  public void onElementAdded(E newElement) {
    // When a deletion and insertion are composed it's possible that
    // recalculation from the deletion event changes the current element
    // to the newly inserted canonical element before this event
    // is fired. Thus the check that the new element is not already
    // the current element.
    // Yet another failure of the ElementListener interface.
    if (newElement == findCanonicalElement() && newElement != currentElement) {
      changeCurrentValue(newElement);
    }
  }

  @Override
  public void onElementRemoved(E removedElement) {
    if (removedElement == currentElement) {
      changeCurrentValue(findCanonicalElement());
    }
  }

  /**
   * Gets the document backing this singleton (for testing).
   */
  DocEventRouter getEventRouter() {
    return router;
  }

  ObservableDocument getDocument() {
    return router.getDocument();
  }

  /**
   * Finds the canonical value-providing document element, which is the first
   * element with the expected tag name.
   *
   * @return the canonical element, or null
   */
  private E findCanonicalElement() {
    // NOTE(anorth): this is correct for planned transform semantics where
    // colliding insertions result in document order matching temporal order.
    // Current (June 2010) transformation results in the opposite ordering.
    return DocHelper.getElementWithTagName(getDocument(), valueTagName, container);
  }

  /**
   * Initializes the current value without firing events.
   */
  private void maybeInitCurrentValue() {
    if (currentValue == null) {
      currentValue = (currentElement != null)
          ? valueFactory.adapt(getEventRouter(), currentElement)
          : null;
    }
  }

  /**
   * Sets the current value to be provided by a canonical element and fires
   * events.
   *
   * @param newCurrentElement new value-providing element, or null;
   */
  private void changeCurrentValue(E newCurrentElement) {
    assert currentElement != newCurrentElement;
    maybeInitCurrentValue();
    V oldValue = currentValue;
    currentElement = newCurrentElement;
    currentValue = null;
    maybeInitCurrentValue();
    maybeTriggerOnValueChanged(oldValue, currentValue);
  }

  /**
   * Deletes all non-canonical elements from the document.
   */
  private void cleanup() {
    E canonical = findCanonicalElement();
    E toDelete = DocHelper.getLastElementWithTagName(getDocument(), valueTagName, container);
    while (toDelete != canonical) {
      getDocument().deleteNode(toDelete);
      toDelete = DocHelper.getLastElementWithTagName(getDocument(), valueTagName, container);
    }
  }

  private void maybeTriggerOnValueChanged(V oldValue, V newValue) {
    if (ValueUtils.notEqual(oldValue, newValue)) {
      for (Listener<? super V> l : listeners) {
        l.onValueChanged(oldValue, newValue);
      }
    }
  }
}
