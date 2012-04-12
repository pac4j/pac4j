/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.profile;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class creates a list of objects (buildable from JSON).
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public final class JsonList<T extends Object> extends JsonObject implements List<T>, Serializable {
    
    private static final long serialVersionUID = -2308482062004321664L;
    
    private static final Logger logger = LoggerFactory.getLogger(JsonList.class);
    
    private List<T> list = new ArrayList<T>();
    
    private Class<T> clazz;
    
    /**
     * Create a list of JsonObject from various inputs.
     * 
     * @param o
     * @param clazz
     */
    public JsonList(Object o, Class<T> clazz) {
        super(null);
        this.clazz = clazz;
        if (o instanceof List) {
            List<String> elements = (List<String>) o;
            for (String element : elements) {
                // expect JSON element : "x"
                if (clazz == String.class && element != null && !element.startsWith("\"")) {
                    element = "\"" + element + "\"";
                }
                buildSingleNode(JsonHelper.getFirstNode(element));
            }
        } else {
            // text is String
            if (o instanceof String && o != null) {
                String s = (String) o;
                // expect array of String ["x", "y",...
                if (clazz == String.class) {
                    if (!s.startsWith("[")) {
                        if (!s.startsWith("\"")) {
                            s = "\"" + s + "\"";
                        }
                        s = "[" + s + "]";
                    }
                }
                // expect array of objects [ {...}, {...}, ...
                else if (JsonObject.class.isAssignableFrom(clazz)) {
                    if (!s.startsWith("[")) {
                        s = "[" + s + "]";
                    }
                }
                o = s;
            }
            buildFrom(o);
        }
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        if (json != null) {
            Iterator<JsonNode> jsonIterator = json.getElements();
            while (jsonIterator.hasNext()) {
                JsonNode node = jsonIterator.next();
                buildSingleNode(node);
            }
        }
    }
    
    /**
     * Add a single node to the JsonList.
     * 
     * @param node
     */
    private void buildSingleNode(JsonNode node) {
        if (clazz == String.class) {
            list.add((T) node.getTextValue());
        } else if (JsonObject.class.isAssignableFrom(clazz)) {
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor(Object.class);
                T jsonObject = constructor.newInstance(node);
                list.add(jsonObject);
            } catch (Exception e) {
                logger.error("Cannot build object", e);
            }
        }
    }
    
    public boolean add(T e) {
        return list.add(e);
    }
    
    public void add(int index, T element) {
        list.add(index, element);
    }
    
    public boolean addAll(Collection<? extends T> c) {
        return list.addAll(c);
    }
    
    public boolean addAll(int index, Collection<? extends T> c) {
        return list.addAll(index, c);
    }
    
    public void clear() {
        list.clear();
        
    }
    
    public boolean contains(Object o) {
        return list.contains(o);
    }
    
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }
    
    public T get(int index) {
        return list.get(index);
    }
    
    public int indexOf(Object o) {
        return list.indexOf(o);
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public Iterator<T> iterator() {
        return list.iterator();
    }
    
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }
    
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }
    
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }
    
    public boolean remove(Object o) {
        return list.remove(o);
    }
    
    public T remove(int index) {
        return list.remove(index);
    }
    
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }
    
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }
    
    public T set(int index, T element) {
        return list.set(index, element);
    }
    
    public int size() {
        return list.size();
    }
    
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
    
    public Object[] toArray() {
        return list.toArray();
    }
    
    @SuppressWarnings("hiding")
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }
}
