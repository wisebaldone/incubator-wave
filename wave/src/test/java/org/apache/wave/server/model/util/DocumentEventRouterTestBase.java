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

package org.apache.wave.server.model.util;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import junit.framework.TestCase;

import org.apache.wave.server.model.document.Doc.E;
import org.apache.wave.server.model.document.Doc.N;
import org.apache.wave.server.model.document.Doc.T;
import org.apache.wave.server.model.document.DocHandler;
import org.apache.wave.server.model.document.ObservableDocument;
import org.apache.wave.server.model.document.ObservableMutableDocument;
import org.apache.wave.server.model.document.indexed.DocumentHandler;
import org.apache.wave.server.model.document.operation.automaton.DocumentSchema;
import org.apache.wave.server.model.testing.BasicFactories;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.apache.wave.server.model.document.Doc;
import org.apache.wave.server.model.document.util.DocumentEventRouter;
import org.apache.wave.server.model.document.util.ListenerRegistration;
import org.apache.wave.server.model.document.util.Point;
import org.apache.wave.server.model.document.util.XmlStringBuilder;

import java.util.Collections;
import java.util.Map;

/**
 * Generic tests for event routers.
 *
 */
public abstract class DocumentEventRouterTestBase extends TestCase {

  /**
   * An empty document.
   */
  private ObservableDocument realDoc;

  /**
   * A mock spying on the document.
   */
  private ObservableDocument doc;

  /**
   * The list of listeners currently registered on the document.
   */
  private final CopyOnWriteSet<Object> docListeners = CopyOnWriteSet.create();

  private E elmOne;
  private E elmTwo;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.realDoc = createDocument();
    elmOne = realDoc.createChildElement(realDoc.getDocumentElement(), "a", noAttribs());
    elmTwo = realDoc.createChildElement(realDoc.getDocumentElement(), "b", noAttribs());
    this.doc = spy(realDoc);
    doAnswer(new Answer<Void>() {
      @SuppressWarnings("unchecked")
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        DocumentHandler<Doc.N, Doc.E, Doc.T> listener =
              (DocumentHandler<N, E, T>) invocation.getArguments()[0];
        docListeners.add(listener);
        realDoc.addListener(listener);
        return null;
      }
    }).when(doc).addListener(Mockito.<DocHandler>any());
    doAnswer(new Answer<Void>() {
      @SuppressWarnings("unchecked")
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        DocumentHandler<N, E, T> listener =
          (DocumentHandler<N, E, T>) invocation.getArguments()[0];
        docListeners.remove(invocation.getArguments()[0]);
        realDoc.removeListener(listener);
        return null;
      }
    }).when(doc).removeListener(Mockito.<DocHandler>any());
    // Mockito messes with how this works on the mock for some reason so we stub
    // it here to get the right behavior.
    doAnswer(new Answer<Point<N>>() {
      @Override
      public Point<N> answer(InvocationOnMock invocation) throws Throwable {
        return realDoc.locate((Integer) invocation.getArguments()[0]);
      }
    }).when(doc).locate(Mockito.anyInt());
  }

  protected abstract DocumentEventRouter<N, E, ?> createRouter(
      ObservableMutableDocument<Doc.N, Doc.E, Doc.T> doc);

  /**
   * Create an empty document.  Subclasses can override this if they need a
   * certain kind of document.
   */
  protected ObservableDocument createDocument() {
    return BasicFactories.createDocument(DocumentSchema.NO_SCHEMA_CONSTRAINTS);
  }

  /**
   * Test that just one listener is registered per router and only when the router
   * has active listeners.
   */
  @SuppressWarnings("unchecked")
  public void testAddedOnDemand() {
    assertEquals(0, docListeners.size());
    DocumentEventRouter<N, E, ?> router = createRouter(doc);
    assertEquals(0, docListeners.size());
    ListenerRegistration firstReg = router.addAttributeListener(new DummyElm(),
        mock(AttributeListener.class));
    assertEquals(1, docListeners.size());
    ListenerRegistration secondReg = router.addAttributeListener(new DummyElm(),
        mock(AttributeListener.class));
    assertEquals(1, docListeners.size());
    firstReg.detach();
    assertEquals(1, docListeners.size());
    secondReg.detach();
    assertEquals(0, docListeners.size());
  }

  /**
   * Test that removing a node removes all associated listeners and, if possible,
   * unregisters the router from the document.
   */
  @SuppressWarnings("unchecked")
  public void testRemoveListenersWhenDeleted() {
    assertEquals(0, docListeners.size());
    DocumentEventRouter<N, E, ?> router = createRouter(doc);
    assertEquals(0, docListeners.size());
    router.addAttributeListener(elmOne, mock(AttributeListener.class));
    router.addAttributeListener(elmOne, mock(AttributeListener.class));
    router.addDeletionListener(elmOne, mock(DeletionListener.class));
    router.addDeletionListener(elmOne, mock(DeletionListener.class));
    router.addChildListener(elmOne, mock(ElementListener.class));
    router.addChildListener(elmOne, mock(ElementListener.class));
    assertEquals(1, docListeners.size());
    realDoc.deleteNode(elmOne);
    assertEquals(0, docListeners.size());
  }

  /**
   * Test that removing yourself during event handling works as expected.
   */
  public void testSelfRemoval() {
    class SelfRemovalListener implements ElementListener<E> {
      private int removeCallCount = 0;
      private ListenerRegistration reg;
      @Override
      public void onElementAdded(E element) {
        fail();
      }
      @Override
      public void onElementRemoved(E element) {
        removeCallCount++;
        if (removeCallCount == 1) {
          reg.detach();
        }
      }
    }
    E parent = elmOne;
    E childOne = realDoc.createChildElement(parent, "a", noAttribs());
    E childTwo = realDoc.createChildElement(parent, "b", noAttribs());
    DocumentEventRouter<N, E, ?> router = createRouter(doc);
    SelfRemovalListener util = new SelfRemovalListener();
    util.reg = router.addChildListener(parent, util);
    assertEquals(0, util.removeCallCount);
    realDoc.deleteNode(childTwo);
    assertEquals(1, util.removeCallCount);
    realDoc.deleteNode(childOne);
    assertEquals(1, util.removeCallCount);
  }

  /**
   * Test that attribute and deletion listeners only receive events for the elements
   * they're listening to.
   */
  @SuppressWarnings("unchecked")
  public void testFiltering() {
    DocumentEventRouter<N, E, ?> router = createRouter(doc);
    AttributeListener<E> attribListenerOne = mock(AttributeListener.class);
    router.addAttributeListener(elmOne, attribListenerOne);
    AttributeListener<E> attribListenerTwo = mock(AttributeListener.class);
    router.addAttributeListener(elmTwo, attribListenerTwo);
    realDoc.setElementAttribute(elmOne, "t", "1");
    verify(attribListenerOne).onAttributesChanged(same(elmOne), anyAttribs(), anyAttribs());
    verify(attribListenerTwo, never()).onAttributesChanged(anyElement(), anyAttribs(), anyAttribs());
    realDoc.setElementAttribute(elmTwo, "s", "2");
    Mockito.verify(attribListenerOne).onAttributesChanged(same(elmOne), anyAttribs(), anyAttribs());
    Mockito.verify(attribListenerTwo).onAttributesChanged(same(elmTwo), anyAttribs(), anyAttribs());
    DeletionListener deleteListenerOne = mock(DeletionListener.class);
    router.addDeletionListener(elmOne, deleteListenerOne);
    DeletionListener deleteListenerTwo = mock(DeletionListener.class);
    router.addDeletionListener(elmTwo, deleteListenerTwo);
    realDoc.deleteNode(elmTwo);
    verify(deleteListenerOne, never()).onDeleted();
    verify(deleteListenerTwo).onDeleted();
    realDoc.deleteNode(elmOne);
    verify(deleteListenerOne).onDeleted();
    verify(deleteListenerTwo).onDeleted();
    verify(attribListenerOne).onAttributesChanged(same(elmOne), anyAttribs(), anyAttribs());
    verify(attribListenerTwo).onAttributesChanged(same(elmTwo), anyAttribs(), anyAttribs());
    verifyNoMoreInteractions(deleteListenerOne);
    verifyNoMoreInteractions(deleteListenerTwo);
    verifyNoMoreInteractions(attribListenerOne);
    verifyNoMoreInteractions(attribListenerTwo);
  }

  /**
   * Test that child listeners are called as appropriate.
   */
  @SuppressWarnings("unchecked")
  public void testChildListeners() {
    DocumentEventRouter<N, E, ?> router = createRouter(doc);
    ElementListener<E> elmListenerOne = mock(ElementListener.class);
    ElementListener<E> elmListenerTwo = mock(ElementListener.class);
    router.addChildListener(elmOne, elmListenerOne);
    router.addChildListener(elmTwo, elmListenerTwo);
    E childOne = realDoc.createChildElement(elmOne, "x", noAttribs());
    verify(elmListenerOne).onElementAdded(childOne);
    verify(elmListenerTwo, never()).onElementAdded(anyElement());
    E childTwo = realDoc.createChildElement(elmTwo, "y", noAttribs());
    verify(elmListenerOne).onElementAdded(childOne);
    verify(elmListenerTwo).onElementAdded(childTwo);
    realDoc.deleteNode(childTwo);
    verify(elmListenerOne, never()).onElementRemoved(anyElement());
    verify(elmListenerTwo).onElementRemoved(childTwo);
    realDoc.deleteNode(childOne);
    verify(elmListenerOne).onElementRemoved(childOne);
    verify(elmListenerTwo).onElementRemoved(childTwo);
    verifyNoMoreInteractions(elmListenerOne);
    verifyNoMoreInteractions(elmListenerTwo);
  }

  /**
   * Test that only the root of a deletion is given child notifications, not
   * children of parents that are themselves being deleted.
   */
  @SuppressWarnings("unchecked")
  public void testRecursiveDeletion() {
    E parent = elmOne;
    final E child = realDoc.createChildElement(parent, "c", noAttribs());
    E grandChild = realDoc.createChildElement(child, "d", noAttribs());
    DocumentEventRouter<N, E, ?> router = createRouter(doc);
    ElementListener<E> childRemovedListener = mock(ElementListener.class);
    router.addChildListener(parent, childRemovedListener);
    ElementListener<E> grandChildRemovedListener = mock(ElementListener.class);
    router.addChildListener(child, grandChildRemovedListener);
    realDoc.deleteNode(child);
    verify(childRemovedListener).onElementRemoved(same(child));
    verifyNoMoreInteractions(childRemovedListener);
    verifyZeroInteractions(grandChildRemovedListener);
  }

  /**
   * Test that only the root of an insertion is given child notifications.
   */
  @SuppressWarnings("unchecked") // Generic mocks.
  public void testRecursiveInsertion() {
    final E parent = elmOne;
    final DocumentEventRouter<N, E, ?> router = createRouter(doc);
    final ElementListener<E> childListener = mock(ElementListener.class);
    ElementListener<E> parentListener = mock(ElementListener.class);
    final ElementListener<E> secondParentListener = mock(ElementListener.class);
    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) throws Throwable {
        E child = (E) invocation.getArguments()[0];
        router.addChildListener(child, childListener);
        router.addChildListener(parent, secondParentListener);
        return null;
      }
    }).when(parentListener).onElementAdded(anyElement());
    router.addChildListener(parent, parentListener);
    E child = doc.insertXml(Point.start(doc, parent),
        XmlStringBuilder.createEmpty().wrap("a").wrap("b"));
    verify(parentListener).onElementAdded(child);
    verifyNoMoreInteractions(parentListener);
    verifyZeroInteractions(childListener);
    verifyZeroInteractions(secondParentListener);
  }

  private static E anyElement() {
    return Matchers.<E>any();
  }

  private static Map<String, String> anyAttribs() {
    return Matchers.<Map<String, String>>any();
  }

  private static Map<String, String> noAttribs() {
    return Collections.<String, String>emptyMap();
  }

  private static class DummyElm implements E { }

}
