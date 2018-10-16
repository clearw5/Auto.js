package com.stardust.autojs.rhino;

import android.os.Build;
import android.support.annotation.RequiresApi;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class NativeArrayLikeJavaObject extends NativeJavaObject implements List {

    private final NativeArray mArray;

    public NativeArrayLikeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType, NativeArray array) {
        super(scope, javaObject, staticType);
        mArray = array;
        setPrototype(mArray);
    }

    public NativeArrayLikeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType, boolean isAdapter, NativeArray array) {
        super(scope, javaObject, staticType, isAdapter);
        mArray = array;
        setPrototype(mArray);
    }

    public NativeArrayLikeJavaObject(NativeArray array) {
        mArray = array;
        setPrototype(mArray);
    }


    @Override
    public boolean has(int index, Scriptable start) {
        return mArray.has(index, start);
    }

    @Override
    public Object get(int index, Scriptable start) {
        return mArray.get(index, start);
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        mArray.put(index, start, value);
    }

    @Override
    public boolean contains(Object o) {
        return mArray.contains(o);
    }


    @Override
    public Object[] toArray() {
        return mArray.toArray();
    }

    @Override
    public boolean add(Object o) {
        return mArray.add(o);
    }

    @Override
    public Object[] toArray(Object[] a) {
        return mArray.toArray(a);
    }

    @Override
    public boolean containsAll(Collection c) {
        return mArray.containsAll(c);
    }

    @Override
    public int size() {
        return mArray.size();
    }

    @Override
    public boolean isEmpty() {
        return mArray.isEmpty();
    }

    @Override
    public Object get(int index) {
        return mArray.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return mArray.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return mArray.lastIndexOf(o);
    }


    @Override
    public Iterator iterator() {
        return mArray.iterator();
    }


    @Override
    public ListIterator listIterator() {
        return mArray.listIterator();
    }


    @Override
    public ListIterator listIterator(int start) {
        return mArray.listIterator(start);
    }

    @Override
    public boolean remove(Object o) {
        return mArray.remove(o);
    }

    @Override
    public boolean addAll(Collection c) {
        return mArray.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return mArray.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return mArray.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        return mArray.retainAll(c);
    }

    @Override
    public Object set(int index, Object element) {
        return mArray.set(index, element);
    }

    @Override
    public void add(int index, Object element) {
        mArray.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return mArray.remove(index);
    }


    @Override
    public List subList(int fromIndex, int toIndex) {
        return mArray.subList(fromIndex, toIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void replaceAll(UnaryOperator operator) {
        mArray.replaceAll(operator);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sort(Comparator c) {
        mArray.sort(c);
    }

    @Override
    public void clear() {
        mArray.clear();
    }

    @Override
    public boolean equals(Object o) {
        return mArray.equals(o);
    }

    @Override
    public int hashCode() {
        return mArray.hashCode();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Spliterator spliterator() {
        return mArray.spliterator();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean removeIf(Predicate filter) {
        return mArray.removeIf(filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Stream stream() {
        return mArray.stream();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Stream parallelStream() {
        return mArray.parallelStream();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void forEach(Consumer action) {
        mArray.forEach(action);
    }
}
