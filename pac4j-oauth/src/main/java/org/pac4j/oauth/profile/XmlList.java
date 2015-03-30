/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oauth.profile;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class creates a list of objects (buildable from XML).
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public final class XmlList<T> extends XmlObject implements List<T> {
    
    private static final long serialVersionUID = -3439014616371376590L;
    
    private static final Logger logger = LoggerFactory.getLogger(XmlList.class);
    
    private List<T> list = new ArrayList<T>();
    
    private Class<T> clazz;
    
    public XmlList() {
    }
    
    public void setList(final List<T> list) {
        this.list = list;
    }
    
    public void setClazz(final Class<T> clazz) {
        this.clazz = clazz;
    }
    
    /**
     * Create a list of XmlObject from a XML text.
     * 
     * @param xml xml
     * @param clazz class
     */
    public XmlList(final String xml, final Class<T> clazz) {
        this.clazz = clazz;
        buildFrom(xml);
    }
    
    @Override
    protected void buildFromXml(final String xml) {
        if (xml != null) {
            int start = xml.indexOf("<");
            if (start >= 0) {
                int end = xml.indexOf(">", start);
                if (end > start) {
                    String tag = xml.substring(start + 1, end);
                    int pos = 0;
                    XmlMatch match = XmlHelper.get(xml, tag, pos);
                    String element = match.getText();
                    pos = match.getPos() + 1;
                    while (element != null) {
                        buildSingleNode(element);
                        match = XmlHelper.get(xml, tag, pos);
                        if (match == null) {
                            break;
                        }
                        element = match.getText();
                        pos = match.getPos() + 1;
                    }
                }
            }
        }
    }
    
    /**
     * Add a single node to the XmlList.
     * 
     * @param xml xml
     */
    @SuppressWarnings("unchecked")
    private void buildSingleNode(final String xml) {
        if (this.clazz == String.class) {
            this.list.add((T) xml);
        } else if (XmlObject.class.isAssignableFrom(this.clazz)) {
            try {
                final Constructor<T> constructor = this.clazz.getDeclaredConstructor();
                final T object = constructor.newInstance();
                ((XmlObject) object).buildFrom(xml);
                this.list.add(object);
            } catch (final Exception e) {
                logger.error("Cannot build object", e);
            }
        }
    }
    
    public boolean add(final T e) {
        return this.list.add(e);
    }
    
    public void add(final int index, final T element) {
        this.list.add(index, element);
    }
    
    public boolean addAll(final Collection<? extends T> c) {
        return this.list.addAll(c);
    }
    
    public boolean addAll(final int index, final Collection<? extends T> c) {
        return this.list.addAll(index, c);
    }
    
    public void clear() {
        this.list.clear();
        
    }
    
    public boolean contains(final Object o) {
        return this.list.contains(o);
    }
    
    public boolean containsAll(final Collection<?> c) {
        return this.list.containsAll(c);
    }
    
    public T get(final int index) {
        return this.list.get(index);
    }
    
    public int indexOf(final Object o) {
        return this.list.indexOf(o);
    }
    
    public boolean isEmpty() {
        return this.list.isEmpty();
    }
    
    public Iterator<T> iterator() {
        return this.list.iterator();
    }
    
    public int lastIndexOf(final Object o) {
        return this.list.lastIndexOf(o);
    }
    
    public ListIterator<T> listIterator() {
        return this.list.listIterator();
    }
    
    public ListIterator<T> listIterator(final int index) {
        return this.list.listIterator(index);
    }
    
    public boolean remove(final Object o) {
        return this.list.remove(o);
    }
    
    public T remove(final int index) {
        return this.list.remove(index);
    }
    
    public boolean removeAll(final Collection<?> c) {
        return this.list.removeAll(c);
    }
    
    public boolean retainAll(final Collection<?> c) {
        return this.list.retainAll(c);
    }
    
    public T set(final int index, final T element) {
        return this.list.set(index, element);
    }
    
    public int size() {
        return this.list.size();
    }
    
    public List<T> subList(final int fromIndex, final int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }
    
    public Object[] toArray() {
        return this.list.toArray();
    }
    
    @SuppressWarnings("hiding")
    public <T> T[] toArray(final T[] a) {
        return this.list.toArray(a);
    }
}
